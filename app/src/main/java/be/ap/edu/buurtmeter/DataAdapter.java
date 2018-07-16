package be.ap.edu.buurtmeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mDataSource;

    public DataAdapter(Context context, ArrayList<String> items) {
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
    public Object getItem(int position) {
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

        // Get data element
        TextView dataText =
                (TextView) rowView.findViewById(R.id.lstvw_textView);

        // Get checkbox element
        CheckBox checkBox =
                (CheckBox) rowView.findViewById(R.id.itemCheckBox);

        // Get Seekbar element
        SeekBar seekBar =
                (SeekBar) rowView.findViewById(R.id.seekBar);

        dataText.setText(getItem(position).toString());
        checkBox.isChecked();


        return rowView;
    }
}
