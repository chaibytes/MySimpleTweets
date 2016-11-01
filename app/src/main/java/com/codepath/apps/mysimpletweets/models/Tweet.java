package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

/**
 * Created by Pragyan on 10/26/16.
 */

// Parse the JSON, Store the data, encapsulate state logic or display logic
public class Tweet {
    // list out the attributes
    private String body;
    private long uid; // unique id for the tweet (DB id)
    private User user; // store embedded user object
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }


    public User getUser() {
        return user;
    }

    public long getUid() {
        return uid;
    }


    // Deserialize the JSON and build tweet objects
    // Tweet.fromJSONObject("{...}") ==> <Tweet>

    public static Tweet fromJSONObject(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        // Extract the values from the json, store them

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = Tweet.getRelativeTimeAlgo(jsonObject.getString("created_at"));
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            // tweet.user = ...;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Return the tweet object
        return tweet;
    }

    // Tweet.fromJSONArray([{...}, {...}, ...]) ==> List<Tweet>
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        // Iterate the json array and create tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSONObject(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        // Return the finished list
        return tweets;
    }

    // Convert created_at: "Tue Aug 28 21:16:23 +0000 2012" to relative time
    private static String getRelativeTimeAlgo(String rawJsonDate) {
        String twitterFormattedString = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormattedString, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(),
                    DateUtils.FORMAT_NUMERIC_DATE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] array = relativeDate.split(" ");
        // Only the numeric value + the first char
        if (array[2].contains("ago")) {
            return (array[0] + array[1].substring(0, 1));
        }
        return relativeDate;
    }
}
