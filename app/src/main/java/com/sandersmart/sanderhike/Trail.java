package com.sandersmart.sanderhike;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jeffrey Sander on 8/17/2017.
 */

public class Trail {
    public ArrayList<MapsActivity.TrailPoint> mTrailPoints = new ArrayList<>();
    public double mDistance = 0;

    public static boolean[] getTrailColors(Trail trail) {
        if(trail.mTrailPoints.size() == 0)
            return new boolean[0];
        boolean[] colors = new boolean[trail.mTrailPoints.get(0).mColors.length];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = false;
        }

        for(MapsActivity.TrailPoint tp : trail.mTrailPoints) {
            for(int i = 0; i < tp.mColors.length; i++) {
                if(tp.mColors[i] == true) {
                    colors[i] = true;
                }
            }
        }

        return colors;
    }

    public static Trail loadTrailFromFile(String filepath) {
        Trail trail = new Trail();

        File file = new File(filepath);

        Log.d("loadDataFromFile", "loading data...");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Log.d("loadDataFromFile", line);
                String[] parts = line.split("/");
                double lat = Double.parseDouble(parts[0]);
                double lon = Double.parseDouble(parts[1]);
                int degree = Integer.parseInt(parts[2]);
                boolean[] mColors = MapsActivity.getBooleanFromString(parts[3]);
                LatLng ll = new LatLng(lat, lon);
                MapsActivity.TrailPoint tp = new MapsActivity.TrailPoint(ll, degree);
                tp.mColors = mColors;
                trail.mTrailPoints.add(tp);

                //addPoint(ll, "Blue Trail", degree, mColors);

            }
            trail.mDistance = MapsActivity.getTotalDistanceFromTrailPoints(trail.mTrailPoints);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return trail;
    }
}
