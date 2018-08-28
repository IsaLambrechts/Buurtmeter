package be.ap.edu.buurtmeter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
    private ArrayList<Marker> myMarkers= new ArrayList<>();
    private Bitmap bmp = null;



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
//        try {
//            getAreaScore(51.21771754098431, 4.413907527923584);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        // https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);
        // default = meistraat

        mapView.getController().setCenter(new GeoPoint(51.2164348, 4.4112339));
        try {
            loadMarkers(mapView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mapView.setOnTouchListener((v, event) -> {
            long LONG_CLICK = 1000L;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getEventTime() - event.getDownTime() < LONG_CLICK) {
                    int X = (int) event.getX();
                    int Y = (int) event.getY();

                    GeoPoint geoPoint = (GeoPoint) mapView.getProjection().fromPixels(X, Y);
                    System.out.println(geoPoint.getLatitude() + " " + geoPoint.getLongitude());
                    try {
                        MapFragment.this.addMarker(geoPoint, mapView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                } else if (event.getEventTime() - event.getDownTime() >= LONG_CLICK) {
                    System.out.println("longclick");
//                    mapView.getOverlays().clear();
//                    mapView.invalidate();

                    removeMarker(mapView);


                    sharedPref.edit().putString("mapMarkers", "{}").apply();
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                return false;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                return false;
            } else {
                return true;
            }
            return false;
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
        if (location != null) {
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
        Double[] lastPoint = polyLoc[polyLoc.length - 1];
        Boolean isInside = false;
        Double x = location[0];

        for (int i = 0; i < polyLoc.length; i++) {
            Double[] point = polyLoc[i];
            Double x1 = lastPoint[0];
            Double x2 = point[0];
            Double dx = x2 - x1;

            if (Math.abs(dx) > 180.0) {
                if (x > 0) {
                    while (x1 < 0)
                        x1 += 360;
                    while (x2 < 0)
                        x2 += 360;
                } else {
                    while (x1 > 0)
                        x1 -= 360;
                    while (x2 > 0)
                        x2 -= 360;
                }
                dx = x2 - x1;
            }

            if ((x1 <= x && x2 > x) || (x1 >= x && x2 < x)) {
                Double grad = (point[1] - lastPoint[1]) / dx;
                Double intersectAtLat = lastPoint[1] + ((x - x1) * grad);

                if (intersectAtLat > location[1])
                    isInside = !isInside;
            }
            lastPoint = point;
        }
        return isInside;
    }

    private JSONObject getAreaScore(Double lat, Double lng) throws JSONException {
        // Calculate area score of position
        JSONObject myDataSets = new JSONObject(dataSets);
        System.out.println(myDataSets);
        JSONArray keys = myDataSets.names();
        System.out.println(keys);
        JSONObject totalResult = new JSONObject();
        Double totalScore = 0.0;
        for (int i = 0; i < keys.length(); i++) {
            if (myDataSets.getJSONObject(keys.getString(i)).getBoolean("used")) {
                Double tempScore = 0.0;
                JSONObject set = new JSONObject(sharedPref.getString(myDataSets.getJSONObject(keys.getString(i)).getString("resource") + ".json", "{}"));
                JSONArray setData = set.getJSONArray("data");
                for (int j = 0; j < setData.length(); j++) {
                    String geometry = setData.getJSONObject(j).getString("geometry");
                    JSONArray coords = new JSONObject(geometry).getJSONArray("coordinates").getJSONArray(0);
                    Double[][] coord = new Double[coords.length()][2];
                    for (int k = 0; k < coords.length(); k++) {
                        for (int l = 0; l < coords.getJSONArray(k).length(); l++) {
                            coord[k][l] = coords.getJSONArray(k).getDouble(l);
                        }
                    }

                    if (inPolygon(new Double[]{lng, lat}, coord)) {
                        tempScore += 1;
                    }
                }
                totalScore += tempScore * (myDataSets.getJSONObject(keys.getString(i)).getDouble("range") / 10);
                totalResult.put(myDataSets.getJSONObject(keys.getString(i)).getString("resource"), new JSONObject());
                totalResult.getJSONObject(myDataSets.getJSONObject(keys.getString(i)).getString("resource")).put("name", myDataSets.getJSONObject(keys.getString(i)).getString("type"));
                totalResult.getJSONObject(myDataSets.getJSONObject(keys.getString(i)).getString("resource")).put("score", tempScore * (myDataSets.getJSONObject(keys.getString(i)).getDouble("range") / 10));

            }
        }
        totalResult.put("total", totalScore);
        System.out.println(totalResult);
        return totalResult;
    }

    private void addMarker(GeoPoint geoPoint, MapView mapView1) throws JSONException {
        // add marker to your map
        String mapMarkers = sharedPref.getString("mapMarkers", "{}");
        JSONObject myMapmarkers = new JSONObject(mapMarkers);
        JSONArray markerNames = myMapmarkers.names();
        int markerCount = 0;
        if (markerNames != null)
            markerCount = markerNames.length();
        Double lat = geoPoint.getLatitude();
        Double lng = geoPoint.getLongitude();
        JSONArray areas = new JSONArray(loadJSONFromAsset(getActivity()));
        System.out.println(areas);
        for (int i = 0; i < areas.length(); i++) {
            JSONObject geometry = new JSONObject(areas.getJSONObject(i).getString("geometry"));
            JSONArray coords = geometry.getJSONArray("coordinates").getJSONArray(0);
            Double[][] coord = new Double[coords.length()][2];
            for (int k = 0; k < coords.length(); k++) {
                for (int l = 0; l < coords.getJSONArray(k).length(); l++) {
                    coord[k][l] = coords.getJSONArray(k).getDouble(l);
                }
            }
            if (inPolygon(new Double[]{lng, lat}, coord)) {
                JSONObject scores = getAreaScore(lat, lng);
                String title = areas.getJSONObject(i).getString("wijknaam");
                String description = "<div>Score: " + scores.getDouble("total") + "</div><br/>";
                if (scores.getDouble("total") > 0) {
                    JSONArray jsonArray = scores.names();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        if (jsonArray.get(j) != "total") {
                            description += scores.getJSONObject(jsonArray.getString(j)).getString("name") + " : " + scores.getJSONObject(jsonArray.getString(j)).getString("score") + "<br/>";
                        }
                    }
                }

                Marker startMarker = new Marker(mapView1);
                startMarker.setPosition(geoPoint);
                startMarker.setIcon(getResources().getDrawable(R.drawable.marker));
                startMarker.setTitle(title);
                startMarker.setSubDescription(description);
//                startMarker.showInfoWindow();
//                startMarker.setRelatedObject(null);
                MyCustomInfoWindow infoWindow = new MyCustomInfoWindow(R.layout.custom_info_window, mapView1, getActivity(), bmp);
                startMarker.setImage(getResources().getDrawable(R.drawable.ic_home_black_24dp));
                startMarker.setInfoWindow(infoWindow);
                startMarker.showInfoWindow();

                mapView1.getOverlays().add(startMarker);
                mapView1.invalidate();
                myMarkers.add(startMarker);

                myMapmarkers.put("Marker" + markerCount, new JSONObject());
                myMapmarkers.getJSONObject("Marker" + markerCount).put("lat", geoPoint.getLatitude()).put("lng", geoPoint.getLongitude()).put("title", title).put("description", description);
                System.out.println(myMapmarkers);
                sharedPref.edit().putString("mapMarkers", myMapmarkers.toString()).apply();
            }
        }
    }

    private void loadMarkers(MapView mapView1) throws JSONException {
        String mapMarkers = sharedPref.getString("mapMarkers", "{}");
        System.out.println(mapMarkers);
        JSONObject myMapMarkers = new JSONObject(mapMarkers);
        JSONArray markerNames = myMapMarkers.names();
        if (markerNames != null) {
            for (int i = 0; i < markerNames.length(); i++) {
                Double lat = myMapMarkers.getJSONObject(markerNames.getString(i)).getDouble("lat");
                Double lng = myMapMarkers.getJSONObject(markerNames.getString(i)).getDouble("lng");
                String title = myMapMarkers.getJSONObject(markerNames.getString(i)).getString("title");
                String description = myMapMarkers.getJSONObject(markerNames.getString(i)).getString("description");
                GeoPoint geoPoint = new GeoPoint(lat, lng);
                Marker startMarker = new Marker(mapView1);
                startMarker.setPosition(geoPoint);
                startMarker.setIcon(getResources().getDrawable(R.drawable.marker));
                //startMarker.setTitle(title);
                //startMarker.setSubDescription(description);
                //startMarker.showInfoWindow();
                //startMarker.setInfoWindow(new MyCustomInfoWindow(mapView1));
                //startMarker.setRelatedObject(null);
                mapView1.getOverlays().add(startMarker);
                mapView1.invalidate();
                myMarkers.add(startMarker);
            }
        }

    }

    private void removeMarker(MapView mapView1) {
        for(Marker m: myMarkers){
            m.closeInfoWindow();
            mapView1.getOverlays().remove(m);
        }
        mapView1.invalidate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result");
        if(requestCode == 1313){
            if(resultCode == Activity.RESULT_OK) {
                bmp = (Bitmap) data.getExtras().get("data");
            }
        }
    }

}
