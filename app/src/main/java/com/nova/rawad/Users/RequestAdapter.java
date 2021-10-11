package com.nova.rawad.Users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.rawad.PassengerPassportsClass;
import com.nova.rawad.R;

import java.util.ArrayList;

public class RequestAdapter extends ArrayAdapter<PassengerPassportsClass> {

    PassengerPassportsClass a ;

    public RequestAdapter(Context context, int view, ArrayList<PassengerPassportsClass> arrayList){
        super(context,view,arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View myView = layoutInflater.inflate(R.layout.row_request_adapter,parent,false);

        TextView textSerial=myView.findViewById(R.id.textView38);
        TextView textName=myView.findViewById(R.id.textView37);
        TextView textMother=myView.findViewById(R.id.textView36);
        TextView textNum=myView.findViewById(R.id.textView35);
        TextView textIss=myView.findViewById(R.id.textView34);

        a=getItem(position);

       // text.setText(a);

        textIss.setText(a.p_issue);
        textSerial.setText(position+"");
        textName.setText(a.p_name);
        textMother.setText("mother");
        textNum.setText(a.p_num);
        return myView ;
    }
}