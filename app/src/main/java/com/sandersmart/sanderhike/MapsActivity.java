package com.sandersmart.sanderhike;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
//import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

// sanderHike by Jeffrey Sander

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private TileProvider tileProvider;
    private TileOverlay tileOverlay;

    private ArrayList<LatLng> lstLatLngs = new ArrayList<LatLng>();
    private ArrayList<LatLng> lstRecentLatLngs = new ArrayList<LatLng>();

    private HashMap<LatLng, Double> elevations = new HashMap<>();
    int numElevationsToRetrieve = 0;
    int numElevationsRetrieved = 0;

    private ArrayList<Marker> lstMarkers = new ArrayList<Marker>();
    private ArrayList<Circle> lstCircles = new ArrayList<>();

    public static final int REQUEST_LOCATION = 99;
    private FusedLocationProviderClient mFusedLocationClient;
    SensorManager mSensorManager;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 500; /* 2 sec */

    private long MIN_DISTANCE = 50;
    private long MAX_DISTANCE = 1000;

    private int MIN_LATLNGS = 7;

    TextView directionText;
    TextView bottomText;
    TextView scaleText;
    ImageButton currentLocationButton;
    ImageButton[] circleSelectorButtons;
    ImageButton mainCircleButton;

    Marker currentLocationMarker;

    private float currentDegree = 0.0f;
    private int[] degreeCounts = new int[8];
    private float degreeSum = 0.0f;
    private int degreeCount = 0;

    private boolean inGoMode = false;

    Location currentLocation = null;

    private TrailPoint selectedStartPoint = null;

    boolean mapFirstLoaded = false;

    int currentTrail = 1;
    static int numMarkers = 8;
    boolean[] currentMarkers = new boolean[numMarkers];

    public static int[] markerIcons = {R.mipmap.ic_black_circle, R.mipmap.ic_red_circle, R.mipmap.ic_blue_circle,
            R.mipmap.ic_green_circle, R.mipmap.ic_yellow_circle,
            R.mipmap.ic_violet_circle, R.mipmap.ic_orange_circle, R.mipmap.ic_white_circle};

    ArrayList<TrailPoint> mTrailPoints = new ArrayList<>();

    TrailPath currentTrailPath = new TrailPath();

    HashMap<LatLng, Polygon> elevationPolygons = new HashMap<>();
    ArrayList<Polyline> trailLines = new ArrayList<>();

    int lastAreaID = 0;

    public static class TrailPoint {
        public LatLng mLatlng;
        public int mDegree;
        public boolean[] mColors = new boolean[numMarkers];
        public ArrayList<TrailPoint> mNeighbors = new ArrayList<>();
        public ArrayList<Marker> mMarkers = new ArrayList<>();
        Circle circle = null;
        Date date = null;
        double altitude = 0;
        double elevation = 0;

        public TrailPoint() {

        }

        public TrailPoint(LatLng ll, int degree) {
            mLatlng = ll;
            mDegree = degree;
        }

        private String getColorString() {
            String ret = "";
            for(boolean color : mColors) {
                if(color == true)
                    ret += '1';
                else
                    ret += '0';
            }
            return ret;
        }

        public String toString () {
            SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:yy HH:mm:ss");
            String dateString = sdf.format(date);
            return "" + mLatlng.latitude + "/" + mLatlng.longitude + "/" + mDegree + "/" + getColorString() +
                    "/" + dateString + "/" + altitude + "/" + elevation;
        }
    }

    public class TrailPath {
        public ArrayList<TrailPoint> trailPoints = new ArrayList<>();
        public double distance = 0;
    }

    class TileProperties {
        int x;
        int y;
        int zoom;
    }

    class DownloadTilesTask extends AsyncTask<LatLng, Void, Double> {
        protected Double doInBackground(LatLng... latLngs) {
            downloadCurrentMapHelper(latLngs[0]);
//            for(TileProperties tileProperties : tilesAL[0]) {
//                downloadTile("testMapName", tileProperties.x, tileProperties.y, tileProperties.zoom);
//            }
            return 0.0;
        }

        protected void onPostExecute(Double result) {
            tileOverlay.clearTileCache();
            Log.d("onPostExecute", "Tile Cache cleared...");
            Toast.makeText(getApplicationContext(), "Tiles Downloaded!", Toast.LENGTH_SHORT).show();
        }
    }

    class RetrieveElevationTask extends AsyncTask<ArrayList<LatLng>, Void, Double> {

        private Exception exception;
        //LatLng latlng = null;
        HashMap<LatLng, Double> elevationResults = new HashMap<>();
        //ArrayList<Double> results = new ArrayList<>();

        protected Double doInBackground(ArrayList<LatLng>... latlngsAL) {
            ArrayList<LatLng> latlngs = latlngsAL[0];
            for(LatLng latlng : latlngs) {
                //latlng = latlngs[0];
                double latitude = latlng.latitude;
                double longitude = latlng.longitude;
                //            String urlString = "http://gisdata.usgs.gov/"
                //                    + "xmlwebservices2/elevation_service.asmx/"
                //                    + "getElevation?X_Value=" + String.valueOf(longitude)
                //                    + "&Y_Value=" + String.valueOf(latitude)
                //                    + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
                String urlString = "https://nationalmap.gov/epqs/pqs.php?x="
                        + longitude + "&y=" + latitude + "&units=Meters&output=xml";
                Double result = Double.NaN;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        int r = -1;
                        StringBuffer respStr = new StringBuffer();
                        while ((r = in.read()) != -1)
                            respStr.append((char) r);
                        String tagOpen = "<Elevation>";
                        String tagClose = "</Elevation>";
                        if (respStr.indexOf(tagOpen) != -1) {
                            int start = respStr.indexOf(tagOpen) + tagOpen.length();
                            int end = respStr.indexOf(tagClose);
                            String value = respStr.substring(start, end);
                            result = Double.parseDouble(value);
                        }
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                elevationResults.put(latlng, result);
            }
            return 0.0;
        }

        protected void onPostExecute(Double r) {
            for(LatLng latlng : elevationResults.keySet()) {
                Double result = elevationResults.get(latlng);
                elevations.put(latlng, result);
                numElevationsRetrieved++;
                if (elevationPolygons.containsKey(latlng)) {
                    elevationPolygons.get(latlng).setFillColor(Color.GREEN);
                }
                if(numElevationsRetrieved == numElevationsToRetrieve) {
                    int polygonAreaID = (int)elevationPolygons.get(latlng).getTag();
                    updateAllElevationPolygons(polygonAreaID);
                }
            }
            //Toast.makeText(getApplicationContext(), "Elevation is " + result, Toast.LENGTH_SHORT).show();
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.trail_marker_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TrailPoint tp = getNearestTrailPoint(marker.getPosition(), mTrailPoints);

            TextView tvName = (TextView) myContentsView.findViewById(R.id.textInfoName);
            tvName.setText("My Trail");
            TextView tvLatLng = (TextView) myContentsView.findViewById(R.id.textInfoLatLng);
            tvLatLng.setText(tp.mLatlng.toString());
            TextView tvTime = (TextView) myContentsView.findViewById(R.id.textInfoTime);
            tvTime.setText(tp.date.toString());
            TextView tvSpeed = (TextView) myContentsView.findViewById(R.id.textInfoSpeed);
            tvSpeed.setText("Speed is " + (int) getSpeed(tp) + " meters per minute");
            TextView tvAltitude = (TextView) myContentsView.findViewById(R.id.textInfoAltitude);
            tvAltitude.setText("Altitude is " + (int)tp.altitude + " meters and elevation is " +
                    elevations.get(tp.mLatlng));

            ArrayList<LatLng> toPass = new ArrayList<>();
            toPass.add(tp.mLatlng);
            new RetrieveElevationTask().execute(toPass);
//            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
//            tvTitle.setText(marker.getTitle());
//            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
//            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d("onActivityResult", data.getBooleanArrayExtra("markers").toString());

                currentMarkers = data.getBooleanArrayExtra("markers");
            }
        } else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                String trailName = data.getStringExtra("trailToLoad");
                loadDataFromFile(trailName);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        directionText = (TextView) findViewById(R.id.direction);
        bottomText = (TextView) findViewById(R.id.bottomText);
        scaleText = (TextView) findViewById(R.id.textScale);
        currentLocationButton = (ImageButton) findViewById(R.id.currentLocationButton);

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
                mMap.animateCamera(cameraUpdate);
            }
        });

        circleSelectorButtons = new ImageButton[8];
        circleSelectorButtons[0] = (ImageButton) findViewById(R.id.blackCircleButton);
        circleSelectorButtons[1] = (ImageButton) findViewById(R.id.redCircleButton);
        circleSelectorButtons[2] = (ImageButton) findViewById(R.id.blueCircleButton);
        circleSelectorButtons[3] = (ImageButton) findViewById(R.id.greenCircleButton);
        circleSelectorButtons[4] = (ImageButton) findViewById(R.id.yellowCircleButton);
        circleSelectorButtons[5] = (ImageButton) findViewById(R.id.violetCircleButton);
        circleSelectorButtons[6] = (ImageButton) findViewById(R.id.orangeCircleButton);
        circleSelectorButtons[7] = (ImageButton) findViewById(R.id.whiteCircleButton);

        for(int i = 0; i < circleSelectorButtons.length; i++) {
            ImageButton ib = circleSelectorButtons[i];
            ib.setTag(i);
            ib.setBackground(null);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentMarkers[(int)v.getTag()] = !currentMarkers[(int)v.getTag()];
                    if(v.getBackground() != null) {
                        v.setBackground(null);
                    } else {
                        v.setBackgroundResource(R.drawable.cast_ic_expanded_controller_play);
                    }
                }
            });
        }

        mainCircleButton = (ImageButton) findViewById(R.id.mainCirclesButton);
        mainCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCircleSelectorButtons();
            }
        });

        //currentLocationMarker = (ImageView) findViewById(R.id.imageCurrentLocation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

