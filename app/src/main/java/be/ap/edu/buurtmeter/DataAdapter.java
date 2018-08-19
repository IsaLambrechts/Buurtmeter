package be.ap.edu.buurtmeter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        dataText.setText(getItem(position).getName());
        checkBox.setChecked(getItem(position).getChecked());
        seekBar.setProgress(getItem(position).getAmount());


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                try {
                    String dataSets = sharedPref.getString("dataSets", "{}");
                    JSONObject myDataSets = new JSONObject(dataSets);
                    JSONArray names = myDataSets.names();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    for(int i = 0; i < names.length(); i++) {
                        if(dataText.getText().equals(myDataSets.getJSONObject(names.getString(i)).getString("type"))) {
                            myDataSets.getJSONObject(names.getString(i)).put("used", checkBox.isChecked());
                            System.out.println(myDataSets.getJSONObject(names.getString(i)));
                            System.out.println(myDataSets);

                            editor.putString("dataSets", String.valueOf(myDataSets));
                            editor.apply();
                        }
                    }
                    editor.putString("dataSets", String.valueOf(myDataSets));
                    editor.apply();
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
                    for(int j = 0; j < names.length(); j++) {
                        if(dataText.getText().equals(myDataSets.getJSONObject(names.getString(j)).getString("type"))) {
                            myDataSets.getJSONObject(names.getString(j)).put("range", seekBar.getProgress());
                        }
                    }
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
}
