package me.divytiwari.deviceinformation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by divya on 4/1/18.
 */

public class ListAdapter extends ArrayAdapter {

    int view;
    ArrayList<AppData> appDataobj;
    Context context;


    public ListAdapter(Context context, int view, int id, ArrayList<AppData> appData){
        super(context, view, appData);
        this.context = context;
        this.view = view;
        this.appDataobj = appData;
    }

    public View getView(int position, View convertview, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(view, parent, false);
        ImageView imageView = (ImageView) itemview.findViewById(R.id.imageapp);
        imageView.setImageDrawable(appDataobj.get(position).getIcon());
        TextView txtnameview = itemview.findViewById(R.id.txt_title);
        txtnameview.setText(appDataobj.get(position).getTitle());
        return itemview;
    }
}