//        Button selectMarkersButton = (Button) findViewById(R.id.SelectMarkers);
//        selectMarkersButton.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//
//                Intent intent = new Intent(getApplicationContext(), MarkerSelectActivity.class);
//                startActivityForResult(intent, 1);
//            }
//        });
//
//        Button saveButton = (Button) findViewById(R.id.buttonSave);
//        saveButton.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                saveDataToFile("testfile.txt");
//            }
//        });
//
//        Button loadButton = (Button) findViewById(R.id.buttonLoad);
//        loadButton.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                loadDataFromFile("testfile.txt");
//            }
//        });

         //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("onLocationResult", "" + location.getLatitude() + ", " + location.getLongitude() +
                    " " + location.getAltitude());
                    currentLocation = location;
                    lstRecentLatLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    if(lstRecentLatLngs.size() > MIN_LATLNGS) {
                        lstRecentLatLngs.remove(0);
                    }

                    addCurrentPoint4();
                    updateCurrentLocationMarker(new LatLng(location.getLatitude(), location.getLongitude()), 0.0f);

                    if(!mapFirstLoaded) {
                        zoomToLocation(latlng);
                    }
                }
            };
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_marker_option:
                Intent intent = new Intent(getApplicationContext(), MarkerSelectActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.go_mode_option:
                inGoMode = !inGoMode;
                if(!inGoMode) {
                    clearAllTrailPoints();
                }
                return true;
            case R.id.go_to_address_option:
                goToAddressOption();
                return true;
            case R.id.download_map_option:
                downloadCurrentMap();
                return true;
            case R.id.show_elevations_option:
                showElevations(mTrailPoints);
                return true;
            case R.id.show_elevation_rects_option:
                showElevationOverlay();
                return true;
            case R.id.save_map_option:
                saveTrail();
                //saveDataToFile("testfile.txt");
                return true;
            case R.id.load_map_option:
                Intent loadMapIntent = new Intent(getApplicationContext(), ViewTrailsActivity.class);
                startActivityForResult(loadMapIntent, 2);
                //loadDataFromFile("testfile.txt");
                return true;
            case R.id.settings_option:
                Intent settingsIntent = new Intent(getApplicationContext(), AppCompatPreferenceActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.about_option:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        currentDegree = degree;
        if(currentLocationMarker != null)
            currentLocationMarker.setRotation(degree);

        //Log.d("compass heading", "" + degree);

        if(degree > 337.5 || degree < 22.5) {
            directionText.setText("North");
            degreeCounts[0]++;
        } else if(degree > 22.5 && degree < 67.5) {
            directionText.setText("North East");
            degreeCounts[1]++;
        } else if (degree > 67.5 && degree < 112.5) {
            directionText.setText("East");
            degreeCounts[2]++;
        } else if (degree > 112.5 && degree < 157.5) {
            directionText.setText("South East");
            degreeCounts[3]++;
        } else if (degree > 157.5 && degree < 202.5) {
            directionText.setText("South");
            degreeCounts[4]++;
        } else if (degree > 202.5 && degree < 247.5) {
            directionText.setText("South West");
            degreeCounts[5]++;
        } else if (degree > 247.5 && degree < 292.5) {
            directionText.setText("West");
            degreeCounts[6]++;
        } else if (degree > 292.5) {
            directionText.setText("North West");
            degreeCounts[7]++;
        }


        //directionText.setText("North");

        //tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

//        // create a rotation animation (reverse turn degree degrees)
//        RotateAnimation ra = new RotateAnimation(
//                currentDegree,
//                        -degree,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF,
//        0.5f);
//        ra.setDuration(210);
//        ra.setFillAfter(true);
//        image.startAnimation(ra);
//        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateCurrentLocationMarker(LatLng location, float degree) {
        if(currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .rotation(currentDegree)
                .anchor(0.5f, 0.5f)
                .zIndex(100)
                //.title(title)
                //.alpha(1.0f)
                //.snippet("" + dateString + " " + altitude)
                .icon((BitmapDescriptorFactory.fromResource(R.mipmap.ic_red)))
        );
    }

    private void toggleCircleSelectorButtons() {
        if(circleSelectorButtons[0].getVisibility() != View.INVISIBLE) {
            for(ImageButton ib : circleSelectorButtons) {
                ib.setVisibility(View.INVISIBLE);
            }
        } else {
            for(ImageButton ib : circleSelectorButtons) {
                ib.setVisibility(View.VISIBLE);
            }
        }
    }

    private void zoomToAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        //GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return;
            }
            Address location=address.get(0);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            long tileX = long2tile(latlng.longitude, 15);
            long tileY = lat2tile(latlng.latitude, 15);
            Log.d("zoomToAddress", "Suspected tile " + tileX + " " + tileY);
            zoomToLocation(latlng);

