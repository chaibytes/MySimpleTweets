package com.codepath.apps.mysimpletweets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class NetworkAlertFragment extends DialogFragment{

    public static NetworkAlertFragment newInstance(String msg) {
        NetworkAlertFragment frag = new NetworkAlertFragment();
        Bundle args = new Bundle();
        args.putString("message", msg);
        frag.setArguments(args);
        return frag;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Network Alert");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return alertDialogBuilder.create();
    }
}
