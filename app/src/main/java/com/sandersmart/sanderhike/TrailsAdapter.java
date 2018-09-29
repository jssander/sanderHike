package com.sandersmart.sanderhike;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jeffrey Sander on 8/12/2017.
 */

public class TrailsAdapter extends RecyclerView.Adapter<TrailsAdapter.TrailViewHolder> {
    private String[] mDataset;
    private ArrayList<String> mTrails = new ArrayList<>();
    private static ViewTrailsActivity mActivity;

    public static int[] markerIcons = {R.mipmap.ic_black_circle, R.mipmap.ic_red_circle, R.mipmap.ic_blue_circle,
            R.mipmap.ic_green_circle, R.mipmap.ic_yellow_circle,
            R.mipmap.ic_violet_circle, R.mipmap.ic_orange_circle, R.mipmap.ic_white_circle};

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public CardView mCardView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
        public ViewHolder(CardView v, TextView tv) {
            super(v);
            mCardView = v;
            mTextView = tv;
        }
        public ViewHolder(CardView cv) {
            super(cv);
            mCardView = cv;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TrailsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    public TrailsAdapter(ArrayList<String> trails, ViewTrailsActivity activity) {
        mTrails = trails;
        mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrailsAdapter.TrailViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trail_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters


        TrailViewHolder tvh = new TrailViewHolder(cv);
        return tvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TrailViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        //holder.mCardView.setCardElevation(10);
        String trailName = mTrails.get(position);
        String trailPath = mActivity.getTrailPath(trailName);
        holder.mTrail = Trail.loadTrailFromFile(trailPath);
        holder.mTrailName.setText(trailName);
        holder.mTrailDistance.setText(Integer.toString((int)holder.mTrail.mDistance));
        holder.mLoadButton.setTag(trailName);
        holder.mDeleteButton.setTag(trailName);
        holder.mColors = Trail.getTrailColors(holder.mTrail);

        ArrayList<Integer> colorResources = new ArrayList<>();
        for(int i = 0; i < holder.mColors.length; i++) {
            if(holder.mColors[i])
                colorResources.add(markerIcons[i]);
        }


        holder.mColorsRecyclerView.setAdapter(new ColorListAdapter(colorResources));
        holder.mColorsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));

//        LinearLayoutManager colorsLayoutManager = (LinearLayoutManager) holder.mColorsRecyclerView.getLayoutManager();
//        for(int i = 0; i < holder.mColors.length; i++) {
//            if(holder.mColors[i] == true) {
//                ImageView im = new ImageView(holder.mColorsRecyclerView.getContext());
//
//                colorsLayoutManager.addView(im);
//            }
//        }
        Log.d("onBindViewHolder", "" + position);
    }

    private class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.ColorViewHolder> {
        ArrayList<Integer> mColorResources;
        public ColorListAdapter(ArrayList<Integer> colorResources) {
            mColorResources = colorResources;
        }
        public ColorListAdapter.ColorViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {

//            CardView cv = (CardView) LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.trail_card_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ImageView imageView = new ImageView(mActivity.getApplicationContext());

            ColorViewHolder cvh = new ColorViewHolder(imageView);
            return cvh;
        }

        public void onBindViewHolder(ColorViewHolder holder, int position) {
            int bitmapWidth = 45;
            int bitmapHeight = 45;
            Bitmap colorBitmap = BitmapFactory.decodeResource(mActivity.getResources(), mColorResources.get(position));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(colorBitmap, bitmapWidth, bitmapHeight, false);
            holder.imageView.setImageBitmap(resizedBitmap);
        }

        public int getItemCount() { return mColorResources.size(); }

        public class ColorViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ColorViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }

    public class TrailViewHolder extends RecyclerView.ViewHolder {
        Trail mTrail;
        TextView mTrailName;
        TextView mTrailDistance;
        Button mLoadButton;
        Button mDeleteButton;
        RecyclerView mColorsRecyclerView;
        boolean[] mColors;

        TrailViewHolder(View itemView) {
            super(itemView);
            mTrailName = (TextView) itemView.findViewById(R.id.card_trail_name);
            mTrailDistance = (TextView) itemView.findViewById(R.id.card_trail_distance);
            mLoadButton = (Button) itemView.findViewById(R.id.trail_load_button);
            mLoadButton.setOnClickListener(loadOnClickListener);
            mDeleteButton = (Button) itemView.findViewById(R.id.trail_delete_button);
            mDeleteButton.setOnClickListener(deleteOnClickListener);

            mColorsRecyclerView = (RecyclerView) itemView.findViewById(R.id.trail_colors_recycler_view);
            LinearLayoutManager layoutManager= new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            mColorsRecyclerView.setLayoutManager(layoutManager);
        }

        View.OnClickListener loadOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trailString = (String)v.getTag();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("trailToLoad", trailString);
                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                mActivity.finish();
            }
        };

        View.OnClickListener deleteOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trailString = (String)v.getTag();
                deleteTrailWithPrompt(trailString);
            }
        };

        private void deleteTrailWithPrompt(final String trailString) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Would you like to delete " + trailString + "?");

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String trailPath = mActivity.getTrailPath(trailString);
                    File file = new File(trailPath);
                    file.delete();
                    int position = mTrails.indexOf(trailString);
                    mTrails.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mTrails.size());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTrails.size();
    }
}