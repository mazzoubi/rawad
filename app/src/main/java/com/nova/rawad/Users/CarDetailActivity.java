package com.nova.rawad.Users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nova.rawad.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CarDetailActivity extends AppCompatActivity {

    String dateFrom="";
    TextView txvExpiryDate ,txvImageState , txvImageState2;
    Uri uri = null;
    Uri uri2 = null;

    EditText edtCarNumber , edtCarType , edtNationality , edtPassengerNumber ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        init();
    }

    void init(){
        txvExpiryDate = findViewById(R.id.editTextTextPersonName2);
        txvImageState = findViewById(R.id.textView15);
        txvImageState2 = findViewById(R.id.textView16);

        edtCarNumber = findViewById(R.id.editTextTextPersonName);
        edtCarType = findViewById(R.id.editTextTextPersonName3);
        edtNationality = findViewById(R.id.editTextTextPersonName4);
        edtPassengerNumber = findViewById(R.id.editTextTextPersonName5);

        FirebaseFirestore.getInstance().collection("DriverLicence")
                .document(getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    edtCarType.setText(documentSnapshot.getString("carType"));
                    edtCarNumber.setText(documentSnapshot.getString("carNumber"));
                    edtPassengerNumber.setText(documentSnapshot.getString("passengerNumber"));
                    edtNationality.setText(documentSnapshot.getString("d_nationality"));
                    txvExpiryDate.setText(documentSnapshot.getString("d_expiry"));

                    uri = Uri.parse(documentSnapshot.getString("d_img"));
                    uri2 = Uri.parse(documentSnapshot.getString("d_img2"));


                }catch (Exception e){}
            }
        });




    }

    void selectImages(){
        if (ActivityCompat.checkSelfPermission(CarDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CarDetailActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setType("image/*");
        startActivityForResult(intent,1);
//        startActivityIfNeeded(intent,1);
    }

    void selectImages2(){
        if (ActivityCompat.checkSelfPermission(CarDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CarDetailActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setType("image/*");
        startActivityForResult(intent,2);
//        startActivityIfNeeded(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode == RESULT_OK ){
            ClipData clipData = data.getClipData();
            if (clipData!=null){

            }else {
                uri=data.getData();
                txvImageState.setText("تم التحميل");
                txvImageState.setBackgroundColor(Color.GREEN);
            }

        }else if (requestCode==2&&resultCode == RESULT_OK ){
            ClipData clipData = data.getClipData();
            if (clipData!=null){

            }else {
                uri2=data.getData();
                txvImageState2.setText("تم التحميل");
                txvImageState2.setBackgroundColor(Color.GREEN);
            }

        }
    }
    void showDateFrom(){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date_ = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "d-M-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dateFrom=sdf.format(myCalendar.getTime());
                txvExpiryDate.setText(dateFrom);

            } };
        new DatePickerDialog(CarDetailActivity.this, date_, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onClickExpiryDate(View view) {
        showDateFrom();
    }

    public void onClickChoseImage(View view) {
        if (uri==null){
            selectImages();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CarDetailActivity.this);
            builder.setTitle("النظام...");
            builder.setMessage("الرجاء اختيار اجراء ");
            builder.setPositiveButton("اختيار صورة", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectImages();
                }
            });

            builder.setNegativeButton("عرض الصورة", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ShowImageDialog a = new ShowImageDialog(uri);
                    a.show();
                }
            });

            builder.show();
        }
    }

    public void onClickChoseImage2(View view) {
        if (uri2==null){
            selectImages2();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CarDetailActivity.this);
            builder.setTitle("النظام...");
            builder.setMessage("الرجاء اختيار اجراء ");
            builder.setPositiveButton("اختيار صورة", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectImages2();
                }
            });

            builder.setNegativeButton("عرض الصورة", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ShowImageDialog a = new ShowImageDialog(uri2);
                    a.show();
                }
            });

            builder.show();
        }
    }

    public void onClickSave(View view) {
        if (edtCarNumber.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال رقم المركبة", Toast.LENGTH_SHORT).show();
        }else if (edtCarType.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال نوع المركبة", Toast.LENGTH_SHORT).show();
        }else if (dateFrom.isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال تاريخ انتهاء الرخصة", Toast.LENGTH_SHORT).show();
        }else if (edtNationality.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال دولة انشاء الخصة", Toast.LENGTH_SHORT).show();
        }else if (edtPassengerNumber.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال عدد الركاب المسموح", Toast.LENGTH_SHORT).show();
        }else if (uri==null){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال صورة رخصة المركبة", Toast.LENGTH_SHORT).show();
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("carType", edtCarType.getText().toString());
            map.put("d_expiry", dateFrom);
            map.put("d_id",getSharedPreferences("User",MODE_PRIVATE).getString("id",""));
            map.put("d_img","");
            map.put("d_img2","");
            map.put("d_name",getSharedPreferences("User",MODE_PRIVATE).getString("fullName","") );
            map.put("d_nationality",edtNationality.getText().toString());
            map.put("d_type","1");
            map.put("passengerNumber",edtPassengerNumber.getText().toString());
            map.put("carNumber",edtCarNumber.getText().toString());
            map.put("office_state","0");
            FirebaseFirestore.getInstance().collection("DriverLicence")
                    .document(getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "خطأ في عملية الحفظ الرجاء المحاولة مرة اخرى", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

            StorageReference aa = firebaseStorage.getReference().child("DriverLicence"+"/"+
                    getSharedPreferences("User",MODE_PRIVATE).getString("id","")
                    +"").child("carLicense");
            aa.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    aa.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri u) {

                            Map<String,Object> map1 = new HashMap<>();
                            map1.put("d_img",u.toString());
                            FirebaseFirestore.getInstance().collection("DriverLicence")
                                    .document(getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                                    .update(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    });
                }
            });

            if (uri2!=null){
                FirebaseStorage firebaseStorage2= FirebaseStorage.getInstance();

                StorageReference aa2 = firebaseStorage2.getReference().child("DriverLicence"+"/"+
                        getSharedPreferences("User",MODE_PRIVATE).getString("id","")
                        +"").child("moreDetail");
                aa.putFile(uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        aa2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri u) {

                                Map<String,Object> map1 = new HashMap<>();
                                map1.put("d_img2",u.toString());
                                FirebaseFirestore.getInstance().collection("DriverLicence")
                                        .document(getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                                        .update(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });

                            }
                        });
                    }
                });
            }

        }
    }

    public class ShowImageDialog extends Dialog{
        Activity c ;
        Uri urrri ;
        public ShowImageDialog(Uri urrri){
            super(CarDetailActivity.this);
            c=CarDetailActivity.this;
            this.urrri = urrri;
        }

        PhotoView imageView ;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_show_image);
            imageView = findViewById(R.id.imageViewMain);
            Picasso.get().load(urrri).into(imageView);
        }
    }
}