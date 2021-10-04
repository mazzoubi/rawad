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

    private class CreateProfileAsyncTask extends AsyncTask<Void, Void, Integer> {

        String name, url;
        int id;

        public CreateProfileAsyncTask(String name, String url) {
            this.name = name;
            this.url = url; }

        @Override
        protected void onPreExecute() {}

        @Override
        protected Integer doInBackground(Void... params) {

            id = CreateProfile(name);

            return id; }

        @Override
        protected void onPostExecute(Integer id) {

            final String api = "https://api.luxand.cloud/subject/"+id;
            RequestQueue mRequestQue = Volley.newRequestQueue(AdminMainActivity.this);

            final StringRequest stringReq = new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{
                        JSONObject obj = new JSONObject(response);
                        String str = obj.getString("message");

                        new AlertDialog.Builder(AdminMainActivity.this)
                                .setTitle("Alert")
                                .setMessage(str)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show(); }

                    catch (Exception ex){}

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
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
    }

    public class CustomGridViewAdapter extends BaseAdapter {

        public Integer[] mThumbIds = {
                R.drawable.ic_person_black_24dp,
                R.drawable.ic_baseline_assignment_24,
                R.drawable.ic_baseline_assignment_ind_24,
                R.drawable.logo, R.drawable.ic_baseline_coronavirus_24
        };

        public String[] mThumbNames2 = {
                "ID Citizen", "Issue Report", "View Reports", "Upload Pictures", "Vaccination"
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

        tvname = findViewById(R.id.name);
        tvssn = findViewById(R.id.ssn);
        tvmobile = findViewById(R.id.mobile);
        tvaddress = findViewById(R.id.address);
        tvage = findViewById(R.id.age);
        img = findViewById(R.id.img);

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
                            startActivityForResult(cameraIntent, 1996);}
                        break;
                    case 1:
                        final AlertDialog.Builder builder4 = new AlertDialog.Builder((AdminMainActivity.this));
                        LayoutInflater inflater4 = (AdminMainActivity.this).getLayoutInflater();
                        builder4.setView(inflater4.inflate(R.layout.dialog_issue_report, null));
                        final AlertDialog dialog4 = builder4.create();
                        ((FrameLayout) dialog4.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams lp4 = new WindowManager.LayoutParams();
                        lp4.copyFrom(dialog4.getWindow().getAttributes());
                        lp4.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp4.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        dialog4.show();
                        dialog4.getWindow().setAttributes(lp4);
                        dialog4.setCanceledOnTouchOutside(false);

                        final EditText edt2 = dialog4.findViewById(R.id.date);
                        edt2.setText(classDate.date());
                        final EditText edt3 = dialog4.findViewById(R.id.time);
                        edt3.setText(classDate.time());
                        final EditText edt4 = dialog4.findViewById(R.id.resp_emp);
                        edt4.setText(getSharedPreferences("UserInfo", MODE_PRIVATE).getString("email", "-"));
                        final EditText edt5 = dialog4.findViewById(R.id.report);
                        final Button btn2 = dialog4.findViewById(R.id.send);

                        btn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Map<String, String> map = new HashMap<>();
                                map.put("date", edt2.getText().toString());
                                map.put("time", edt3.getText().toString());
                                map.put("resp_emp", edt4.getText().toString());
                                map.put("report", edt5.getText().toString());
                                map.put("report_id", classDate.currentTimeAtMs());
                                map.put("uid", info.uid);

                                FirebaseFirestore.getInstance()
                                        .collection("Reports")
                                        .document(map.get("report_id"))
                                        .set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(AdminMainActivity.this, "Report Issued Successfully", Toast.LENGTH_SHORT).show();
                                                dialog4.dismiss();
                                            }
                                        });

                            }
                        });
                        break;
                    case 2:
                        FirebaseFirestore.getInstance()
                                .collection("Reports")
                                .whereEqualTo("uid", info.uid)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                ArrayList<ReportClass> reports = new ArrayList<>();
                                ArrayList<String> titles = new ArrayList<>();

                                for(int i=0; i<list.size(); i++){
                                    reports.add(list.get(i).toObject(ReportClass.class));
                                    titles.add(list.get(i).toObject(ReportClass.class).date+" | "+list.get(i).toObject(ReportClass.class).time); }

                                if(!reports.isEmpty()){
                                    ListView listView = new ListView(AdminMainActivity.this);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminMainActivity.this, android.R.layout.simple_list_item_1, titles);
                                    listView.setAdapter(adapter);

                                    new AlertDialog.Builder(AdminMainActivity.this)
                                            .setView(listView)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            final AlertDialog.Builder builder4 = new AlertDialog.Builder((AdminMainActivity.this));
                                            LayoutInflater inflater4 = (AdminMainActivity.this).getLayoutInflater();
                                            builder4.setView(inflater4.inflate(R.layout.dialog_issue_report2, null));
                                            final AlertDialog dialog4 = builder4.create();
                                            ((FrameLayout) dialog4.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
                                            WindowManager.LayoutParams lp4 = new WindowManager.LayoutParams();
                                            lp4.copyFrom(dialog4.getWindow().getAttributes());
                                            lp4.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                            lp4.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                            dialog4.show();
                                            dialog4.getWindow().setAttributes(lp4);
                                            dialog4.setCanceledOnTouchOutside(false);

                                            final EditText edt2 = dialog4.findViewById(R.id.date);
                                            edt2.setText(reports.get(position).date);
                                            final EditText edt3 = dialog4.findViewById(R.id.time);
                                            edt3.setText(reports.get(position).time);
                                            final EditText edt4 = dialog4.findViewById(R.id.resp_emp);
                                            edt4.setText(reports.get(position).resp_emp);
                                            final EditText edt5 = dialog4.findViewById(R.id.report);
                                            edt5.setText(reports.get(position).report);
                                            edt5.setEnabled(false);

                                            final Button close = dialog4.findViewById(R.id.send);
                                            close.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog4.dismiss();
                                                }
                                            });

                                        }
                                    });

                                }
                            }
                        });
                        break;
                    case 3:
                        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder2.build());

                        pictureImagePath = "";
                        String timeStamp2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName2 = timeStamp2 + ".jpg";
                        File storageDir2 = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES);
                        pictureImagePath = storageDir2.getAbsolutePath() + "/" + imageFileName2;
                        File file2 = new File(pictureImagePath);
                        Uri outputFileUri2 = Uri.fromFile(file2);
                        Intent cameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);

                        int camera2 = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.CAMERA);
                        int read2 = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        int write2 = ContextCompat.checkSelfPermission(AdminMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        ArrayList<String> listPermissionsNeeded2 = new ArrayList<>();
                        if (camera2 != PackageManager.PERMISSION_GRANTED){
                            listPermissionsNeeded2.add(Manifest.permission.CAMERA);
                            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded2.toArray
                                    (new String[listPermissionsNeeded2.size()]), 1); }
                        if (read2 != PackageManager.PERMISSION_GRANTED){
                            listPermissionsNeeded2.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded2.toArray
                                    (new String[listPermissionsNeeded2.size()]), 2); }
                        if (write2 != PackageManager.PERMISSION_GRANTED){
                            listPermissionsNeeded2.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            ActivityCompat.requestPermissions(AdminMainActivity.this,listPermissionsNeeded2.toArray
                                    (new String[listPermissionsNeeded2.size()]), 3); }
                        else{
                            startActivityForResult(cameraIntent2, 1997);}
                        break;
                    case 4:
                        final AlertDialog.Builder builder3 = new AlertDialog.Builder((AdminMainActivity.this));
                        LayoutInflater inflater3 = (AdminMainActivity.this).getLayoutInflater();
                        builder3.setView(inflater3.inflate(R.layout.dialo_vaccine, null));
                        final AlertDialog dialog3 = builder3.create();
                        ((FrameLayout) dialog3.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams lp3 = new WindowManager.LayoutParams();
                        lp3.copyFrom(dialog3.getWindow().getAttributes());
                        lp3.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp3.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        dialog3.show();
                        dialog3.getWindow().setAttributes(lp3);
                        dialog3.setCanceledOnTouchOutside(false);

                        final EditText edt = dialog3.findViewById(R.id.vaccine);
                        final Button btn = dialog3.findViewById(R.id.send);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                FirebaseFirestore.getInstance()
                                        .collection("Accounts")
                                        .document(info.uid)
                                        .update("vaccine", edt.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        tvaddress.setText("Vaccine: "+edt.getText().toString());
                                        Toast.makeText(AdminMainActivity.this, "Vaccine Type Changed Successfully", Toast.LENGTH_SHORT).show();
                                        dialog3.dismiss();
                                    }
                                });

                            }
                        });
                        break;
                }

            }
        });

    }

    private void Space(Bundle savedInstanceState){

        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Profile", R.drawable.ic_baseline_emoji_people_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("Exit", R.drawable.ic_baseline_logout_24));
        spaceNavigationView.setSpaceItemIconSize(100);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {}

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemName){
                    case "Profile":
                        startActivity(new Intent(AdminMainActivity.this, ProfileScreen.class));
                        break;
                    case "Exit":
                        startActivity(new Intent(AdminMainActivity.this, LoginScreenActivity.class));
                        finish();
                        break;
                }

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemName){
                    case "Profile":
                        startActivity(new Intent(AdminMainActivity.this, ProfileScreen.class));
                        break;
                    case "Exit":
                        startActivity(new Intent(AdminMainActivity.this, LoginScreenActivity.class));
                        finish();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == 1996){

                Toast.makeText(this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
            else if(requestCode == 1997){
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
                                new CreateProfileAsyncTask(tvname.getText().toString().replaceAll("Name: ", ""), DownloadUrl).execute();

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

                    FirebaseFirestore.getInstance()
                            .collection("Accounts")
                            .whereEqualTo("name", name)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            try{
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                info = list.get(0).toObject(AccountInfoClass.class);

                                tvname.setText("Name: "+info.name);
                                tvssn.setText("SSN: "+info.ssn);
                                tvmobile.setText("Mobile: "+info.mobile);
                                tvaddress.setText("Vaccine: "+info.vaccine);
                                tvage.setText("Age: "+info.age);

                                Uri myUri = Uri.parse(info.pic);
                                Picasso.get().load(myUri).placeholder(R.mipmap.ic_launcher_round).into(img);

                            }
                            catch (Exception ex){
                                Toast.makeText(AdminMainActivity.this, "could not find "+name+" in the data base", Toast.LENGTH_LONG).show();
                            }
                        }
                    }); }

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

    public int CreateProfile(String name){

        String jsonInputString = "";
        try {
            URL url = new URL("https://api.luxand.cloud/subject");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("token", "ab6820ebe05d47898a36edcec5e3b5b6");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);

            jsonInputString = "";
            jsonInputString+="{\n";
            jsonInputString+="\"name\":\""+name+"\"\n";
            jsonInputString+="}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length); }

            int status = con.getResponseCode();

            if (status > 400) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null)
                        response.append(responseLine.trim());
                    return 0; }
            }
            else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null)
                        response.append(responseLine.trim());

                    JSONObject obj = new JSONObject(response.toString());
                    String id = obj.getString("id");

                    return Integer.parseInt(id); }
            }
        }
        catch (Exception e) { return 0; }

    }

}