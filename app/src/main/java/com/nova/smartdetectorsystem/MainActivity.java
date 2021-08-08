package com.nova.smartdetectorsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public class CustomGridViewAdapter extends BaseAdapter {

        public Integer[] mThumbIds = {
                R.drawable.ic_baseline_error_outline_24,
                R.drawable.ic_baseline_error_outline_24,
                R.drawable.ic_baseline_error_outline_24,
                R.drawable.ic_baseline_error_outline_24,
                R.drawable.ic_baseline_error_outline_24,
                R.drawable.ic_baseline_error_outline_24
        };

        public String[] mThumbNames2 = {
                "Func-1", "Func-2", "Func-3", "Func-4", "Func-5", "Func-6",
        };

        private Context mContext;

        public CustomGridViewAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return mThumbIds[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolderItem viewHolder;

            if (convertView == null) {

                LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
                convertView = inflater.inflate(R.layout.row_grid, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.textViewItem = convertView.findViewById(R.id.textView);
                viewHolder.imageViewItem = convertView.findViewById(R.id.imageView);
                viewHolder.rel4 = convertView.findViewById(R.id.main_conv);

                convertView.setTag(viewHolder); }

            else
                viewHolder = (ViewHolderItem) convertView.getTag();

            viewHolder.textViewItem.setText(mThumbNames2[position]);
            viewHolder.textViewItem.setTag(position);
            viewHolder.imageViewItem.setBackground(getDrawable(mThumbIds[position]));

            return convertView; }

    }

    static class ViewHolderItem {
        TextView textViewItem;
        RelativeLayout rel4;
        ImageView imageViewItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Space(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Smart Detector System");

        GridView gridView = findViewById(R.id.gridViewCustom);
        gridView.setAdapter(new CustomGridViewAdapter(MainActivity.this));
        gridView.setBackgroundColor(Color.parseColor("#ffffff"));

    }

    private void Space(Bundle savedInstanceState){

        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Profile", R.drawable.ic_baseline_emoji_people_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("About", R.drawable.ic_baseline_error_outline_24));
        spaceNavigationView.setSpaceItemIconSize(100);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {


            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemName){
                    case "Profile":
                        startActivity(new Intent(MainActivity.this, ProfileScreen.class));
                        break;
                    case "About":
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("About This App")
                                .setMessage("We have built this application using many libraries such as Firebase, google MLK, and TensorFlow API.\n\n if you want to log out please do so...")
                                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                                editor.putString("username", "");
                                editor.putString("mobile", "");
                                editor.putString("email", "");
                                editor.putString("password", "");
                                editor.apply();

                                startActivity(new Intent(MainActivity.this, LoginScreenActivity.class));
                                finish();

                            }
                        }).setCancelable(false).show();
                        break;
                }

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemName){
                    case "Profile":
                        startActivity(new Intent(MainActivity.this, ProfileScreen.class));
                        break;
                        case "About":
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("About This App")
                                .setMessage("We have built this application using many libraries such as Firebase, google MLK, and TensorFlow API.\n\n if you want to log out please do so...")
                                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                                editor.putString("username", "");
                                editor.putString("mobile", "");
                                editor.putString("email", "");
                                editor.putString("password", "");
                                editor.apply();

                                startActivity(new Intent(MainActivity.this, LoginScreenActivity.class));
                                finish();

                            }
                        }).setCancelable(false).show();
                        break;
                }
            }
        });
    }

}