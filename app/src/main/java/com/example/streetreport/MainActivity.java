package com.example.streetreport;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    private final static String MAP_API_KEY="f58847800a57c457527644cd5499088a";
    ImageButton btnLocation;
    ImageButton btnMenu;
    Button btnCurrentDanger;
    //GpsTracker
    private GpsInfo gps;

    public double lat;
    public double lon;

    public MapPoint myPoint = MapPoint.mapPointWithGeoCoord(lat,lon);
    public MapPoint circlePoint;
    public MapView mapView;

    //location permission flagment
    public static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    Api.Root root;
    ArrayList<MapCircle> mapDangerCircles = new ArrayList<MapCircle>();

    //bottom navigation bar listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;

                case R.id.navigation_share:
                    final Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.navigation_profile:
                    return true;
            }
            return false;
        }
    };

    public void onPopupButtonClick(View button){
        PopupMenu popup = new PopupMenu(this,button);
        popup.getMenuInflater().inflate(R.menu.popup,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.search:
                        mapDangerCircles.clear();
                        mapView.removeAllCircles();
                        mapDangerCircles=makeCircle(root.getSerchResult().getFrequentzone());

                        for(MapCircle c : mapDangerCircles){
                            c.setTag(1234);
                            mapView.addCircle(c);
                        }

                        MapCircle circle1 = new MapCircle(
                                MapPoint.mapPointWithGeoCoord(37.275071,127.044439), // center
                                150, // radius
                                Color.argb(128, 255, 0, 0), // strokeColor
                                Color.argb(128, 255, 0, 0) // fillColor
                        );
                        mapDangerCircles.add(circle1);
                        mapView.addCircle(mapDangerCircles.get(4));

                        break;
                    case R.id.add:
                        mapDangerCircles.clear();
                        mapView.removeAllCircles();
                        break;
                    case R.id.edit:
                        mapDangerCircles.clear();
                        mapView.removeAllCircles();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public ArrayList<MapCircle> makeCircle(ArrayList<Api.Spot> data){
        ArrayList<MapCircle> circles=new ArrayList<>();
        for(Api.Spot spot : data){
            circles.add(new MapCircle(
                            MapPoint.mapPointWithGeoCoord(Double.parseDouble(spot.getY_crd()),Double.parseDouble(spot.getX_crd())),
                            150,
                            Color.argb(128, 255, 0, 0), // strokeColor
                            Color.argb(128, 255, 0, 0) // fillColor
                    )
            );
        }
        return circles;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnLocation=(ImageButton)findViewById(R.id.btn_location);
        btnMenu=(ImageButton)findViewById(R.id.btn_menu);
        btnCurrentDanger=(Button)findViewById(R.id.btn_currentDanger);

        //map Init
        mapView=new MapView(this);
        mapView.setDaumMapApiKey(MAP_API_KEY);
        RelativeLayout viewGroup = (RelativeLayout) findViewById(R.id.map_view);
        viewGroup.addView(mapView);
        fetchAsyncPosts();

        //btnLocation 현재 위치 받는 버튼
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
        //btnLocation End


        //현재 위험도 버튼
        btnCurrentDanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDangerCircles.clear();
                mapView.removeAllCircles();
                mapDangerCircles=currentCircles();
                for(MapCircle c:mapDangerCircles){
                    mapView.addCircle(c);
                }

            }
        });//현재 위험도 버튼 End
    }//OnCreate End

    public ArrayList<MapCircle> currentCircles(){
        ArrayList<MapCircle> circles = new ArrayList<>();
        circles.add(makeCurrentCircle(37.279677,127.043763));
        circles.add(makeCurrentCircle(37.278217,127.043806));
        circles.add(makeCurrentCircle(37.274563,127.04401));
        circles.add(makeCurrentCircle(37.277159,127.045512));
        circles.add(makeCurrentCircle(37.276561,127.042455));
        circles.add(makeCurrentCircle(37.280394,127.042487));

        return circles;
    }

    private MapCircle makeCurrentCircle(double x, double y){
        double dangerPercent=calculDanger(x,y);
        int redRgb=calculColor(dangerPercent);
        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(x,y),
                100,
                Color.argb(128,redRgb,0,255-redRgb),
                Color.argb(128,redRgb,0,255-redRgb)
        );
        return circle;
    }

    private int calculColor(double percent){
        double rgbNum;
        rgbNum=percent/0.3*255;
        return ((int) rgbNum);
    }

    private double calculDanger(double x,double y){
        double weather=1;
        double location=1;
        double situation=1;
        double age=1;
        double day=1;
        double sex=1;
        double dangerPercent=1;

        if(((x>37.279271)&&(x<37.279954)&&(y>127.043452)&&(y<127.04401))||
                ((x>37.274217)&&(x<37.2749)&&(y>127.043839)&&y<127.044354)){
            location=0.24587/0.1;
        }else{
            location=0.70661/0.5;
        }
        if((x<37.280532&&x>37.280237&&y<127.042744&&y>127.042701)
                ||(x<37.278023&&x>37.278003&&y<127.047325&&y>127.047305)
                ||(x<37.278346&&x>37.278312&&y>127.043715&&y<127.043892)
                ||(x<37.2751249&&x>37.274923&&y<127.042674&&y>127.0425291)){
            situation=0.33846;
        }else{
            situation=0.05715;
        }
        weather=0.08074/0.8;

        dangerPercent=location*situation*weather;
        return dangerPercent;
    }



    //json get &json_parse using gson
    private void fetchAsyncPosts(){
        FetchPostsTask fetchPostsTask = new FetchPostsTask();
        fetchPostsTask.execute(Api.GET_POST);
    }

    class FetchPostsTask extends AsyncTask<String, Void, Api.Root> {
        @Override
        protected Api.Root doInBackground(String... strings) {
            String url = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new Gson();
                root = gson.fromJson(response.body().charStream(),Api.Root.class);

                Log.i("bbb", root.getSerchResult().getFrequentzone().get(1).getX_crd());

                return  root;
            } catch (IOException e) {
                Log.d("FetchPostsTask",e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(Api.Root s) {
            super.onPostExecute(s);
            Log.d("FetchPostsTask",s.toString());
        }
    }//FetchPostsTask class End
    //json parse End
}
