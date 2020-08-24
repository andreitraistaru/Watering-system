package com.myplant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.myplant.history.DataReading;
import com.myplant.history.HistoryClient;

import java.util.List;

public class ShowHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        new getHistory().start();
    }

    class getHistory extends Thread {
        @Override
        public void run() {
            List<DataReading> history = HistoryClient.getInstance(getApplicationContext()).getDatabase().getHistoryDAO().getAll();

            final StringBuilder message = new StringBuilder();

            for (DataReading dataReading : history) {
                message.append(dataReading.getReadingTime()).append(" ");
                message.append(dataReading.getAirHumidity()).append(" ");
                message.append(dataReading.getAirTemperature()).append(" ");
                message.append(dataReading.getSoilHumidity()).append("\n");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = findViewById(R.id.textView);

                    textView.setText(message);
                }
            });
        }
    }
}