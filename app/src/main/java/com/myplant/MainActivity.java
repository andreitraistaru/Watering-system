package com.myplant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView airHumidity, airTemperature, soilHumidity, status;

        airHumidity = findViewById(R.id.airHumidity_main_activity);
        airTemperature = findViewById(R.id.airTemperature_main_activ);
        soilHumidity = findViewById(R.id.soilHumidity_main_activity);
        status = findViewById(R.id.status_main_activ);

        airHumidity.setText(getResources().getText(R.string.unavailable));
        airTemperature.setText(getResources().getText(R.string.unavailable));
        soilHumidity.setText(getResources().getText(R.string.unavailable));
        status.setText(getResources().getText(R.string.unavailable));

        requestData(null);
    }

    public void requestData(View view) {
        FrameLayout frameLayout = findViewById(R.id.frameLayout_main_activity);
        ProgressBar progressBar = findViewById(R.id.progressBar_main_activity);
        TextView textView = findViewById(R.id.progressBar_text_main_activity);
        ImageView imageView = findViewById(R.id.imageViewStatusIcon_main_activity);

        frameLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(getResources().getString(R.string.progress_bar));
        imageView.setVisibility(View.INVISIBLE);

        new DataRetriever(this).start();
    }

    public void sendWateringRequest(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.water_title_dialog))
                .setMessage(getResources().getString(R.string.water_message_dialog))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FrameLayout frameLayout = findViewById(R.id.frameLayout_main_activity);
                        ProgressBar progressBar = findViewById(R.id.progressBar_main_activity);
                        TextView textView = findViewById(R.id.progressBar_text_main_activity);
                        ImageView imageView = findViewById(R.id.imageViewStatusIcon_main_activity);

                        frameLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        textView.setText(getResources().getString(R.string.progress_bar_watering));
                        imageView.setVisibility(View.INVISIBLE);

                        new RequestSender(MainActivity.this).start();
                    }})
                .setNegativeButton(android.R.string.no, null).create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        }

        dialog.show();
    }
}