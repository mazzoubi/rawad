package com.nova.rawad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminMainActivity extends AppCompatActivity {

    public class CustomGridViewAdapter extends BaseAdapter {

        public Integer[] mThumbIds = {
                R.drawable.ic_baseline_drive_eta_24,
                R.drawable.ic_person_black_24dp,
                R.drawable.ic_baseline_assignment_24,
                R.drawable.ic_baseline_assignment_ind_24
        };

        public String[] mThumbNames2 = {
                "سائقين", "ركاب", "تأشيرة", "رحلات"
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

                LayoutInflater inflater = (AdminMainActivity.this).getLayoutInflater();
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

    String pictureImagePath;
    TextView tvname, tvssn, tvmobile, tvaddress, tvage;
    AccountInfoClass info;
    CircleImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Permessions();

        tvname = findViewById(R.id.name);
        tvssn = findViewById(R.id.ssn);
        tvmobile = findViewById(R.id.mobile);
        tvaddress = findViewById(R.id.address);
        tvage = findViewById(R.id.age);
        img = findViewById(R.id.img);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("رواد الرمثا");

        GridView gridView = findViewById(R.id.gridViewCustom);
        gridView.setAdapter(new CustomGridViewAdapter(AdminMainActivity.this));
        gridView.setBackgroundColor(Color.parseColor("#ffffff"));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        startActivity(new Intent(AdminMainActivity.this, DriversActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(AdminMainActivity.this, PassengersActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(AdminMainActivity.this, VisaActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(AdminMainActivity.this, TripActivity.class));
                        break;
                }

            }
        });

    }

    private void Permessions(){
        int camera = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.CAMERA);
        int store1 = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int store2 = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        if (store1 != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (store2 != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1); }
    }

}