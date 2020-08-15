package com.myplant;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestSender extends Thread {
    private MainActivity activity;
    private URL url;

    public RequestSender(MainActivity activity) {
        this.activity = activity;

        try {
            this.url = new URL(Utils.getServerProtocol(), Utils.getServerIp(), Utils.getServerPort(), Utils.getServerFileWater());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);

            try {
                sleep(Utils.getWateringTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar progressBar = activity.findViewById(R.id.progressBar_main_activity);
                        TextView textView = activity.findViewById(R.id.progressBar_text_main_activity);

                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setText(activity.getResources().getString(R.string.progress_bar_watering_success));

                        Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.watering_success), Snackbar.LENGTH_LONG)
                                .setBackgroundTint(activity.getResources().getColor(R.color.colorAccent)).show();
                    }
                });

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.requestData(null);
                    }
                });

                try {
                    sleep(Utils.getWateringTimeResult());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout frameLayout = activity.findViewById(R.id.frameLayout_main_activity);
                        ImageView imageView = activity.findViewById(R.id.imageViewStatusIcon_main_activity);

                        frameLayout.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                handleFailure();
            }
        } catch (IOException e) {
            handleFailure();
        }
    }

    private void handleFailure() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = activity.findViewById(R.id.progressBar_main_activity);
                TextView textView = activity.findViewById(R.id.progressBar_text_main_activity);

                progressBar.setVisibility(View.INVISIBLE);
                textView.setText(activity.getResources().getString(R.string.progress_bar_watering_failure));

                Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.watering_failure), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(activity.getResources().getColor(R.color.colorAccent)).show();
            }
        });

        try {
            sleep(Utils.getWateringTimeResult());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout frameLayout = activity.findViewById(R.id.frameLayout_main_activity);
                ImageView imageView = activity.findViewById(R.id.imageViewStatusIcon_main_activity);

                frameLayout.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }
}
