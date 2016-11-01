package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {

    EditText etComposeTweet;
    ImageView ivProfileImage;
    TextView tvUserHandle;

    Button btCancel;
    Button btTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        setOnClickHandlers();
        polulateViews();
        client = TwitterApplication.getRestClient();
    }

    private void setOnClickHandlers() {
        btCancel = (Button) findViewById(R.id.btCancel);
        btTweet = (Button) findViewById(R.id.btTweet);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To-do: Post a tweet
                postTweet();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Return back result OK to refresh the results
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void postTweet() {
        String tweet = etComposeTweet.getText().toString();
        client.postTweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "Successfully created a new tweet!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Error creating tweet!");
            }
        });
    }

    private void polulateViews() {
        etComposeTweet = (EditText) findViewById(R.id.etComposeTweet);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserHandle = (TextView) findViewById(R.id.tvUserHandle);
        User user = (User) getIntent().getParcelableExtra("user");
        tvUserHandle.setText(String.format("@%s", user.getScreenName()));
        Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

}
