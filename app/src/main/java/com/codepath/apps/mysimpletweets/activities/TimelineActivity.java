package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.os.Handler;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adpter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragments;
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

    //private SwipeRefreshLayout swipeRefreshLayout;
    private TwitterClient client;
    private User user;

    private HomeTimelineFragment homeTimelineFragment;

    private ViewPager viewPager;
    private TweetsPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        client = TwitterApplication.getRestClient();
        // Get the view pager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the view pager adapter for the pager
        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        // Find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the view pager
        tabStrip.setViewPager(viewPager);


//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                populateTimeline(0, 0);
//            }
//        });

        // Configure the refreshing colors
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
//                android.R.color.holo_green_light, android.R.color.holo_blue_bright);


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    public void onProfileView(MenuItem item) {
        // Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = data.getParcelableExtra("tweet");
            homeTimelineFragment.addNewTweet(0, tweet);
        }
    }

    // Return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String[] tabTitles = { "Home", "Mentions" };

        // Adapter gets the manager to insert or remove fragment from the activity
        public TweetsPagerAdapter(FragmentManager fm) {
           super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                homeTimelineFragment = new HomeTimelineFragment();
                return homeTimelineFragment;
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        // Returns the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // How many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
