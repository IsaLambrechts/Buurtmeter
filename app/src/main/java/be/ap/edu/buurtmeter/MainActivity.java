package be.ap.edu.buurtmeter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MapView mapView = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.frame_fragmentholder, new MapFragment()).commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentTransaction.replace(R.id.frame_fragmentholder, new DataFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 23) {
//            checkPermissions();
//        }
        setContentView(R.layout.activity_main);

//        // https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
//        mapView = (MapView) findViewById(R.id.mapview);
//        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
//        mapView.setBuiltInZoomControls(true);
//        mapView.setMultiTouchControls(true);
//        mapView.getController().setZoom(16.0);
//        // default = meistraat
//
//        mapView.getController().setCenter(new GeoPoint(51.2164348, 4.4112339));
//
//        List<GeoPoint> geoPoints = new ArrayList<>();
////add your points here
//        geoPoints.add(new GeoPoint(51.2164348, 4.4112339));
//        geoPoints.add(new GeoPoint(51.229857372061, 4.4242370563038));
//        geoPoints.add(new GeoPoint(51.229829165066, 4.4244103034827));
//        Polygon polygon = new Polygon();    //see note below
//        polygon.setFillColor(Color.argb(75, 255,0,0));
//        geoPoints.add(geoPoints.get(0));    //forces the loop to close
//        polygon.setPoints(geoPoints);
//        polygon.setTitle("A sample polygon");
//
////polygons supports holes too, points should be in a counter-clockwise order
//        List<List<GeoPoint>> holes = new ArrayList<>();
//        holes.add(geoPoints);
//        polygon.setHoles(holes);
//
//        mapView.getOverlayManager().add(polygon);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragmentholder, new MapFragment()).commit();

    }

}
