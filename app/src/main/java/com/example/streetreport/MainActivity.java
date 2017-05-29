package com.example.streetreport;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MainActivity extends Activity {

    private final static String MAP_API_KEY="f58847800a57c457527644cd5499088a";
    ImageButton btnLocation;
    //GpsTracker
    private GpsInfo gps;

    public double lat;
    public double lon;
    public MapPoint myPoint = MapPoint.mapPointWithGeoCoord(lat,lon);


    public static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;

                case R.id.navigation_dashboard:
                    final Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnLocation=(ImageButton)findViewById(R.id.btn_location);
        //map Init
        final MapView mapView=new MapView(this);
        mapView.setDaumMapApiKey(MAP_API_KEY);
        RelativeLayout viewGroup = (RelativeLayout) findViewById(R.id.map_view);
        viewGroup.addView(mapView);

        btnLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //지도 permission 권한 받기
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                            PERMISSION_ACCESS_COARSE_LOCATION);
                }
                gps =new GpsInfo(MainActivity.this);
                if(gps.isGetLocation()){
                    lat=gps.getLatitude();
                    lon =gps.getLongitude();
                    myPoint = MapPoint.mapPointWithGeoCoord(lat,lon);

                }else{
                    gps.showSettingsAlert();
                }

                Log.i("AAA","lat :"+Double.toString(lat)+" lon : "+Double.toString(lon));
                mapView.setMapCenterPoint(myPoint,true);

            }
        });
    }
}
