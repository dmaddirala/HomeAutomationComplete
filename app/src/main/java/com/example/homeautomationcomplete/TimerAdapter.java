package com.example.homeautomationcomplete;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.homeautomationcomplete.R;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TimerAdapter extends ArrayAdapter<PlantTimer> {

    Context context;
    ArrayList<PlantTimer> items;

    public TimerAdapter(Activity context, ArrayList<PlantTimer> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view_item, parent, false);
        }

        final PlantTimer currentItem = getItem(position);

        final TextView serialNumber = (TextView) listItemView.findViewById(R.id.tv_serial_number);
        final TextView timeTv = (TextView) listItemView.findViewById(R.id.tv_time);
        final TextView dateTv = (TextView) listItemView.findViewById(R.id.tv_date);

        String currentTime = currentItem.getTime();
        String currentDate = currentItem.getDate();

        serialNumber.setText( (position+1) + ". " );
        timeTv.setText(currentTime);
        dateTv.setText("-"+currentDate);

        return listItemView;
    }
}

