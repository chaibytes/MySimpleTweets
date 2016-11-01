package com.codepath.apps.mysimpletweets.functionality;

import android.widget.AbsListView;

/**
 * Created by Pragyan on 10/28/16.
 */

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 5;
    private long since_id = 1;
    private long max_id = 1;
    private int previousTotalItemsCount = 0;
    private boolean loading = true;
    private long starting_id = 1;
    private boolean limitReached = false;

    public EndlessScrollListener() {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        // Do nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount < previousTotalItemsCount) {
            this.since_id = this.starting_id;
            this.max_id = this.starting_id;
            this.previousTotalItemsCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemsCount)) {
            loading = false;
            previousTotalItemsCount = totalItemCount;
        }

        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold >= totalItemCount)) {
            if (!limitReached) {
                loading = onLoadMore(since_id, max_id, totalItemCount);
            }
        }
    }

    public abstract boolean onLoadMore(long since_id, long max_id, int totalItemCount);

    public void onLoadFinish(long since_id, long max_id, int totalItemsCount) {
        loading = false;
        previousTotalItemsCount = totalItemsCount;
        this.since_id = since_id;
        this.max_id = max_id;
    }

    public void onFailure() {
        loading = false;
    }

    public void limitReached() {
        limitReached = true;
        loading = false;
    }


}
