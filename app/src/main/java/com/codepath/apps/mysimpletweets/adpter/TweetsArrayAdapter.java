package com.codepath.apps.mysimpletweets.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pragyan on 10/27/16.
 */

// Taking the Tweets objects and turing them into views
// to be displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvBody;
        TextView tvCreatedAt;
        TextView tvScreenName;
    }
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    // Override and setup custom template
    // ToDo: Use the viewholder pattern

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the tweet
        Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        // 2. Find or inflate the template
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            // 3. Find the subviews to fill with data in the template
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 4. Populate data into the subviews
        viewHolder.tvUserName.setText(String.format("@%s", tweet.getUser().getScreenName()));
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
        viewHolder.tvCreatedAt.setText(tweet.getCreatedAt());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
        // 5. Return the view to be inserted into the list
        return convertView;

    }
}
