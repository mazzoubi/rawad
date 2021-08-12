package com.nova.smartdetectorsystem;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Space(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Smart Detector System");

        GridView gridView = findViewById(R.id.gridViewCustom);
        gridView.setAdapter(new CustomGridViewAdapter(AdminMainActivity.this));
        gridView.setBackgroundColor(Color.parseColor("#ffffff"));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1996);
                        break;
                    case 1:
                        RecognizeFace("https://firebasestorage.googleapis.com/v0/b/smartdetectorsystem.appspot.com/o/image1628761511118553329?alt=media&token=3ef337d0-65c8-47a8-9bbc-d1b7b4567946");
                        break;
                    case 2:
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        pictureImagePath = "";
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = timeStamp + ".jpg";
                        File storageDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES);
                        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
                        File file = new File(pictureImagePath);
                        Uri outputFileUri = Uri.fromFile(file);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                        int camera = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.CAMERA);
                        int read = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        int write = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
                        if (camera != PackageManager.PERMISSION_GRANTED){
                            listPermissionsNeeded.add(Manifest.permission.CAMERA);
                            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded.toArray
                                    (new String[listPermissionsNeeded.size()]), 1); }
                        if (read != PackageManager.PERMISSION_GRANTED){
                            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded.toArray
                                    (new String[listPermissionsNeeded.size()]), 2); }
                        if (write != PackageManager.PERMISSION_GRANTED){
                            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded.toArray
                                    (new String[listPermissionsNeeded.size()]), 3); }
                        else{
                            startActivityForResult(cameraIntent, 1997);}
                        break;
                }

            }
        });

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
                        startActivity(new Intent(AdminMainActivity.this, ProfileScreen.class));
                        break;
                    case "About":
                        new AlertDialog.Builder(AdminMainActivity.this)
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

                                startActivity(new Intent(AdminMainActivity.this, LoginScreenActivity.class));
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
                        startActivity(new Intent(AdminMainActivity.this, ProfileScreen.class));
                        break;
                    case "About":
                        new AlertDialog.Builder(AdminMainActivity.this)
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
                                editor.putString("email", "");
                                editor.putString("password", "");
                                editor.apply();

                                startActivity(new Intent(AdminMainActivity.this, LoginScreenActivity.class));
                                finish();

                            }
                        }).setCancelable(false).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == 1997){

                Toast.makeText(this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 5, bytes);
                String path = MediaStore.Images.Media.insertImage(AdminMainActivity.this.getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("image"+classDate.currentTimeAtMs()+uri.getLastPathSegment());

                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String DownloadUrl = uri.toString();
                                RecognizeFace(DownloadUrl);
                            }
                        });
                    }
                });

            }
            else {
                Toast.makeText(this, "Uploading Image...", Toast.LENGTH_SHORT).show();

                Uri uri = data.getData();
                Bitmap bitmap=null;

                try { bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(AdminMainActivity.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("image"+classDate.currentTimeAtMs()+uri.getLastPathSegment());

                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String DownloadUrl = uri.toString();



                            }
                        });
                    }
                });
            }
            }

    }

    private void RecognizeFace(String url) {

        final String api = "https://api.luxand.cloud/photo/search";
        RequestQueue mRequestQue = Volley.newRequestQueue(AdminMainActivity.this);

        final StringRequest stringReq = new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    String name = response.substring(
                            response.indexOf("name")+8,
                            response.indexOf("probability"))
                            .replaceAll(", ", "")
                            .replaceAll("\"", "");

                    new AlertDialog.Builder(AdminMainActivity.this)
                            .setTitle("Alert")
                            .setMessage(name)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show(); }

                catch (Exception ex){
                    new AlertDialog.Builder(AdminMainActivity.this)
                            .setTitle("Alert")
                            .setMessage(ex.toString())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new AlertDialog.Builder(AdminMainActivity.this)
                        .setTitle("Alert")
                        .setMessage(error.toString())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("token", "ab6820ebe05d47898a36edcec5e3b5b6");
                return pars; }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("photo", url);
                return pars;
            }
        };

        mRequestQue.add(stringReq);

    }

    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;

    }

}