//            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
//                    (double) (location.getLongitude() * 1E6));

            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zoomToLocation(LatLng latlng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        mMap.animateCamera(cameraUpdate);
        mapFirstLoaded = true;
    }

    private void goToAddressOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What address would you like to go to?");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                zoomToAddress(m_Text);
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

    private void downloadCurrentMap() {
        new DownloadTilesTask().execute(mMap.getCameraPosition().target);
    }

    private void downloadCurrentMapHelper(LatLng latlng) {
        int TILES_TO_DOWNLOAD = 7;

//        Toast.makeText(getApplicationContext(), "Downloading " + TILES_TO_DOWNLOAD * TILES_TO_DOWNLOAD * 3 + " tiles...",
//                Toast.LENGTH_SHORT).show();

        //LatLng latlng = mMap.getCameraPosition().target;

        long targetTileXZ15 = long2tile(latlng.longitude, 15);
        long targetTileYZ15 = lat2tile(latlng.latitude, 15);

        for(int i = 0; i < TILES_TO_DOWNLOAD; i++) {
            for(int j = 0; j < TILES_TO_DOWNLOAD; j++) {
                downloadTile("testMap", (int)targetTileXZ15 - TILES_TO_DOWNLOAD / 2 + i,
                        (int)targetTileYZ15 - TILES_TO_DOWNLOAD / 2 + j, 15);
            }
        }

        long targetTileXZ14 = long2tile(latlng.longitude, 14);
        long targetTileYZ14 = lat2tile(latlng.latitude, 14);

        for(int i = 0; i < TILES_TO_DOWNLOAD; i++) {
            for(int j = 0; j < TILES_TO_DOWNLOAD; j++) {
                downloadTile("testMap", (int)targetTileXZ14 - TILES_TO_DOWNLOAD / 2 + i,
                        (int)targetTileYZ14 - TILES_TO_DOWNLOAD / 2 + j, 14);
            }
        }

        long targetTileXZ13 = long2tile(latlng.longitude, 13);
        long targetTileYZ13 = lat2tile(latlng.latitude, 13);

        for(int i = 0; i < TILES_TO_DOWNLOAD; i++) {
            for(int j = 0; j < TILES_TO_DOWNLOAD; j++) {
                downloadTile("testMap", (int)targetTileXZ13 - TILES_TO_DOWNLOAD / 2 + i,
                        (int)targetTileYZ13 - TILES_TO_DOWNLOAD / 2 + j, 13);
            }
        }
    }

    private void downloadTile(String mapName, int x, int y, int zoom) {
        String s = String.format("https://basemap.nationalmap.gov/arcgis/rest/services/USGSImageryTopo/MapServer/tile/%d/%d/%d",
                zoom, y, x);

        Log.d("getTile", "Downloading tile x,y,z: " + x + " " + y + " " + zoom);

        Bitmap tileBitmap = getBitmap(s);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("mapTilesDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"" + x + "-" + y + "-" + zoom);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            tileBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            //Log.d("getTile", "Saved tile as " + )
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public double getSpeed(TrailPoint tp) {
        int index = mTrailPoints.indexOf(tp);
        if(index <= 0)
            return 0;
        TrailPoint previous = mTrailPoints.get(index - 1);
        double deltaDistance = getDistance(tp.mLatlng, previous.mLatlng);
        long timeDiff = (tp.date.getTime() - previous.date.getTime());
        double deltaTimeMinutes = (double) timeDiff / (60 * 1000);

        return deltaDistance / deltaTimeMinutes;
    }

    private float getTotalDistance() {
        float totalDistance = 0;
        for (int i = 0; i < lstLatLngs.size() - 1; i++) {
            totalDistance += getDistance(lstLatLngs.get(i), lstLatLngs.get(i+1));
        }
        return totalDistance;
    }

    public float getTotalDistanceFromTrailPoints() {
        float totalDistance = 0;
        for (int i = 0; i < mTrailPoints.size() - 1; i++) {
            totalDistance += getDistance(mTrailPoints.get(i).mLatlng, mTrailPoints.get(i+1).mLatlng);
        }
        return totalDistance;
    }

    public static float getTotalDistanceFromTrailPoints(ArrayList<TrailPoint> trailPoints) {
        float totalDistance = 0;
        for (int i = 0; i < trailPoints.size() - 1; i++) {
            totalDistance += getDistance(trailPoints.get(i).mLatlng, trailPoints.get(i+1).mLatlng);
        }
        return totalDistance;
    }

    private float getTotalDistance(ArrayList<TrailPoint> trailPoints) {
        float totalDistance = 0;
        for (int i = 0; i < trailPoints.size() - 1; i++) {
            totalDistance += getDistance(trailPoints.get(i).mLatlng, trailPoints.get(i+1).mLatlng);
        }
        return totalDistance;
    }

    private float getAverageDegree(int[] degreeCounts) {
        return 0.0f;
    }

    private int getColorIcon(int color) {
        switch (color) {
            case 0: return R.mipmap.ic_black;
            case 1: return R.mipmap.ic_red;
            case 2: return R.mipmap.ic_blue;
            case 3: return R.mipmap.ic_green;
            case 4: return R.mipmap.ic_yellow;
            case 5: return R.mipmap.ic_white;
        }

        return 0;
    }

    private TrailPoint getNearestTrailPoint(LatLng latlng, ArrayList<TrailPoint> trailPoints) {
        double minDistance = 10000.0;
        TrailPoint closestTrailPoint = trailPoints.get(0);

        for(TrailPoint tp : trailPoints) {
            double d = getDistance(tp.mLatlng, latlng);
            if(d < minDistance) {
                closestTrailPoint = tp;
                minDistance = d;
            }
        }

        return closestTrailPoint;
    }

    private boolean pointWithinRange(LatLng latlng, double range, ArrayList<TrailPoint> trailPoints) {
        for(TrailPoint tp : trailPoints) {
            double distance = getDistance(tp.mLatlng, latlng);
            if(distance <= range) {
                return true;
            }
        }
        return false;
    }

    private void clearTrailLines() {
        for(Polyline polyline : trailLines) {
            polyline.remove();
        }
        trailLines.clear();
    }

    private void drawTrailLine(TrailPoint a, TrailPoint b) {
        PolylineOptions rectOptions = new PolylineOptions()
                .add(a.mLatlng)
                .add(b.mLatlng); // Closes the polyline.

// Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(rectOptions);
        trailLines.add(polyline);
    }

    private void drawTrailPath(TrailPath trailPath) {
        for(int i = 0; i < trailPath.trailPoints.size() - 1; i++) {
            TrailPoint a = trailPath.trailPoints.get(i);
            TrailPoint b = trailPath.trailPoints.get(i+1);

            drawTrailLine(a, b);
        }
    }

    private ArrayList<Marker> addTrailPointMarkers(LatLng latlng, boolean[] markers) {
        double offset = 0.0;
        double delta = 0.0001;

        ArrayList<Marker> tpMarkers = new ArrayList<>();

        int markerCount = 0;
        for(boolean marker : markers) {
            if(marker == true)
                markerCount++;
        }

        offset = - markerCount / 2 * delta;

        for (int i = 0; i < markerIcons.length; i++) {
            if (markers[i]) {
                LatLng ll = new LatLng(latlng.latitude + offset, latlng.longitude);
                int markerIconWidth = 35;
                int markerIconHeight = 35;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), markerIcons[i]);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, markerIconWidth, markerIconHeight, false);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                Marker newMarker = mMap.addMarker(new MarkerOptions()
                                .position(ll)
                                //.rotation(degree)
                                .anchor(0.5f, 0.5f)
                                //.title(title)
                                .alpha(1.0f)
                                //.snippet("" + dateString + " " + altitude)

                                .icon(bitmapDescriptor)
                        //.icon((BitmapDescriptorFactory.fromResource(markerIcons[i])))
                );
                lstMarkers.add(newMarker);
                tpMarkers.add(newMarker);
                //onTrail = true;
                offset += delta;
            }
        }
        return tpMarkers;
    }

    private TrailPoint addTrailPoint(LatLng latlng, String title, int degree, boolean[] markers,
                                     Date date, double altitude, ArrayList<Marker> tpMarkers) {
        TrailPoint newTrailPoint = new TrailPoint(latlng, degree);

        TrailPoint tp = new TrailPoint(latlng, (int) degree);
        tp.mColors = markers;
        tp.date = date;
        tp.altitude = altitude;
        tp.mMarkers = tpMarkers;
//        if (mTrailPoints.size() > 0) {
//            tp.mNeighbors.add(mTrailPoints.get(mTrailPoints.size() - 1));
//        }

        //Retrieve Elevation
        ArrayList<LatLng> toRetrieve = new ArrayList<>();
        toRetrieve.add(latlng);
        new RetrieveElevationTask().execute(toRetrieve);

        mTrailPoints.add(tp);

        //Update Current Trail Path
//        currentTrailPath.trailPoints.add(tp);
//        clearTrailLines();
//        drawTrailPath(currentTrailPath);

        return newTrailPoint;
    }

    private void addPoint(LatLng latlng, String title, float degree, boolean[] markers, Date date, double altitude) {
        //Calendar c = Calendar.getInstance();
        //Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        String dateString = "No Date";
        if(date != null) dateString = sdf.format(date);

        double offset = 0.0;
        double delta = 0.0001;
        boolean onTrail = false;

        ArrayList<Marker> tpMarkers = new ArrayList<>();

        int markerCount = 0;
        for(boolean marker : markers) {
            if(marker == true)
                markerCount++;
        }

        offset = - markerCount / 2 * delta;

        if(!pointWithinRange(latlng, MIN_DISTANCE * 1/2, mTrailPoints)) {
            for (int i = 0; i < markerIcons.length; i++) {
                if (markers[i]) {
                    LatLng ll = new LatLng(latlng.latitude + offset, latlng.longitude);
                    int markerIconWidth = 35;
                    int markerIconHeight = 35;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), markerIcons[i]);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, markerIconWidth, markerIconHeight, false);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                    Marker newMarker = mMap.addMarker(new MarkerOptions()
                                    .position(ll)
                                    .rotation(degree)
                                    .anchor(0.5f, 0.5f)
                                    .title(title)
                                    .alpha(1.0f)
                                    .snippet("" + dateString + " " + altitude)

                                    .icon(bitmapDescriptor)
                            //.icon((BitmapDescriptorFactory.fromResource(markerIcons[i])))
                    );
                    lstMarkers.add(newMarker);
                    tpMarkers.add(newMarker);
                    onTrail = true;
                    offset += delta;
                }
            }
        }

        if(onTrail) {
            //Create Trail Point
            TrailPoint tp = new TrailPoint(latlng, (int) degree);
            tp.mColors = markers;
            tp.date = date;
            tp.altitude = altitude;
            tp.mMarkers = tpMarkers;
            if (mTrailPoints.size() > 0) {
                tp.mNeighbors.add(mTrailPoints.get(mTrailPoints.size() - 1));
            }

            //Retrieve Elevation
            ArrayList<LatLng> toRetrieve = new ArrayList<>();
            toRetrieve.add(latlng);
            new RetrieveElevationTask().execute(toRetrieve);

            mTrailPoints.add(tp);

            //Update Current Trail Path
            currentTrailPath.trailPoints.add(tp);
            clearTrailLines();
            drawTrailPath(currentTrailPath);
        }
    }

    private LatLng getAverageLatLng(ArrayList<LatLng> lstLatLngs) {
        float latitudeSum = 0.0f;
        float longitudeSum = 0.0f;
        int count = 0;

        int beginIndex = lstLatLngs.size() - MIN_LATLNGS;
        if(beginIndex < 0)
            beginIndex = 0;

        for(int i = beginIndex; i < lstLatLngs.size(); i++) {
            latitudeSum += lstLatLngs.get(i).latitude;
            longitudeSum += lstLatLngs.get(i).longitude;
            count++;
        }

        return new LatLng(latitudeSum / count, longitudeSum / count);
    }

    private float getDegree(LatLng ll1, LatLng ll2) {
        //Source
        //JSONObject source = step.getJSONObject("start_location");
        double lat1 = ll1.latitude;
        double lng1 = ll1.longitude;

        // destination
        //JSONObject destination = step.getJSONObject("end_location");
        double lat2 = ll2.latitude;
        double lng2 = ll2.longitude;

        double dLon = (lng2-lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));
        return (float) brng;
    }

    private double getElevation(Double latitude, Double longitude) {
        double result = Double.NaN;
        String urlString = "http://gisdata.usgs.gov/"
                + "xmlwebservices2/elevation_service.asmx/"
                + "getElevation?X_Value=" + String.valueOf(longitude)
                + "&Y_Value=" + String.valueOf(latitude)
                + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = in.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<double>";
                String tagClose = "</double>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = Double.parseDouble(value);
                }
                in.close();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

