package com.myplant;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.myplant.history.DataReading;
import com.myplant.history.HistoryClient;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ShowHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        setTitle(getResources().getString(R.string.history_activity_title));
        ((TextView) findViewById(R.id.showHistory_show_history_activity)).setMovementMethod(new ScrollingMovementMethod());

        new getHistory().start();
    }

    class getHistory extends Thread {
        @Override
        public void run() {
            List<DataReading> history = HistoryClient.getInstance(getApplicationContext()).getDatabase().getHistoryDAO().getAll();
            final StringBuilder historyString = new StringBuilder();

            for (DataReading dataReading : history) {
                historyString.append(dataReading.getReadingTime()).append(" ");
                historyString.append(getApplicationContext().getResources().getString(R.string.history_air_humidity)).append(dataReading.getAirHumidity()).append(" ");
                historyString.append(getApplicationContext().getResources().getString(R.string.history_air_temperature)).append(dataReading.getAirTemperature()).append(" ");
                historyString.append(getApplicationContext().getResources().getString(R.string.history_soil_humidity)).append(dataReading.getSoilHumidity()).append("\n\n");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.showHistory_show_history_activity)).setText(historyString.toString());
                }
            });
        }
    }
}