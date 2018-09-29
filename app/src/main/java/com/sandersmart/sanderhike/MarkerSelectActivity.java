package com.sandersmart.sanderhike;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MarkerSelectActivity extends AppCompatActivity {

    int numMarkers = 8;
    boolean[] markers = new boolean[numMarkers];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_select);

        Button buttonDone = (Button) findViewById(R.id.buttonDone);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("markers", markers);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        final ImageView markerBlack = (ImageView) findViewById(R.id.imageBlack);
        final ImageView markerRed = (ImageView) findViewById(R.id.imageRed);
        final ImageView markerBlue = (ImageView) findViewById(R.id.imageBlue);
        final ImageView markerGreen = (ImageView) findViewById(R.id.imageGreen);
        final ImageView markerYellow = (ImageView) findViewById(R.id.imageYellow);
        final ImageView markerOrange = (ImageView) findViewById(R.id.imageOrange);
        final ImageView markerViolet = (ImageView) findViewById(R.id.imageViolet);
        final ImageView markerWhite = (ImageView) findViewById(R.id.imageWhite);

        markerBlack.setBackgroundColor(Color.WHITE);
        markerRed.setBackgroundColor(Color.LTGRAY);
        markerBlue.setBackgroundColor(Color.WHITE);
        markerGreen.setBackgroundColor(Color.LTGRAY);
        markerYellow.setBackgroundColor(Color.WHITE);
        markerViolet.setBackgroundColor(Color.LTGRAY);
        markerOrange.setBackgroundColor(Color.WHITE);
        markerWhite.setBackgroundColor(Color.LTGRAY);

        markerBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[0] = !markers[0];
                if(markers[0] == true) {
                    markerBlack.setBackgroundColor(Color.GREEN);
                } else {
                    markerBlack.setBackgroundColor(Color.WHITE);
                }
            }
        });

        markerRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[1] = !markers[1];
                if(markers[1] == true) {
                    markerRed.setBackgroundColor(Color.GREEN);
                } else {
                    markerRed.setBackgroundColor(Color.LTGRAY);
                }
            }
        });

        markerBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[2] = !markers[2];
                if(markers[2] == true) {
                    markerBlue.setBackgroundColor(Color.GREEN);
                } else {
                    markerBlue.setBackgroundColor(Color.WHITE);
                }
            }
        });

        markerGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[3] = !markers[3];
                if(markers[3] == true) {
                    markerGreen.setBackgroundColor(Color.GREEN);
                } else {
                    markerGreen.setBackgroundColor(Color.LTGRAY);
                }
            }
        });

        markerYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[4] = !markers[4];
                if(markers[4] == true) {
                    markerYellow.setBackgroundColor(Color.GREEN);
                } else {
                    markerYellow.setBackgroundColor(Color.WHITE);
                }
            }
        });

        markerViolet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[5] = !markers[5];
                if(markers[5] == true) {
                    markerViolet.setBackgroundColor(Color.GREEN);
                } else {
                    markerViolet.setBackgroundColor(Color.LTGRAY);
                }
            }
        });

        markerOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[6] = !markers[6];
                if(markers[6] == true) {
                    markerOrange.setBackgroundColor(Color.GREEN);
                } else {
                    markerOrange.setBackgroundColor(Color.WHITE);
                }
            }
        });

        markerWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markers[7] = !markers[7];
                if(markers[7] == true) {
                    markerWhite.setBackgroundColor(Color.GREEN);
                } else {
                    markerWhite.setBackgroundColor(Color.LTGRAY);
                }
            }
        });


    }
}
