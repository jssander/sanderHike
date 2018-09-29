package com.sandersmart.sanderhike;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.File;
import java.util.ArrayList;

public class ViewTrailsActivity extends AppCompatActivity {
    public RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String[] myDataset = {"Trail A", "Trail B", "Trail C"};
    ArrayList<String> trails = new ArrayList<>();

    public String getTrailPath(String trailname) {
        return getFilesDir()+File.separator+"Trails"+File.separator+trailname;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trails);
        mRecyclerView = (RecyclerView) findViewById(R.id.trails_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        String path = getFilesDir().getPath() + File.separator + "Trails";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files != null) {
            Log.d("Files", "Size: " + files.length);

            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                trails.add(files[i].getName());
            }
        }

        // specify an adapter (see also next example)
        mAdapter = new TrailsAdapter(trails, this);
        mRecyclerView.setAdapter(mAdapter);


    }
}
