package com.myplant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Sync().start();
        return START_NOT_STICKY;
    }

    class Sync extends Thread {
        @Override
        public void run() {
            URL url;
            HttpURLConnection connection;
            final String response;

            try {
                url = new URL(Utils.getServerProtocol(), Utils.getServerIp(), Utils.getServerPort(), Utils.getServerFileInfo());
            } catch (MalformedURLException e) {
                handleError();

                return;
            }

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

                    try {
                        airHumidityData = jsonResponse.getInt(getApplicationContext().getResources().getString(R.string.air_humidity_json));
                        soilHumidityData = Utils.getHumidityPercentage(jsonResponse.getInt(getApplicationContext().getResources().getString(R.string.soil_humidity_json)));
                        airTemperatureData = jsonResponse.getInt(getApplicationContext().getResources().getString(R.string.air_temperature_json));

                    } catch (JSONException e) {
                        handleError();

                        return;
                    }

                    long now = new Date().getTime();
                    String readingTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(now);

                    new AddToHistory(getApplicationContext(), new DataReading(now, readingTime, airHumidityData, airTemperatureData, soilHumidityData)).start();
                }
            } catch (IOException e) {
            }
        }
    }

    class AddToHistory extends Thread {
        private DataReading dataReading;
        private Context context;

        public AddToHistory(Context context, DataReading dataReading) {
            this.context = context;
            this.dataReading = dataReading;
        }

        @Override
        public void run() {
            HistoryClient.getInstance(context).getDatabase().getHistoryDAO().insert(dataReading);
        }
    }

    private void handleError() {

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
