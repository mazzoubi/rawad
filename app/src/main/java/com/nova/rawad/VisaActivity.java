package com.nova.rawad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisaActivity extends AppCompatActivity {

    ArrayList<RequestsClass> requests = null;
    String pictureImagePath1 = "";
    Bitmap bitmap1 = null;
    String DownloadUrl1= "";
    ImageView img_dia1 = null;
    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("رواد الرمثا");

        Toast.makeText(this, "جاري جلب البيانات, يرجى الإنتظار...", Toast.LENGTH_LONG).show();

        FirebaseFirestore.getInstance().collection("Requests")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                requests = new ArrayList<>();

                for(int i=0; i<list.size(); i++)
                    requests.add(list.get(i).toObject(RequestsClass.class));

                Toast.makeText(VisaActivity.this, "تم جلب البيانات", Toast.LENGTH_SHORT).show();



            }
        });

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
                Toast.makeText(VisaActivity.this, "جاري جلب البيانات, يرجى الإنتظار...", Toast.LENGTH_LONG).show();

                index = -1;
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
                            recreate();}
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

        //here abu rashed

    }
}