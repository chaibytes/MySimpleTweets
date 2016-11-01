package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.os.Handler;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adpter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.functionality.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 20;
    private static final int COUNT = 30;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private User user;

    private long sinceID = 1;
    private long maxID = 1;

    Handler handler = new Handler();

    EndlessScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(0, 0);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light, android.R.color.holo_blue_bright);

        // Find the listview
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        // Create the arraylist (data source)
        tweets = new ArrayList<>();
        // Construct the adapter from data source
        aTweets = new TweetsArrayAdapter(this, tweets);
        // Connect adapter to listview
        lvTweets.setAdapter(aTweets);
        // Get the client
        client = TwitterApplication.getRestClient(); // singleton client
        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(long since_id, long max_id, int totalItemCount) {
                populateTimeline(since_id, max_id);
                return true;
            }
        };
        lvTweets.setOnScrollListener(scrollListener);
        populateTimeline(0, 0);
        getUserCredentials();

    }

    // Send an API request to get the current user's details
    // Fill the User object that is passed when the user clicks the
    // compose button
    private void getUserCredentials() {
        client.getUserCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating tweet objects from the json
    private void populateTimeline(long since_id, long max_id) {
        Log.d("DEBUG", Long.toString(since_id) + " " + Long.toString(max_id));
        client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            // Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // JSON here
                Log.d("DEBUG", json.toString());

                ArrayList<Tweet> tweetArrayListarray = null;

                tweetArrayListarray = Tweet.fromJSONArray(json);
                if (tweetArrayListarray.size() == 0) {
                    // Limit reached
                    scrollListener.limitReached();
                } else {
                    // Deserialize JSON
                    // Create Models and add them to the adapter
                    aTweets.addAll(tweetArrayListarray);
                    Log.d("DEBUG", aTweets.toString());
                    // Load the Model data into ListView
                    aTweets.notifyDataSetChanged();
                    maxID = aTweets.getItem(aTweets.getCount() - 1).getUid() - 1;
                    sinceID = aTweets.getItem(0).getUid();
                    scrollListener.onLoadFinish(sinceID, maxID, aTweets.getCount());
                    if (aTweets.getCount() <= COUNT) {
                        // API rate limit reached
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                populateTimeline(sinceID, maxID);
                            }
                        }, 3000L);
                    }
                }
            }

            // Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                scrollListener.onFailure();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    public void onComposeAction(MenuItem mi) {
        // Display the compose activity
        Intent i = new Intent(this, ComposeTweetActivity.class);
        i.putExtra("user", user);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            aTweets.clear();
            aTweets.notifyDataSetChanged();
            populateTimeline(0, 0);
        }
    }
}
