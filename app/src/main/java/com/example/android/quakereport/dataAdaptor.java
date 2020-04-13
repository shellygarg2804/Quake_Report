package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;

public class dataAdaptor extends ArrayAdapter {


    public dataAdaptor(@NonNull EarthquakeActivity context, ArrayList<data> earthquake, int background) {
        super(context, 0, earthquake);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View ListItemView= convertView;
        if(ListItemView==null)
        {
            ListItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_view, parent, false);
        }

        data current = (data) getItem(position);


        double magnitude=current.getMagnitude();
        DecimalFormat magnitudeformatter = new DecimalFormat("0.0");
        String mag=magnitudeformatter.format(magnitude);
        TextView textView1 = (TextView)ListItemView.findViewById(R.id.magnitude);
        textView1.setText(mag);



        String place=current.getPlace();
        String locationoffset="Near By";
        String primarylocation=place;
        for (int i = 0; i <place.length() ; i++) {
            if (place.charAt(i)=='o'&& place.charAt(i+1)=='f')
            {
                locationoffset=place.substring(0,i+2);
                primarylocation=place.substring(i+3);
                break;
            }
        }
        TextView textView2 = (TextView)ListItemView.findViewById(R.id.location_offset);
        textView2.setText(locationoffset);
        TextView textView5 = (TextView)ListItemView.findViewById(R.id.primary_location);
        textView5.setText(primarylocation);


        Date dateObject = new Date(current.getTimeInMilliSeconds());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM, yyyy");
        String date = dateFormat.format(dateObject);
        TextView textView3 = (TextView)ListItemView.findViewById(R.id.date);
        textView3.setText(date);

        TextView textview4= (TextView)ListItemView.findViewById(R.id.time);
        dateFormat = new SimpleDateFormat("HH:mm");
        String time= dateFormat.format(dateObject);
        textview4.setText(time);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) textView1.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(current.getMagnitude());
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        return  ListItemView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}

