package be.ap.edu.buurtmeter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
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
import java.util.Map;

public class DataAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Data> mDataSource;
    private SharedPreferences sharedPref = null;

    public DataAdapter(Context context, ArrayList<Data> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Data getItem(int position) {
        return mDataSource.get(position);

    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item, parent, false);
        Activity act = (Activity) mContext;
        sharedPref = act.getPreferences(Context.MODE_PRIVATE);

        // Get data element
        TextView dataText =
                (TextView) rowView.findViewById(R.id.lstvw_textView);

        // Get checkbox element
        CheckBox checkBox =
                (CheckBox) rowView.findViewById(R.id.itemCheckBox);
        checkBox.setTag(dataText.getText());

        // Get Seekbar element
        SeekBar seekBar =
                (SeekBar) rowView.findViewById(R.id.seekBar);


        dataText.setText(mDataSource.get(position).getName());
        checkBox.setChecked(mDataSource.get(position).getChecked());
        seekBar.setProgress(mDataSource.get(position).getAmount());



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                try {
                    String dataSets = sharedPref.getString("dataSets", "{}");
                    JSONObject myDataSets = new JSONObject(dataSets);
                    JSONArray names = myDataSets.names();
                    SharedPreferences.Editor editor = sharedPref.edit();

                    myDataSets.getJSONObject(names.getString(position)).put("used", checkBox.isChecked());
                    editor.putString("dataSets", String.valueOf(myDataSets));
                    editor.apply();
                    load(dataText.getText().toString(), position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    String dataSets = sharedPref.getString("dataSets", "{}");
                    JSONObject myDataSets = new JSONObject(dataSets);
                    JSONArray names = myDataSets.names();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    myDataSets.getJSONObject(names.getString(position)).put("range", seekBar.getProgress());
                    editor.putString("dataSets", String.valueOf(myDataSets));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rowView;
    }

    private void load(String name, int i) throws JSONException {
        String dataSets = sharedPref.getString("dataSets", "{}");
        JSONObject myDataSets = new JSONObject(dataSets);
        JSONArray names = myDataSets.names();


        if (myDataSets.getJSONObject(names.getString(i)).getBoolean("used")) {
            if (!sharedPref.contains(myDataSets.getJSONObject(names.getString(i)).getString("resource") + ".json")) {
                RequestQueue mRequestQueue = Volley.newRequestQueue((Activity) mContext);
                String url = "http://datasets.antwerpen.be/v4/gis/";
                final JSONObject sets = new JSONObject();
                int finalI = i;
                JsonObjectRequest jr = new JsonObjectRequest(url + myDataSets.getJSONObject(names.getString(i)).getString("resource") + ".json", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject obj = response;
                        try {
                            sharedPref.edit().putString(myDataSets.getJSONObject(names.getString(finalI)).getString("resource") + ".json", obj.toString()).apply();
                            Map<String, ?> prefsMap = sharedPref.getAll();
                            for (Map.Entry<String, ?> entry: prefsMap.entrySet()) {
                                Log.v("SharedPreferences", entry.getKey() + ":" +
                                        entry.getValue().toString());
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
        }


    }
}
