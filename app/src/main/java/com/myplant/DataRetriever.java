package com.myplant;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myplant.history.DataReading;
import com.myplant.history.HistoryClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class DataRetriever extends Thread {
    private URL url;
    private Activity activity;

    public DataRetriever(Activity activity) {
        this.activity = activity;

        try {
            this.url = new URL(Utils.getServerProtocol(), Utils.getServerIp(), Utils.getServerPort(), Utils.getServerFileInfo());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        HttpURLConnection connection;
        final String response;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                response = readStream(connection.getInputStream());
                final JSONObject jsonResponse;

                try {
                    jsonResponse = new JSONObject(response);
                } catch (JSONException e) {
                    handleError();

                    return;
                }

                final int airHumidityData, soilHumidityData, airTemperatureData;
                final String statusData;

                try {
                    airHumidityData = jsonResponse.getInt(activity.getResources().getString(R.string.air_humidity_json));
                    soilHumidityData = Utils.getHumidityPercentage(jsonResponse.getInt(activity.getResources().getString(R.string.soil_humidity_json)));
                    airTemperatureData = jsonResponse.getInt(activity.getResources().getString(R.string.air_temperature_json));
                    Date lastWateringDate = Utils.getStatusInfo(jsonResponse.getLong(activity.getResources().getString(R.string.status_json)));

                    if (lastWateringDate == null) {
                        statusData = activity.getResources().getString(R.string.unavailable);
                    } else {
                        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
                        statusData = dateFormat.format(lastWateringDate);
                    }
                } catch (JSONException e) {
                    handleError();

                    return;
                }

                final FrameLayout frameLayout = activity.findViewById(R.id.frameLayout_main_activity);
                final ImageView imageView = activity.findViewById(R.id.imageViewStatusIcon_main_activity);
                final TextView airHumidity = activity.findViewById(R.id.airHumidity_main_activity);
                final TextView soilHumidity = activity.findViewById(R.id.soilHumidity_main_activity);
                final TextView airTemperature = activity.findViewById(R.id.airTemperature_main_activ);
                final TextView status = activity.findViewById(R.id.status_main_activ);
                final TextView lastRefresh = activity.findViewById(R.id.refreshLastTime_main_activity);

                frameLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        frameLayout.setVisibility(View.INVISIBLE);
                    }
                });

                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (soilHumidityData > Utils.getThirstyPlantThreshold()) {
                            imageView.setImageResource(R.drawable.happy_plant);
                        } else if (soilHumidityData < Utils.getDriedPlantThreshold()) {
                            imageView.setImageResource(R.drawable.dried_plant);
                        } else {
                            imageView.setImageResource(R.drawable.thirsty_plant);
                        }

                        imageView.setVisibility(View.VISIBLE);
                    }
                });

                soilHumidity.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = String.valueOf(activity.getResources().getText(R.string.humidity_first)) + " " +
                                soilHumidityData + activity.getResources().getText(R.string.humidity_second);

                        soilHumidity.setText(message);
                    }
                });

                airHumidity.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = String.valueOf(activity.getResources().getText(R.string.humidity_first)) + " " +
                                airHumidityData + activity.getResources().getText(R.string.humidity_second);

                        airHumidity.setText(message);
                    }
                });

                airTemperature.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = String.valueOf(activity.getResources().getText(R.string.temperature_first)) + " " +
                                airTemperatureData + activity.getResources().getText(R.string.temperature_second);

                        airTemperature.setText(message);
                    }
                });

                status.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = activity.getResources().getText(R.string.status) + "\n" + statusData;

                        status.setText(message);
                    }
                });

                lastRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

                        message = activity.getResources().getString(R.string.last_refresh) + "\n" + dateFormat.format(new Date().getTime());

                        lastRefresh.setText(message);
                    }
                });

                long now = new Date().getTime();
                String readingTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(now);

                new AddToHistory(new DataReading(now, readingTime, airHumidityData, airTemperatureData, soilHumidityData)).start();
            }
        } catch (IOException e) {
            handleError();
        }
    }

    class AddToHistory extends Thread {
        private DataReading dataReading;

        public AddToHistory(DataReading dataReading) {
            this.dataReading = dataReading;
        }

        @Override
        public void run() {
            List<DataReading> history = HistoryClient.getInstance(activity).getDatabase().getHistoryDAO().getAll();
            if (history.size() > Utils.getMaxHistoryCapacity()) {
                HistoryClient.getInstance(activity).getDatabase().getHistoryDAO().delete(history.get(0));
            }

            HistoryClient.getInstance(activity).getDatabase().getHistoryDAO().insert(dataReading);
        }
    }

    private void handleError() {
        final ProgressBar progressBar = activity.findViewById(R.id.progressBar_main_activity);
        final TextView textView = activity.findViewById(R.id.progressBar_text_main_activity);

        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(activity.getResources().getString(R.string.progress_bar_error));
            }
        });
    }

    private String readStream(InputStream input) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(input));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}