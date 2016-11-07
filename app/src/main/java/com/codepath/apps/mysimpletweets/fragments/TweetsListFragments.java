package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.adpter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.functionality.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragments extends Fragment{
    protected TweetsArrayAdapter aTweets;
    protected ArrayList<Tweet> tweets;
    protected ListView lvTweets;

    EndlessScrollListener scrollListener;


    private long sinceID = 1;
    private long maxID = 1;

    Handler handler = new Handler();

    public static final int COUNT = 30;
    // inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, parent, false);
        // Find the listview
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        // Connect adapter to listview
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(scrollListener);
        return v;
    }


    // creation lifecycle

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the arraylist (data source)
        tweets = new ArrayList<>();
        // Construct the adapter from data source
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        aTweets.setCustomObjectListener(new TweetsArrayAdapter.ProfileImageClickListner() {
            @Override
            public void onProfileImageClick(String screenName) {
                // TODO: Open the ProfileActivity
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra("screen_name", screenName);
                startActivity(i);
            }
        });
        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(long since_id, long max_id, int totalItemCount) {
                populateTimeline(since_id, max_id);
                return true;
            }
        };

    }

    protected abstract void populateTimeline(long since_id, long max_id);

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public TweetsArrayAdapter getAdapter() {
        return aTweets;
    }

    public ListView getListView() {
        return lvTweets;
    }

    public ArrayList<Tweet> getListOfTweets() {
        return tweets;
    }

    public void addNewTweet(int position, Tweet tweet) {
        // Populate the tweets first
        tweets.add(position, tweet);
        aTweets.notifyDataSetChanged();
    }
}
