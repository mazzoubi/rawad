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
import android.app.ProgressDialog;
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

public class PassportDetailActivity extends AppCompatActivity {
    String dateFrom="";
    TextView txvExpiryDate ,txvImageState , txvImageState2;
    Uri uri = null;
    Uri uri2 = null;

    EditText edtPassNumber , edtPassPlace ;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_detail);
        init();
    }

    void init(){
        progressDialog = new ProgressDialog(PassportDetailActivity.this);
        progressDialog.setTitle("الرجاء الإنتظار...");
        txvExpiryDate = findViewById(R.id.editTextTextPersonName2);
        txvImageState = findViewById(R.id.textView15);
        txvImageState2 = findViewById(R.id.textView16);

        edtPassNumber = findViewById(R.id.editTextTextPersonName);
        edtPassPlace = findViewById(R.id.editTextTextPersonName3);

        progressDialog.show();
        FirebaseFirestore.getInstance().collection("DriverPassports")
                .document(getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                progressDialog.dismiss();
                try {
                    edtPassNumber.setText(documentSnapshot.getString("p_num"));
                    edtPassPlace.setText(documentSnapshot.getString("passportPlace"));
                    txvExpiryDate.setText(documentSnapshot.getString("p_expiry"));
                }catch (Exception e){}
            }
        });

    }

    void selectImages(){
        if (ActivityCompat.checkSelfPermission(PassportDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PassportDetailActivity.this,
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
        if (ActivityCompat.checkSelfPermission(PassportDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PassportDetailActivity.this,
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
        new DatePickerDialog(PassportDetailActivity.this, date_, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onClickExpiryDate(View view) {
        showDateFrom();
    }

    public void onClickChoseImage(View view) {
        if (uri==null){
            selectImages();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PassportDetailActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PassportDetailActivity.this);
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
        if (edtPassNumber.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال رقم الجواز", Toast.LENGTH_SHORT).show();
        }else if (edtPassPlace.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال مكان اصدار الجواز", Toast.LENGTH_SHORT).show();
        }else if (dateFrom.isEmpty()){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال تاريخ انتهاء الجواز", Toast.LENGTH_SHORT).show();
        }else if (uri==null){
            Toast.makeText(getApplicationContext(), "الرجاء ادخال صورة الجواز", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            Map<String,Object> map = new HashMap<>();
            map.put("d_id", getSharedPreferences("User",MODE_PRIVATE).getString("id",""));
            map.put("p_name", getSharedPreferences("User",MODE_PRIVATE).getString("fullName",""));
            map.put("userPhone",getSharedPreferences("User",MODE_PRIVATE).getString("phone",""));
            map.put("p_num",edtPassNumber.getText().toString());
            map.put("passportPlace", edtPassPlace.getText().toString());
            map.put("p_expiry",dateFrom );
            map.put("p_issue","jordan");
            map.put("infoState","1");
            map.put("office_state","0");
            FirebaseFirestore.getInstance().collection("DriverPassports")
                    .document(getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "خطأ في عملية الحفظ الرجاء المحاولة مرة اخرى", Toast.LENGTH_SHORT).show();
                    }
                }
            });
           FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

           StorageReference aa = firebaseStorage.getReference().child("DriverPassports"+"/"+
                    getSharedPreferences("User",MODE_PRIVATE).getString("id","")
                    +"").child("passport");
                    aa.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    aa.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri u) {

                            Map<String,Object> map1 = new HashMap<>();
                            map1.put("p_img",u.toString());
                            FirebaseFirestore.getInstance().collection("DriverPassports")
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

                StorageReference aa2 = firebaseStorage2.getReference().child("DriverPassports"+"/"+
                        getSharedPreferences("User",MODE_PRIVATE).getString("id","")
                        +"").child("moreDetail");
                aa.putFile(uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        aa2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri u) {

                                Map<String,Object> map1 = new HashMap<>();
                                map1.put("p_img2",u.toString());
                                FirebaseFirestore.getInstance().collection("DriverPassports")
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

    public class ShowImageDialog extends Dialog {
        Activity c ;
        Uri urrri ;
        public ShowImageDialog(Uri urrri){
            super(PassportDetailActivity.this);
            c=PassportDetailActivity.this;
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