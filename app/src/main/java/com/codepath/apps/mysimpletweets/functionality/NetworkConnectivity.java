package com.codepath.apps.mysimpletweets.functionality;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.mysimpletweets.fragments.NetworkAlertFragment;

import java.io.IOException;

public class NetworkConnectivity {
    private Context context;
    private FragmentManager fragmentManager;
    public NetworkConnectivity(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    public boolean isNetworkConnected() {
        return isNetworkAvailable() && isOnline();
    }
    // Check if internet is available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    // Checking if actual internet is connected
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {e.printStackTrace();}
        catch (InterruptedException e) {e.printStackTrace();}
        return false;
    }

    public void showAlertDialog() {
        NetworkAlertFragment alertDialog = NetworkAlertFragment.newInstance("Please check your network settings");
        alertDialog.show(fragmentManager, "fragment_alert");
    }
}
