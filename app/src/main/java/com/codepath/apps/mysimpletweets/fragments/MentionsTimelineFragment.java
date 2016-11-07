package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.functionality.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragments{

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the client
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline(0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        getListView().setOnScrollListener(scrollListener);
        return v;
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating tweet objects from the json
    public void populateTimeline(long since_id, long max_id) {
        client.getMentionsTimeline(since_id, max_id, new JsonHttpResponseHandler() {
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
                    addAll(tweetArrayListarray);

                    // Load the Model data into ListView
                    getAdapter().notifyDataSetChanged();
                    final long maxID = getAdapter().getItem(getAdapter().getCount() - 1).getUid() - 1;
                    final long sinceID = getAdapter().getItem(0).getUid();
                    scrollListener.onLoadFinish(sinceID, maxID, getAdapter().getCount());
                    if (getAdapter().getCount() <= COUNT) {
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
        //swipeRefreshLayout.setRefreshing(false);
    }
}
