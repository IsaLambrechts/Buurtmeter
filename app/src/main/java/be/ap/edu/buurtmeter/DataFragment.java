package be.ap.edu.buurtmeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RequestQueue mRequestQueue;
    private JSONObject obj = new JSONObject();
    private SharedPreferences sharedPref = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataFragment newInstance(String param1, String param2) {
        DataFragment fragment = new DataFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_data, container, false);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String dataSets = sharedPref.getString("dataSets", "{}");
        try {
            obj = new JSONObject(dataSets);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(obj.length());
        saveData(obj);

        TextView dataSetID = view.findViewById(R.id.textView);
        ListView listView = view.findViewById(R.id.listview);
        try {
            String size = sharedPref.getString("dataSets", "{}");
            JSONObject json = new JSONObject(size);
            dataSetID.setText(String.format("Datasets (%d)", json.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] array = new String[obj.length()];
        if(obj.length() > 0) {
            JSONArray names = obj.names();
            for (int i = 0; i < names.length(); i++) {
                try {
                    String type = obj.getJSONObject(names.getString(i)).getString("type");
                    array[i] = type;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(array);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, array);

            listView.setAdapter(adapter);


        }

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
            Toast.makeText(context, "Data fragment attached", Toast.LENGTH_SHORT).show();
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

    public void saveData(JSONObject jsonObject) {
        if(jsonObject.length() == 0) {
            ((MainActivity) getActivity()).setTitle("Ophalen Data...");
            String url = "http://datasets.antwerpen.be/v1/opendata/statistieken.json";

            mRequestQueue = Volley.newRequestQueue(getActivity());

            final JSONObject sets = new JSONObject();
            JsonObjectRequest jr = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    obj = response;
                    JSONArray statistics = new JSONArray();
                    try {
                        statistics = obj.getJSONArray("statistieken");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < statistics.length(); i++) {
                        try {
                            if (statistics.getJSONObject(i).getString("package").equals("geografie")) {
                                final JSONArray finalStatistics = statistics;
                                final int finalI = i;
                                JsonObjectRequest jsonr = new JsonObjectRequest("http://datasets.antwerpen.be/v4/gis/" + statistics.getJSONObject(i).getString("resource") + ".json", new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONObject geo;
                                            if ((response.getJSONArray("data").length() > 0) && (response.getJSONArray("data").getJSONObject(0).has("geometry")) && ((geo = new JSONObject(response.getJSONArray("data").getJSONObject(0).getString("geometry"))).length() > 0)) {
                                                if (geo.getString("type").equals("Polygon")) {
                                                    String fileName = finalStatistics.getJSONObject(finalI).getString("resource") + ".json";
                                                    String resource = finalStatistics.getJSONObject(finalI).getString("resource");
                                                    String type = "";
                                                    if ((response.getJSONArray("data").getJSONObject(0).has("type")) && (response.getJSONArray("data").getJSONObject(0).getString("type").length() > 0)) {
                                                        type = response.getJSONArray("data").getJSONObject(0).getString("type");
                                                    } else {
                                                        type = resource;
                                                    }

                                                    JSONObject set = new JSONObject("{\"used\":" + false + ", \"range\":" + 5 + ", \"resource\":\"" + resource + "\", \"type\": \"" + type + "\"}");
                                                    sets.put(set.getString("resource"), set);

                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putString("dataSets", String.valueOf(sets));
                                                    editor.apply();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("error onErrorResponse");
                                        System.out.println(error);
                                        error.printStackTrace();
                                    }
                                });
                                mRequestQueue.add(jsonr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error onErrorResponse");
                    System.out.println(error);
                    error.printStackTrace();
                }
            });

            jr.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            mRequestQueue.add(jr);
        } else {
            ((MainActivity) getActivity()).setTitle("Buurtmeter");
        }
    }
}
