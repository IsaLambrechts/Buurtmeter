package be.ap.edu.buurtmeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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
    private DataAdapter dataAdapter;
    private TextView dataSetID;
    private ListView listView;

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
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        dataSetID = view.findViewById(R.id.textView);
        listView = view.findViewById(R.id.listview);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String dataSets = sharedPref.getString("dataSets", "{}");
        try {
            obj = new JSONObject(dataSets);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (obj.length() == 0) {
            saveData();
        } else {
            ((MainActivity) getActivity()).setTitle("Buurtmeter");
        }

        try {
            String size = sharedPref.getString("dataSets", "{}");
            JSONObject json = new JSONObject(size);
            dataSetID.setText(String.format("Datasets (%d)", json.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<Data> array = new ArrayList<>();
        if (obj.length() > 0) {
            array = loadArrayList(obj);

        }
        dataAdapter = new DataAdapter(getActivity(), array);
        listView.setAdapter(dataAdapter);



        view.invalidate();


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

    public void saveData() {

        ((MainActivity) getActivity()).setTitle("Ophalen Data...");
        String url = "http://datasets.antwerpen.be/v1/opendata/statistieken.json";

        mRequestQueue = Volley.newRequestQueue(getActivity());

        final JSONObject sets = new JSONObject();
        JsonObjectRequest jr = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // obj = response;
                JSONArray statistics = new JSONArray();
                try {
                    statistics = response.getJSONArray("statistieken");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < statistics.length(); i++) {
                    try {
                        if (statistics.getJSONObject(i).getString("package").equals("geografie")) {
                            final JSONArray finalStatistics = statistics;
                            final int finalI = i;
                            JSONArray finalStatistics1 = statistics;
                            int finalI1 = i;
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

                                                try {
                                                    if (finalStatistics1.getJSONObject(finalI1).equals(finalStatistics1.getJSONObject(finalStatistics1.length() - 1))) {
                                                        ((MainActivity) getActivity()).setTitle("Buurtmeter");
                                                        ArrayList<Data> listArray = loadArrayList(sets);
                                                        dataAdapter = new DataAdapter(getActivity(), listArray);
                                                        listView.setAdapter(dataAdapter);
                                                        dataAdapter.notifyDataSetChanged();
                                                        dataSetID.setText(String.format("Datasets (%d)", listArray.size()));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
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

    }

    private ArrayList<Data> loadArrayList(JSONObject sets) {
        ArrayList<Data> array = new ArrayList<>();
        JSONArray names = sets.names();
        for (int i = 0; i < names.length(); i++) {
            try {
                String type = sets.getJSONObject(names.getString(i)).getString("type");
                Boolean used = sets.getJSONObject(names.getString(i)).getBoolean("used");
                int amount = sets.getJSONObject(names.getString(i)).getInt("range");
                array.add(new Data(type, amount, used));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }
}