//    private double getAltitude(Double longitude, Double latitude) {
//        double result = Double.NaN;
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        String url = "http://gisdata.usgs.gov/"
//                + "xmlwebservices2/elevation_service.asmx/"
//                + "getElevation?X_Value=" + String.valueOf(longitude)
//                + "&Y_Value=" + String.valueOf(latitude)
//                + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = httpClient.execute(httpGet, localContext);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream instream = entity.getContent();
//                int r = -1;
//                StringBuffer respStr = new StringBuffer();
//                while ((r = instream.read()) != -1)
//                    respStr.append((char) r);
//                String tagOpen = "<double>";
//                String tagClose = "</double>";
//                if (respStr.indexOf(tagOpen) != -1) {
//                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
//                    int end = respStr.indexOf(tagClose);
//                    String value = respStr.substring(start, end);
//                    result = Double.parseDouble(value);
//                }
//                instream.close();
//            }
//        } catch (ClientProtocolException e) {}
//        catch (IOException e) {}
//        return result;
//    }

    private boolean isOnTrail(boolean[] markers) {
        for(boolean b : markers) {
            if(b) return true;
        }
        return false;
    }

    private void addCurrentPoint3() {
        if(lstRecentLatLngs.size() >= MIN_LATLNGS) {
            LatLng averageLatLng = getAverageLatLng(lstRecentLatLngs);
            float distance = -1.0f;

            LatLng previousLatLng = null;
            float degree = 0;
            if(lstLatLngs.size() > 0) {
                previousLatLng = lstLatLngs.get(lstLatLngs.size() - 1);
                distance = getDistance(previousLatLng, averageLatLng);
                degree = (getDegree(averageLatLng, previousLatLng) + 180) % 360;
            }

            if(((distance > MIN_DISTANCE && distance < MAX_DISTANCE) || distance < 0) && isOnTrail(currentMarkers)) {
                lstLatLngs.add(averageLatLng);
                double currentAltitude = 0;
                if(currentLocation != null) currentAltitude = currentLocation.getAltitude();
                addPoint(averageLatLng, "Blue Trail", degree, currentMarkers, new Date(), currentAltitude);
                //lstRecentLatLngs.clear();
                updateBottomText();
            }
        }
    }

    private void addCurrentPoint4() {
        if(lstRecentLatLngs.size() >= MIN_LATLNGS) {
            LatLng averageLatLng = getAverageLatLng(lstRecentLatLngs);
            float distance = -1.0f;

            LatLng previousLatLng = null;
            float degree = 0;
            if(lstLatLngs.size() > 0) {
                previousLatLng = lstLatLngs.get(lstLatLngs.size() - 1);
                distance = getDistance(previousLatLng, averageLatLng);
                degree = (getDegree(averageLatLng, previousLatLng) + 180) % 360;
            }



            if(((distance > MIN_DISTANCE && distance < MAX_DISTANCE) || distance < 0) && isOnTrail(currentMarkers)) {
                lstLatLngs.add(averageLatLng);
                double currentAltitude = 0;
                if(currentLocation != null) currentAltitude = currentLocation.getAltitude();
                //addPoint(averageLatLng, "Blue Trail", degree, currentMarkers, new Date(), currentAltitude);
                //lstRecentLatLngs.clear();

                if(pointWithinRange(averageLatLng, MIN_DISTANCE * 1, mTrailPoints)) {
                    TrailPoint nearestTrailPoint = getNearestTrailPoint(averageLatLng, mTrailPoints);
                    TrailPoint newTrailPoint = addTrailPoint(nearestTrailPoint.mLatlng, "My Trail", (int) degree, currentMarkers, new Date(), currentAltitude,
                            null);

                    //Update Current Trail Path
                    currentTrailPath.trailPoints.add(newTrailPoint);
                } else {
                    ArrayList<Marker> newTrailPointMarkers = addTrailPointMarkers(averageLatLng, currentMarkers);
                    TrailPoint newTrailPoint = addTrailPoint(averageLatLng, "My Trail", (int) degree, currentMarkers, new Date(), currentAltitude,
                            newTrailPointMarkers);

                    //Update Current Trail Path
                    currentTrailPath.trailPoints.add(newTrailPoint);
                }
                clearTrailLines();
                drawTrailPath(currentTrailPath);
                updateBottomText();
            }
        }
    }

    private void updateBottomText() {
        bottomText.setText("Total Distance: " + (int)getTotalDistanceFromTrailPoints());

        LatLng left = mMap.getProjection().getVisibleRegion().farLeft;
        LatLng right = mMap.getProjection().getVisibleRegion().farRight;

        double distance = getDistance(left, right);


        scaleText.setText("" + (int)distance + " meters");
    }

    private void saveTrail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a name for your trail:");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                saveDataToFile(m_Text);
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

    private void saveDataToFile(String filename) {
        FileOutputStream outputStream;
        String path = getFilesDir().getPath()+File.separator+"Trails"+File.separator+filename;


        Log.d("saveDataToFile", "saving " + mTrailPoints.size() + " data points to " + path);

        try {
            File file = new File(path);
            //file.mkdirs();
            outputStream = new FileOutputStream(file);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( outputStream ) );
            for(TrailPoint trailPoint : mTrailPoints) {
                String trailPointString = trailPoint.toString();
                Log.d("saveDataToFile", "tp: " + trailPointString);
                //outputStreamWriter.append(trailPointString);
                writer.println(trailPointString.toString());
            }
            writer.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTrailPath(String trailname) {
        return getFilesDir()+File.separator+"Trails"+File.separator+trailname;
    }

    private void loadDataFromFile(String filename) {
        //Get the text file
        File file = new File(getFilesDir()+File.separator+"Trails"+File.separator+filename);

        //Read text from file
        StringBuilder text = new StringBuilder();

        Log.d("loadDataFromFile", "loading data...");

        SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:yyyy HH:mm:ss");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Log.d("loadDataFromFile", line);
                String[] parts = line.split("/");
                double lat = Double.parseDouble(parts[0]);
                double lon = Double.parseDouble(parts[1]);
                int degree = Integer.parseInt(parts[2]);
                boolean[] mColors = getBooleanFromString(parts[3]);
                Date date = null;
                if(parts.length >= 5) date = sdf.parse(parts[4]);
                double altitude = 0;
                if(parts.length >= 6) altitude = Double.parseDouble(parts[5]);
                LatLng ll = new LatLng(lat, lon);
                //addPoint(ll, "Blue Trail", degree, mColors, date, altitude);
                ArrayList<Marker> newTrailPointMarkers = addTrailPointMarkers(ll, mColors);
                addTrailPoint(ll, filename, degree, mColors, date, altitude, newTrailPointMarkers);
            }
            br.close();

            updateBottomText();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean[] getBooleanFromString(String s) {
        boolean[] ret = new boolean[s.length()];

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '1')
                ret[i] = true;
            else
                ret[i] = false;
        }
        return ret;
    }

    private static float getDistance(LatLng a, LatLng b) {
        Location l1 = new Location("");
        l1.setLatitude(a.latitude);
        l1.setLongitude(a.longitude);

        Location l2 = new Location("");
        l2.setLatitude(b.latitude);
        l2.setLongitude(b.longitude);

        return l1.distanceTo(l2);
    }

    private ArrayList<TrailPoint> findPath(TrailPoint start, TrailPoint end, ArrayList<TrailPoint> trailPoints) {
        ArrayList<ArrayList<TrailPoint>> paths = new ArrayList<>();
        ArrayList<TrailPoint> initialPath = new ArrayList<>();
        initialPath.add(start);
        paths.add(initialPath);

        int tries = 0;
        int MAX_TRIES = trailPoints.size();
        float bestpathdistance = MIN_DISTANCE * 100;
        ArrayList<TrailPoint> bestPath = null;
        while(tries < MAX_TRIES) {
            int sizeOfPaths = paths.size();
            for (int j = 0; j < sizeOfPaths; j++) {
                ArrayList<TrailPoint> path = paths.get(j);
                for (int i = 0; i < path.size(); i++) {
                    TrailPoint tp = path.get(i);
                    ArrayList<TrailPoint> neighbors = findPointsWithinRange(tp, MIN_DISTANCE * 2, trailPoints);
                    ArrayList<TrailPoint> newPath = new ArrayList<>();
                    newPath.addAll(path);
                    //newPath.add(tp);
                    for(TrailPoint neighbortp : neighbors) {
                        if(!newPath.contains(neighbortp)) {
                            newPath.add(neighbortp);
                        }
                    }
                    if(newPath.size() > path.size()) {
                        boolean containsPath = false;
                        for(int k = 0; k < paths.size(); k++) {
                            if(newPath.equals(paths.get(k)))
                                containsPath = true;
                        }
                        if(!containsPath)
                            paths.add(newPath);
                    }
                }
                //TrailPoint last = path.get(path.size() - 1);
            }
            boolean foundPath = false;
            for (ArrayList<TrailPoint> path : paths) {
                if (path.contains(end)) {
                    float distance = getTotalDistance(path);
                    if(distance < bestpathdistance) {
                        bestPath = path;
                        bestpathdistance = distance;
                    }
                    foundPath = true;
                }
            }
            if(foundPath)
                break;
            tries++;
        }

        if(bestPath == null)
            Toast.makeText(getApplicationContext(), "No path found", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "" + bestpathdistance + " meter path found!", Toast.LENGTH_SHORT).show();

        return bestPath;
    }

    private TrailPath findPath2(TrailPoint start, TrailPoint end, ArrayList<TrailPoint> trailPoints) {
        ArrayList<TrailPath> paths = new ArrayList<>();

        HashMap<TrailPoint, Double> shortestDistances = new HashMap<>();
        for(TrailPoint tp : trailPoints) {
            shortestDistances.put(tp, new Double(10000));
        }

        TrailPath startPath = new TrailPath();
        startPath.distance = 0;
        startPath.trailPoints.add(start);

        paths.add(startPath);

        int tries = 0;
        int MAX_TRIES = 100;

        while(tries < MAX_TRIES) {
            int sizeOfPaths = paths.size();
            boolean foundPath = false;
            for(int i = 0; i < sizeOfPaths; i++) {
                TrailPath currentPath = paths.get(i);
                TrailPoint lastPoint = currentPath.trailPoints.get(currentPath.trailPoints.size() - 1);
                ArrayList<TrailPoint> neighbors = findPointsWithinRange(lastPoint, MIN_DISTANCE * 1.5, trailPoints);

                for(TrailPoint neighbor : neighbors) {
                    if(!currentPath.trailPoints.contains(neighbor)) {
                        TrailPath newPath = new TrailPath();
                        newPath.trailPoints.addAll(currentPath.trailPoints);
                        newPath.trailPoints.add(neighbor);
                        newPath.distance = getTotalDistance(newPath.trailPoints);
                        Double previousShortestDistance = shortestDistances.get(neighbor);
                        if(newPath.distance < previousShortestDistance) {
                            shortestDistances.put(neighbor, newPath.distance);
                            paths.add(newPath);
                        }
                    }
                }
                if(currentPath.trailPoints.contains(end))
                    foundPath = true;
            }
            if(foundPath)
                break;
            tries++;
        }

        TrailPath shortest = null;
        for(TrailPath path : paths) {
            if(path.trailPoints.contains(end)) {
                if (shortest == null || path.distance < shortest.distance) {
                    shortest = path;
                }
            }
        }
        return shortest;
    }

    private HashMap<TrailPoint, ArrayList<TrailPoint>> findTrailPointNeighbors(ArrayList<TrailPoint> trailPoints) {
        HashMap<TrailPoint, ArrayList<TrailPoint>> trailPointNeighbors = new HashMap<>();

        for(TrailPoint tp : trailPoints) {
            ArrayList<TrailPoint> neighbors = new ArrayList<>();
            neighbors = findPointsWithinRange(tp, MIN_DISTANCE * 1.5, trailPoints);
            trailPointNeighbors.put(tp, neighbors);
        }

        return trailPointNeighbors;
    }

    private TrailPath findPath3(TrailPoint start, TrailPoint end, ArrayList<TrailPoint> trailPoints) {
        HashMap<TrailPoint, TrailPath> shortestPaths = new HashMap<>();
        HashMap<TrailPoint, ArrayList<TrailPoint>> trailPointNeighbors = new HashMap<>();

        Log.d("findPath3", "Generating trail point neighbors hash map...");
        trailPointNeighbors = findTrailPointNeighbors(trailPoints);
        Log.d("findPath3", "Generated trail point neighbors hash map.");

        Queue<TrailPoint> currentPoints = new LinkedList<>();
        currentPoints.add(start);

        int MAX_TRIES = 250;
        for(int i = 0; i < MAX_TRIES; i++) {
//            for(TrailPoint currentTrailPoint : currentPoints) {
            while(!currentPoints.isEmpty()) {
                TrailPoint currentTrailPoint = currentPoints.remove();
                TrailPath currentPath = shortestPaths.get(currentTrailPoint);
                ArrayList<TrailPoint> currentNeighbors = trailPointNeighbors.get(currentTrailPoint);
                for (TrailPoint tp : currentNeighbors) {
                    if (shortestPaths.get(tp) == null) { //No path has been found yet
                        TrailPath newTrailPath = new TrailPath();
                        if(currentPath != null)
                            newTrailPath.trailPoints.addAll(currentPath.trailPoints);
                        newTrailPath.trailPoints.add(currentTrailPoint);
                        shortestPaths.put(tp, newTrailPath);
                        currentPoints.add(tp);
                        if(newTrailPath.trailPoints.contains(end)) {
                            return newTrailPath;
                        }
                    }
                }
                //currentPoints.remove(currentTrailPoint);
            }
        }

        TrailPath shortestPath = shortestPaths.get(end);
        if(shortestPath != null) {
            shortestPath.trailPoints.add(end);
        }
        return shortestPath;
    }

    private ArrayList<TrailPoint> findPointsWithinRange(TrailPoint target, double range, ArrayList<TrailPoint> trailPoints) {
        ArrayList<TrailPoint> withinRange = new ArrayList();
        for(TrailPoint tp : trailPoints) {
            if(getDistance(target.mLatlng, tp.mLatlng) <= range) {
                withinRange.add(tp);
            }
        }
        return withinRange;
    }

    private double findMinElevation(ArrayList<TrailPoint> trailPoints) {
        double minElevation = 100000;

        for(TrailPoint tp : trailPoints) {
            Double tpElevation = elevations.get(tp.mLatlng);
            if(tpElevation < minElevation) {
                minElevation = tpElevation;
            }
        }
        return minElevation;
    }

    private double findAverageElevation(ArrayList<TrailPoint> trailPoints) {
        double elevationSum = 0;
        for(TrailPoint tp : trailPoints) {
            Double tpElevation = elevations.get(tp.mLatlng);
            elevationSum += tpElevation;
        }
        return elevationSum / trailPoints.size();
    }

    private double findMaxElevation(ArrayList<TrailPoint> trailPoints) {
        double maxElevation = 0;
        for(TrailPoint tp : trailPoints) {
            Double tpElevation = elevations.get(tp.mLatlng);
            if(tpElevation > maxElevation) {
                maxElevation = tpElevation;
            }
        }
        return maxElevation;
    }

    private void showElevations(ArrayList<TrailPoint> trailPoints) {
        double ALTITUDE_LEVEL_1 = findMaxElevation(trailPoints);
        double ALTITUDE_LEVEL_2 = findAverageElevation(trailPoints);
        double ALTITUDE_LEVEL_3 = findMinElevation(trailPoints);

        double A12_DIFF = (ALTITUDE_LEVEL_1 - ALTITUDE_LEVEL_2) / 2;
        double A23_DIFF = (ALTITUDE_LEVEL_2 - ALTITUDE_LEVEL_3) / 2;

        if(trailPoints == null)
            return;

        clearAllTrailPoints();

        for(TrailPoint tp : trailPoints) {
            Double tpElevation = elevations.get(tp.mLatlng);
            if(tp.circle == null) {
                int color = 0;
                if(tpElevation > ALTITUDE_LEVEL_1 - A12_DIFF) {
                    color = Color.RED;
                } else if(tpElevation > ALTITUDE_LEVEL_2 ) {
                    color = Color.rgb(255,69,0);
                } else if(tpElevation > ALTITUDE_LEVEL_3 + A23_DIFF) {
                    color = Color.YELLOW;
                } else {
                    color = Color.GREEN;
                }
                tp.circle = mMap.addCircle(new CircleOptions()
                        .center(tp.mLatlng)
                        .radius(20)
                        .strokeColor(Color.BLUE)
                        .fillColor(color));
            }
        }
    }

    private void downloadElevationsForOverlay() {

    }

    private double getNearestElevation(LatLng latlng, double maxRange) {
        double nearestElevation = -1;
        double nearestElevationDistance = 10000;
        for (LatLng ll : elevations.keySet()) {
            double distance = getDistance(latlng, ll);
            if(distance <= maxRange && distance < nearestElevationDistance) {
                nearestElevationDistance = distance;
                nearestElevation = elevations.get(ll);
            }
        }
        return nearestElevation;
    }

    private void showElevationOverlay() {
        int verticalRegions = 15;
        int horizontalRegions = 10;

        LatLng northeast = mMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        LatLng southwest = mMap.getProjection().getVisibleRegion().latLngBounds.southwest;

        LatLng northwest = new LatLng(northeast.latitude, southwest.longitude);

        double latitudeDelta = abs(northeast.latitude - southwest.latitude) / verticalRegions;
        double longitudeDelta = abs(northeast.longitude - southwest.longitude) / horizontalRegions;

        for(int i = 0; i < verticalRegions; i++) {
            for(int j = 0; j < horizontalRegions; j++) {
                LatLng topLeft = new LatLng(northwest.latitude - latitudeDelta * (i + 1),
                                            northwest.longitude + longitudeDelta * j);
                LatLng topRight = new LatLng(topLeft.latitude, topLeft.longitude + longitudeDelta);
                LatLng bottomLeft = new LatLng(topLeft.latitude + latitudeDelta, topLeft.longitude);
                LatLng bottomRight = new LatLng(topLeft.latitude + latitudeDelta, topLeft.longitude + longitudeDelta);

                LatLng centerOfRect = new LatLng((topLeft.latitude + topRight.latitude) / 2,
                        (topLeft.longitude + bottomLeft.longitude) / 2);
                //new RetrieveElevationTask().execute(centerOfRect);
                //executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params)
                int tofillcolor = Color.argb(128, 255, 0, 0);
                //int tofillcolor = Color.RED;

                PolygonOptions rectOptions = new PolygonOptions().fillColor(tofillcolor)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(1.0f)
                        .add(topLeft, topRight, bottomRight, bottomLeft);
                Polygon polygon = mMap.addPolygon(rectOptions);
                polygon.setTag(lastAreaID);
                elevationPolygons.put(centerOfRect, polygon);
            }
        }
        lastAreaID++;

        //Retrieve Elevations
        numElevationsToRetrieve = verticalRegions * horizontalRegions;
        numElevationsRetrieved = 0;
        ArrayList<LatLng> toRetrieve = new ArrayList<>();
        //ArrayList<LatLng> keySet = elevationPolygons.keySet();
        for(LatLng latlng : elevationPolygons.keySet()) {
            if((int)elevationPolygons.get(latlng).getTag() != lastAreaID - 1)
                continue;
            toRetrieve.add(latlng);
            if(toRetrieve.size() > 10) {
                new RetrieveElevationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, toRetrieve);
                toRetrieve = new ArrayList<>();
            }
        }
        new RetrieveElevationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, toRetrieve);
    }

    private void updateAllElevationPolygons(int targetAreaID) {
        double maxElevation = 0;
        for(LatLng latlng : elevationPolygons.keySet()) {
            if(elevations.get(latlng) != null && (int)elevationPolygons.get(latlng).getTag() == targetAreaID) {
                if(elevations.get(latlng) > maxElevation) {
                    maxElevation = elevations.get(latlng);
                }
            }
        }

        double minElevation = 10000;
        for(LatLng latlng : elevationPolygons.keySet()) {
            if(elevations.get(latlng) != null && (int)elevationPolygons.get(latlng).getTag() == targetAreaID) {
                if(elevations.get(latlng) < maxElevation) {
                    minElevation = elevations.get(latlng);
                }
            }
        }

//        double sumElevation = 0;
//        for(LatLng latlng : elevationPolygons.keySet()) {
//            if(elevations.get(latlng) != null) {
//                sumElevation += elevations.get(latlng);
//            }
//        }
//        double averageElevation = sumElevation / elevationPolygons.keySet().size();



        int alphaValue = 100;
        int COLOR_BLUE = Color.argb(alphaValue, 0, 0, 255);
        int COLOR_GREEN = Color.argb(alphaValue, 0, 255, 0);
        int COLOR_YELLOW = Color.argb(alphaValue, 255, 255, 0);
        int COLOR_ORANGE = Color.argb(alphaValue, 255, 69, 0);
        int COLOR_RED = Color.argb(alphaValue, 255, 0, 0);

        int[] polygonColors = {COLOR_BLUE, COLOR_GREEN, COLOR_YELLOW, COLOR_ORANGE, COLOR_RED};

        double diff = abs(maxElevation - minElevation) / polygonColors.length;

        for(LatLng latlng : elevationPolygons.keySet()) {
            Double polygonElevation = elevations.get(latlng);
            int color = 0;
            for(int i = 1; i <= polygonColors.length; i++) {
                if(polygonElevation <= minElevation + diff * i) {
                    color = polygonColors[i - 1];
                    break;
                }
            }
            int polygonAreaID = (int)elevationPolygons.get(latlng).getTag();
            if(polygonAreaID == targetAreaID) {
                elevationPolygons.get(latlng).setFillColor(color);
            }
        }

    }

    private void clearAllTrailPoints() {
        for(TrailPoint tp : mTrailPoints) {
            if(tp.circle != null) {
                tp.circle.remove();
                tp.circle = null;
            }
        }
    }

    private void showTrailPoints(ArrayList<TrailPoint> trailPoints) {
        if(trailPoints == null)
            return;

        clearAllTrailPoints();

        for(TrailPoint tp : trailPoints) {
            if(tp.circle == null) {
                tp.circle = mMap.addCircle(new CircleOptions()
                        .center(tp.mLatlng)
                        .radius(20)
                        .strokeColor(Color.YELLOW)
                        .fillColor(Color.BLUE));
            }
        }

        TrailPoint startPoint = trailPoints.get(0);
        TrailPoint endPoint = trailPoints.get(trailPoints.size() - 1);

        startPoint.circle.remove();
        startPoint.circle = mMap.addCircle(new CircleOptions()
                .center(startPoint.mLatlng)
                .radius(20)
                .strokeColor(Color.GREEN)
                .fillColor(Color.BLUE));

        endPoint.circle.remove();
        endPoint.circle = mMap.addCircle(new CircleOptions()
                .center(endPoint.mLatlng)
                .radius(20)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
//        TrailPoint nearestTrailPoint = getNearestTrailPoint(marker.getPosition(), mTrailPoints);
//        ArrayList<TrailPoint> path = findPath(mTrailPoints.get(0), nearestTrailPoint, mTrailPoints);
//        showTrailPoints(path);
        if(inGoMode) {
            if(selectedStartPoint != null) {
                TrailPoint endPoint = getNearestTrailPoint(marker.getPosition(), mTrailPoints);
                TrailPath path = findPath3(selectedStartPoint, endPoint, mTrailPoints);
                if(path != null) {
                    showTrailPoints(path.trailPoints);
                    Toast.makeText(getApplicationContext(), "" + getTotalDistance(path.trailPoints) + " meter path found!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "No Path found!", Toast.LENGTH_SHORT).show();
                selectedStartPoint = null;
            } else {
                selectedStartPoint = getNearestTrailPoint(marker.getPosition(), mTrailPoints);
                if(selectedStartPoint.circle != null)
                    selectedStartPoint.circle.remove();
                selectedStartPoint.circle = mMap.addCircle(new CircleOptions()
                        .center(selectedStartPoint.mLatlng)
                        .radius(20)
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.BLUE));
            }
            return true;
        } else {
            return false;
        }
    }

    //function long2tile(lon,zoom) { return (Math.floor((lon+180)/360*Math.pow(2,zoom))); }

    private long long2tile(double lon, int zoom) {
        return (long)Math.floor((lon+180)/360*Math.pow(2,zoom));
    }

    //function lat2tile(lat,zoom)  { return (Math.floor((1-Math.log(Math.tan(lat*Math.PI/180) + 1/Math.cos(lat*Math.PI/180))/Math.PI)/2 *Math.pow(2,zoom))); }

    private long lat2tile(double lat, int zoom) {
        return (long)(Math.floor((1-Math.log(Math.tan(lat*Math.PI/180) + 1/Math.cos(lat*Math.PI/180))/Math.PI)/2*Math.pow(2,zoom)));
    }

    private class CustomTileProvider implements TileProvider {
        int TILE_WIDTH = 256;
        int TILE_HEIGHT = 256;

        public Tile getTile(int x, int y, int zoom) {

            //byte[] image = readTileImage(x, y, zoom);
            String s = String.format("https://basemap.nationalmap.gov/arcgis/rest/services/USGSImageryTopo/MapServer/tile/%d/%d/%d",
                    zoom, y, x);

            Log.d("getTile", "Downloading tile x,y,z: " + x + " " + y + " " + zoom);
            //return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
            //Bitmap tileBitmap = getBitmap(s);
            File tileFile = getTileFile(x, y, zoom);

            Bitmap tileBitmap = null;

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(tileFile);
                tileBitmap = BitmapFactory.decodeStream(fis);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(tileBitmap == null) {
                tileBitmap = getBitmap(s);
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            tileBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Tile tile = new Tile(TILE_WIDTH, TILE_HEIGHT, byteArray);
            return tile;
        }

//        private byte[] readTileImage(int x, int y, int zoom) {
//            InputStream in = null;
//            ByteArrayOutputStream buffer = null;
//
//            try {
//                in = mAssets.open(getTileFilename(x, y, zoom));
//                buffer = new ByteArrayOutputStream();
//
//                int nRead;
//                byte[] data = new byte[BUFFER_SIZE];
//
//                while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
//                    buffer.write(data, 0, nRead);
//                }
//                buffer.flush();
//
//                return buffer.toByteArray();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            } catch (OutOfMemoryError e) {
//                e.printStackTrace();
//                return null;
//            } finally {
//                if (in != null) try { in.close(); } catch (Exception ignored) {}
//                if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
//            }
//        }

        private File getTileFile(int x, int y, int zoom) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("mapTilesDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath=new File(directory,"" + x + "-" + y + "-" + zoom);
            return mypath;
        }


    }

    public Bitmap getBitmap(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap d = BitmapFactory.decodeStream(is);
            is.close();
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
        }


        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(false);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

        tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

    /* Define the URL pattern for the tile images */
                String s = String.format("https://basemap.nationalmap.gov/arcgis/rest/services/USGSImageryTopo/MapServer/tile/%d/%d/%d",
                        zoom, y, x);

                if (!checkTileExists(x, y, zoom)) {
                    Log.d("TileProvider", "Tile Doesn't exist");
                    return null;
                } else {
                    Log.d("TileProvider", "Tile exists! " + x + " " + y + " " + zoom + " " + s);
                }

                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }

            /*
             * Check that the tile server supports the requested x, y and zoom.
             * Complete this stub according to the tile range you support.
             * If you support a limited range of tiles at different zoom levels, then you
             * need to define the supported x, y range at each zoom level.
             */
            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 0;
                int maxZoom = 20;

                if ((zoom < minZoom || zoom > maxZoom)) {
                    return false;
                }

                return true;
            }
        };

        tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(new CustomTileProvider())
                .zIndex(-10));
    }
}
