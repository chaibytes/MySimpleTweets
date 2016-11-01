package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {

    EditText etComposeTweet;
    ImageView ivProfileImage;
    TextView tvUserHandle;
    TextView tvCount;

    Button btCancel;
    Button btTweet;
    private TwitterClient client;

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
                // Post a tweet
                String tweetStr = etComposeTweet.getText().toString();
                client.postTweet(tweetStr, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "Successfully created a new tweet!");
                        Tweet tweet = Tweet.fromJSONObject(response);
                        Intent data = new Intent();
                        data.putExtra("tweet", tweet);
                        // Return back result OK and the intent Tweet object
                        setResult(RESULT_OK, data);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("DEBUG", "Error creating tweet!");
                    }
                });


            }
        });
        etComposeTweet = (EditText) findViewById(R.id.etComposeTweet);
        etComposeTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCount.setText(String.valueOf(140 - s.length()));
                if (s.length() > 140) {
                    tvCount.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 140) {
                    tvCount.setTextColor(Color.BLACK);
                }
            }
        });
    }

    private void polulateViews() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserHandle = (TextView) findViewById(R.id.tvUserHandle);
        User user = (User) getIntent().getParcelableExtra("user");
        tvUserHandle.setText(String.format("@%s", user.getScreenName()));
        Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvCount = (TextView) findViewById(R.id.tvCountChars);
    }

}
