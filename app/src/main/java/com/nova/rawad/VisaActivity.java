package com.nova.rawad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nova.rawad.Users.PassportDetailActivity;
import com.nova.rawad.Users.RequestActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VisaActivity extends AppCompatActivity {

    ArrayList<RequestsClass> requests = null;
    String pictureImagePath1 = "";
    Bitmap bitmap1 = null;
    String DownloadUrl1= "";
    ImageView img_dia1 = null;
    int index = -1;

    ListView listView ;

    String dateFrom = "" ;

    Button button ;

    Uri uri111 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("رواد الرمثا");

        listView=findViewById(R.id.listView);
        button=findViewById(R.id.search2);
        dateFrom = classDate.date();
        button.setText(classDate.date());

        getRequests();

    }

    void getRequests(){
        Toast.makeText(this, "جاري جلب البيانات, يرجى الإنتظار...", Toast.LENGTH_LONG).show();

        FirebaseFirestore.getInstance().collection("Requests")
                .whereEqualTo("dateOfPermit",classDate.addDays(dateFrom,1))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                requests = new ArrayList<>();
                ArrayList<String> str = new ArrayList<>();

                for(int i=0; i<list.size(); i++) {
                    requests.add(list.get(i).toObject(RequestsClass.class));
                    str.add("اسم السائق: "+list.get(i).toObject(RequestsClass.class).userName+"\n"
                            +"تاريخ الرحلة: "+list.get(i).toObject(RequestsClass.class).dateOfPermit+
                            "\n"+"رقم الرحلة: "+list.get(i).toObject(RequestsClass.class).transId)
                    ;
                }

                Toast.makeText(VisaActivity.this, "تم جلب البيانات", Toast.LENGTH_SHORT).show();

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,str);
                listView.setAdapter(adapter);

            }
        });
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
                button.setText(dateFrom);
                getRequests();
            }
        };

        new DatePickerDialog(VisaActivity.this, date_, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void Search(View view) {

        if (requests == null)
            return;
        final AlertDialog.Builder builder = new AlertDialog.Builder((VisaActivity.this));
        LayoutInflater inflater = (VisaActivity.this).getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_visa, null));
        final AlertDialog dialog2 = builder.create();
        ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog2.show();
        dialog2.getWindow().setAttributes(lp);
        dialog2.setCanceledOnTouchOutside(false);

        dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                pictureImagePath1 = "";
                bitmap1 = null;
                DownloadUrl1 = "";
                img_dia1 = null;
                index = -1;
            }
        });

        AutoCompleteTextView act = dialog2.findViewById(R.id.sea);
        EditText name = dialog2.findViewById(R.id.txvDate1);
        EditText req = dialog2.findViewById(R.id.txvDate12);
        EditText conf = dialog2.findViewById(R.id.txvDate2);
        EditText trans = dialog2.findViewById(R.id.txvDate3);

        img_dia1 = dialog2.findViewById(R.id.imageView);

        Spinner pconfirm = dialog2.findViewById(R.id.txvDate6);
        Button btn_sea = dialog2.findViewById(R.id.btn_sea);
        Button btn_edit = dialog2.findViewById(R.id.btn_edit);

        ArrayList<String> temp = new ArrayList<>();
        for(int i=0; i<requests.size(); i++)
            temp.add(requests.get(i).transId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(VisaActivity.this, android.R.layout.simple_spinner_dropdown_item, temp);
        act.setAdapter(adapter);

        act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(VisaActivity.this, "جاري جلب البيانات, يرجى الإنتظار...", Toast.LENGTH_LONG).show();

                int index = -1;
                for(int i=0; i<requests.size(); i++)
                    if(requests.get(i).transId.equals(act.getText().toString()))
                        index = i;

                if(index != -1){

                    name.setText(requests.get(index).userName);
                    req.setText(requests.get(index).dateOfRequest);
                    conf.setText(requests.get(index).dateOfPermit);
                    trans.setText(requests.get(index).transId);

                    if(requests.get(index).payment.equals("") || requests.get(index).payment.equals("0"))
                        pconfirm.setSelection(1);
                    else
                        pconfirm.setSelection(0);

                    Picasso.get().load(Uri.parse(requests.get(index).img)).into(img_dia1);

                }
                else
                    Toast.makeText(VisaActivity.this, "لم يتم العثور على الراكب", Toast.LENGTH_SHORT).show();
            }
        });

        btn_sea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder((VisaActivity.this));
                LayoutInflater inflater = (VisaActivity.this).getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_driver_sea, null));
                final AlertDialog dialog3 = builder.create();
                ((FrameLayout) dialog3.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog3.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog3.show();
                dialog3.getWindow().setAttributes(lp);
                dialog3.setCanceledOnTouchOutside(false);

                AutoCompleteTextView act = dialog3.findViewById(R.id.sea);
                ListView li = dialog3.findViewById(R.id.li);

                FirebaseFirestore.getInstance().collection("Users")
                        .whereEqualTo("type", "1")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        ArrayList<UsersClass> drivers = new ArrayList<>();
                        ArrayList<String> names = new ArrayList<>();

                        for(int i=0; i<list.size(); i++){
                            drivers.add(list.get(i).toObject(UsersClass.class));
                            names.add(list.get(i).toObject(UsersClass.class).fullName); }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(VisaActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                        act.setAdapter(adapter);

                        act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                int index = -1;
                                for(int i=0; i<drivers.size(); i++)
                                    if(drivers.get(i).fullName.equals(act.getText().toString()))
                                        index = i;

                                if(index != -1){
                                    FirebaseFirestore.getInstance().collection("Requests")
                                            .whereEqualTo("userId", drivers.get(index).id)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                            ArrayList<RequestsClass> reqs = new ArrayList<>();
                                            ArrayList<String> dates = new ArrayList<>();

                                            Collections.reverse(list);

                                            for(int i=0; i<list.size(); i++){
                                                reqs.add(list.get(i).toObject(RequestsClass.class));
                                                dates.add(list.get(i).toObject(RequestsClass.class).dateOfRequest); }

                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(VisaActivity.this, android.R.layout.simple_spinner_dropdown_item, dates);
                                            li.setAdapter(adapter);

                                            li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    name.setText(reqs.get(position).userName);
                                                    req.setText(reqs.get(position).dateOfRequest);
                                                    conf.setText(reqs.get(position).dateOfPermit);
                                                    trans.setText(reqs.get(position).transId);

                                                    if(requests.get(position).payment.equals("") || requests.get(position).payment.equals("0"))
                                                        pconfirm.setSelection(1);
                                                    else
                                                        pconfirm.setSelection(0);

                                                    uri111=Uri.parse(requests.get(position).img);
                                                    Picasso.get().load(Uri.parse(requests.get(position).img)).into(img_dia1);
                                                    dialog3.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                                else
                                    Toast.makeText(VisaActivity.this, "لم يتم العثور على السائق", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

            }
        });

        img_dia1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(VisaActivity.this)
                        .setMessage("يرجى إختيار أسلوب الإدخال..")
                        .setPositiveButton("الأستوديو", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1996);
                            }
                        }).setNegativeButton("الكاميرا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        CaptureImage1();

                    }
                }).setNeutralButton("عرض الصورة", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowImageDialog a = new ShowImageDialog(index);
                        a.show();
                    }
                }).create().show();

            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("userName", name.getText().toString());
                map.put("dateOfRequest", req.getText().toString());
                map.put("dateOfPermit", conf.getText().toString());

                if(pconfirm.getSelectedItemPosition() == 0)
                    map.put("payment", "1");
                else
                    map.put("payment", "0");

                if(!DownloadUrl1.equals(""))
                    map.put("img", DownloadUrl1);

                if(trans.getText().toString().equals("")){
                    trans.setText(classDate.currentTimeAtMs());
                    map.put("transId", trans.getText().toString());
                    map.put("userId", requests.get(index).userId);
                    Toast.makeText(VisaActivity.this, "تم إضافة تأشيرة جديدة", Toast.LENGTH_SHORT).show(); }

                FirebaseFirestore.getInstance().collection("Requests")
                        .document(trans.getText().toString())
                        .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(VisaActivity.this, "تم تعديل البيانات", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VisaActivity.this, VisaActivity.class));
                            finish(); }
                        else
                            Toast.makeText(VisaActivity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    private void CaptureImage1() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        pictureImagePath1 = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath1 = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath1);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, 1997);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == 1996){
                Toast.makeText(this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(VisaActivity.this.getContentResolver(), bitmap1, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("image"+classDate.currentTimeAtMs()+uri.getLastPathSegment());

                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl1 = uri.toString();
                                uri111 = uri;
                                Toast.makeText(VisaActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();
                                img_dia1.setImageBitmap(bitmap1);
                            }
                        });
                    }
                });
            }
            else if(requestCode == 1997){
                Toast.makeText(this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath1);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(VisaActivity.this.getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("image"+classDate.currentTimeAtMs()+uri.getLastPathSegment());

                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl1 = uri.toString();
                                uri111 = uri;
                                Toast.makeText(VisaActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();
                                img_dia1.setImageBitmap(imageBitmap);
                            }
                        });
                    }
                });
            }
        }

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

    public void Search2(View view) {
        showDateFrom();
    }

    public class ShowImageDialog extends Dialog {
        Activity c ;
        int i ;
        public ShowImageDialog(int i ){
            super(VisaActivity.this);
            c=VisaActivity.this;
            this.i = i;
        }

        PhotoView imageView ;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_show_image);
            imageView = findViewById(R.id.imageViewMain);
            Picasso.get().load(Uri.parse(requests.get(i).img)).into(imageView);
        }
    }

}