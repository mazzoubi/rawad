package com.nova.rawad.Users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.rawad.R;

import java.util.ArrayList;

public class RequestAdapter extends ArrayAdapter<String> {

    String a ;

    public RequestAdapter(Context context, int view, ArrayList<String> arrayList){
        super(context,view,arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View myView = layoutInflater.inflate(R.layout.row_request_adapter,parent,false);

       // TextView text=myView.findViewById(R.id.txvText);

        a=getItem(position);

       // text.setText(a);

        return myView ;
    }
}