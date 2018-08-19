package be.ap.edu.buurtmeter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MapView mapView;

    private SharedPreferences sharedPref = null;
    private String dataSets = "";


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        dataSets = sharedPref.getString("dataSets", "{}");
        try {
            getAreaScore(0.0, 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);
        // default = meistraat

        mapView.getController().setCenter(new GeoPoint(51.2164348, 4.4112339));

        mapView.setOnTouchListener((View v, MotionEvent event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {

                Double[] coords = {4.104122829437256, 100.220808703284156};
                Double[][] polyLoc = {{4.4033358456473,51.22758255433},{4.4034703234995,51.227582046742},{4.4035987670495,51.227590421318},{4.403891986069,51.227587825078},{4.4043671091547,51.227584138739},{4.4052564789258,51.227567503507},{4.4052396366633,51.227486379831},{4.4052188326158,51.227301873226},{4.4052142775621,51.227046883726},{4.4052126341747,51.226942691106},{4.4052107187617,51.226820476893},{4.4052032351956,51.226752662019},{4.4051763990275,51.226524698492},{4.4051334954029,51.226414748261},{4.4050735111994,51.226337008681},{4.404932670118,51.225997822659},{4.4048141024632,51.225728271398},{4.4047689376755,51.225635201864},{4.404849230603,51.225437657459},{4.4049040172207,51.225316378281},{4.4049316496089,51.225250475914},{4.4049534374109,51.225215504351},{4.4049874082081,51.22518218284},{4.4047616490268,51.225032030459},{4.4049989459234,51.224784902143},{4.4050492304226,51.224691856755},{4.4052179998964,51.224443122487},{4.4053287368647,51.224283220396},{4.4054114579518,51.224156440222},{4.405445728228,51.224123091505},{4.4056381279004,51.223858628435},{4.4059331728298,51.223436763666},{4.4061097134491,51.223162642326},{4.4059316709124,51.223103250344},{4.4059504072707,51.222855911089},{4.405950337694,51.222770296786},{4.4059230384202,51.222220504197},{4.4059539500091,51.22182569753},{4.4056115350473,51.221836215324},{4.405375774889,51.221834510266},{4.4051180384544,51.221826555711},{4.4050260876278,51.221807897644},{4.405055544185,51.221770874265},{4.4050720226658,51.221721298169},{4.4050913345453,51.221634868804},{4.405095198886,51.221472303663},{4.4051021093513,51.22141456011},{4.4051142775742,51.221362792185},{4.4051316167322,51.221315831422},{4.4051618070134,51.221266017306},{4.4052098053406,51.221183120326},{4.4052354891992,51.22097481538},{4.4052607594262,51.220535401105},{4.4052907932025,51.220057362135},{4.4052803629469,51.219900922482},{4.4052534304668,51.219709235539},{4.4052089760265,51.219423652347},{4.4051318407502,51.219041274359},{4.4050323251421,51.218482550945},{4.4048586788009,51.218401628661},{4.4048219626097,51.218323189361},{4.4047076221575,51.218216514615},{4.4044098727073,51.217876557488},{4.4041262938595,51.217551399138},{4.4040449554013,51.217474725914},{4.4039604870904,51.217459345477},{4.4035751577647,51.217382494194},{4.4031168156245,51.217428705528},{4.4026922414805,51.217464200014},{4.4025617748151,51.217469055942},{4.4024262973164,51.217530099666},{4.402120174986,51.217572460695},{4.4018286206559,51.217612974148},{4.4015522174057,51.21764635479},{4.4010124337083,51.217712822877},{4.4004936396882,51.217772154941},{4.4002925897595,51.217785980152},{4.4001233880607,51.217783958829},{4.3996448291794,51.217787377402},{4.3992631684816,51.217796476135},{4.39915907353,51.217815855584},{4.3990554223896,51.217835558406},{4.3987858561457,51.217889145691},{4.39865709284,51.21787490551},{4.3982816252291,51.217945228435},{4.3981573000221,51.217977294799},{4.3974076370392,51.218167740663},{4.3969797989347,51.218289135512},{4.3968927343719,51.218355742814},{4.3967715764833,51.218374800733},{4.3960559039442,51.218462959774},{4.3959152448685,51.218494443394},{4.3957908583414,51.218524862399},{4.3956967386069,51.218548344149},{4.3956015429037,51.21856770939},{4.3954917528172,51.21859475403},{4.3954084356824,51.218612938879},{4.3953519225298,51.218625275027},{4.3953933361928,51.218686665184},{4.3941734181506,51.218971738193},{4.3943409819746,51.21918406983},{4.394738488548,51.219756721126},{4.3953449576275,51.220640015549},{4.3957725532307,51.221271251386},{4.3959825916393,51.221588060516},{4.3961609731884,51.221860599985},{4.3969286307093,51.223026387884},{4.396929289768,51.223027385433},{4.3969294473771,51.223027637069},{4.3973861286978,51.223725553142},{4.3978522736028,51.22444967498},{4.3982412185513,51.2250608846},{4.3983824341472,51.225284784768},{4.3985682594129,51.225579995967},{4.3987990966322,51.225946167311},{4.3990123280173,51.226287642727},{4.3994106028616,51.226933280054},{4.399588192024,51.227223143568},{4.4009258086323,51.2268608089},{4.400968009442,51.226901172903},{4.4011507742367,51.227067604407},{4.4012000488543,51.227110393203},{4.4012455351216,51.227160328791},{4.4013213311376,51.227236402993},{4.4013971349224,51.22730297644},{4.4016812737321,51.227594442568},{4.4018548436323,51.227589539374},{4.4022520934613,51.227588562295},{4.40261504338,51.227587108537},{4.4033358456473,51.22758255433}};
                System.out.println(inPolygon(coords, polyLoc));
                //do something
                int X = (int) event.getX();
                int Y = (int) event.getY();

                GeoPoint geoPoint = (GeoPoint) mapView.getProjection().fromPixels(X, Y);
                System.out.println(geoPoint.getLatitude() + " " + geoPoint.getLongitude());
                // addMarker(geoPoint, mapView);
                Marker startMarker = new Marker(mapView);
                startMarker.setPosition(geoPoint);
                startMarker.setIcon(getResources().getDrawable(R.drawable.marker));
                startMarker.setTitle("test");
                startMarker.setSubDescription("Test description");
                startMarker.showInfoWindow();
                startMarker.setRelatedObject(null);
//                startMarker.showInfoWindow();
                mapView.getOverlays().add(startMarker);
                mapView.invalidate();

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                return false;
            }
            return true;
        });

        try {
            JSONArray areas = new JSONArray(loadJSONFromAsset(getActivity()));
            for (int i = 0; i < areas.length(); i++) {
                JSONObject area = new JSONObject(areas.getJSONObject(i).getString("geometry"));
                String title = areas.getJSONObject(i).getString("wijknaam");
                JSONArray coordinates = area.getJSONArray("coordinates");
                for (int j = 0; j < coordinates.length(); j++) {
                    List<GeoPoint> geoPoints = new ArrayList<>();
                    for (int k = 0; k < coordinates.getJSONArray(j).length(); k++) {
                        JSONArray coords = coordinates.getJSONArray(j).getJSONArray(k);
                        geoPoints.add(new GeoPoint(coords.getDouble(1), coords.getDouble(0)));
                    }
                    Polygon polygon = new Polygon();    //see note below
                    geoPoints.add(geoPoints.get(0));    //forces the loop to close
                    polygon.setPoints(geoPoints);
                    polygon.setTitle(title);
                    mapView.getOverlayManager().add(polygon);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String dataSets = sharedPref.getString("dataSets", "{}");


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "Map fragment attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        String message = "osmdroid permissions:";
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nLocation to show user location.";
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            message += "\nLocation to show user location.";
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            message += "\nStorage access to store map tiles.";
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        } // else: We already have permissions, so handle as normal
    }


    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("areas.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


    private void locate() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        checkPermissions();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            System.out.println("location: " + location.getLatitude());
        } else {
            System.out.println("location is null");
        }
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//
//        System.out.println(longitude + latitude);

    }

    private boolean inPolygon(Double[] location, Double[][] polyLoc) {
        Double[] lastPoint = polyLoc[polyLoc.length-1];
        Boolean isInside = false;
        Double x = location[0];

        for(int i = 0; i < polyLoc.length; i++) {
            Double[] point = polyLoc[i];
            Double x1 = lastPoint[0];
            Double x2 = point[0];
            Double dx = x2 - x1;

            if(Math.abs(dx) > 180.0) {
                if(x > 0){
                    while(x1 < 0)
                        x1 += 360;
                    while(x2 < 0)
                        x2 += 360;
                } else {
                    while(x1 > 0)
                        x1 -= 360;
                    while(x2 > 0)
                        x2 -= 360;
                }
                dx = x2 - x1;
            }

            if((x1 <= x && x2 > x ) || (x1 >= x && x2 < x)) {
                Double grad = (point[1] - lastPoint[1]) / dx;
                Double intersectAtLat = lastPoint[1] + ((x - x1) * grad);

                if(intersectAtLat > location[1])
                    isInside = !isInside;
            }
            lastPoint = point;
        }
        return isInside;
    }

    private void getAreaScore(Double lat, Double lng) throws JSONException {
        // Calculate area score of position
        JSONObject myDataSets = new JSONObject(dataSets);
        System.out.println(myDataSets);
        JSONArray keys = myDataSets.names ();
        System.out.println(keys);

        int totalScore = 0;
        for(int i = 0; i < keys.length(); i++) {
            if(myDataSets.getJSONObject(keys.getString(i)).getBoolean("used")) {
                System.out.println(myDataSets.getJSONObject(keys.getString(i)).getString("type"));
                int tempScore = 0;

            }
        }
    }

    private void addMarker() {
        // add marker to your map
    }

}
