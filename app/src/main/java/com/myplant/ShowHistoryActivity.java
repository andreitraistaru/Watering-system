package com.myplant;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.myplant.history.DataReading;
import com.myplant.history.HistoryClient;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.List;

public class ShowHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        setTitle(getResources().getString(R.string.air_temperature_plot_title));

        XYPlot plot = findViewById(R.id.plot);

        plot.setTitle("");

        final Number[] domainLabels = new Number[1000];
        final Number[] series1Numbers = new Number[1000];

        for(int i = 0; i < 1000; i++) {
            domainLabels[i] = i;
            series1Numbers[i] = 2*i;
        }

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);

        series1Format.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(series1, series1Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));

        PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        plot.getOuterLimits().set(0, 999, 0, 1998);
        PanZoom.attach(plot);

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