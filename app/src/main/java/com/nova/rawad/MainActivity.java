package com.nova.rawad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    EditText inputSSN, inputName, inputMobile, inputEmail,
            inputAddress, inputAge, inputPassword, inpuVaccine;
    Button btnSignUp;
    CircleImageView pic;
    ProgressDialog pb;

    Bitmap bitmap;
    String DownloadUrl = "";
    AccountInfoClass accountInfoClass;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        inputSSN = findViewById(R.id.ssn);
        inputName = findViewById(R.id.name);
        inputMobile = findViewById(R.id.mobile);
        inputEmail = findViewById(R.id.email);
        inputAddress = findViewById(R.id.address);
        inputAge = findViewById(R.id.age);
        inpuVaccine = findViewById(R.id.vaccine);
        inputPassword = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.sign_up_button);
        pic = findViewById(R.id.img);
        auth = FirebaseAuth.getInstance();

        GetUserData();

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1996);

            }
        });

        pb = new ProgressDialog(this);
        pb.setMessage("Loading, Please Wait...");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.show();

                final String SSN = inputSSN.getText().toString().trim();
                final String Name = inputName.getText().toString().trim();
                final String Mobile = inputMobile.getText().toString().trim();
                final String Email = inputEmail.getText().toString().trim();
                final String Address = inputAddress.getText().toString().trim();
                final String Age = inputAge.getText().toString().trim();
                final String Password = inputPassword.getText().toString().trim();

                if(SSN.equals("") || Name.equals("") || Mobile.equals("") || Email.equals("")||
                        Address.equals("") || Age.equals("") || Password.equals("")){
                    Toast.makeText(MainActivity.this, "Don't Leave Any Blanks Please.", Toast.LENGTH_SHORT).show();
                    pb.dismiss(); }

                else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ssn", SSN);
                    map.put("name", Name);
                    map.put("mobile", Mobile);
                    map.put("address", Address);
                    map.put("age", Age);
                    map.put("pic", accountInfoClass.pic);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Accounts")
                            .document(auth.getCurrentUser().getUid())
                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful())
                                Toast.makeText(MainActivity.this, "Error, Please Try Again Later.", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(MainActivity.this, "Welcome "+map.get("username")+" Your Account Has Been Updated.", Toast.LENGTH_LONG).show();
                                recreate(); }
                        }
                    });
                }
            }
        });

    }

    private void GetUserData() {


        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts")
                .document(auth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                accountInfoClass = documentSnapshot.toObject(AccountInfoClass.class);

                inputSSN.setText(accountInfoClass.ssn);
                inputName.setText(accountInfoClass.name);
                inputMobile.setText(accountInfoClass.mobile);
                inputEmail.setText(accountInfoClass.email);
                inputAddress.setText(accountInfoClass.address);
                inputAge.setText(accountInfoClass.age);
                inpuVaccine.setText(accountInfoClass.vaccine);
                inputPassword.setText(accountInfoClass.password);

                Uri myUri = Uri.parse(accountInfoClass.pic);
//                Picasso.with(MainActivity.this).load(myUri).placeholder(R.mipmap.ic_launcher_round).into(pic);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Toast.makeText(this, "Uploading Image...", Toast.LENGTH_SHORT).show();

            Uri uri = data.getData();

            try { bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); }
            catch (IOException e) {}
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

            String path = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
            uri = Uri.parse(path);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ImageName =  storageReference.child("image"+classDate.currentTimeAtMs()+uri.getLastPathSegment());

            ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            DownloadUrl = uri.toString();
                            accountInfoClass.pic = DownloadUrl;
                            Toast.makeText(MainActivity.this, "Profile Image Uploaded.", Toast.LENGTH_SHORT).show();

                            pic.setImageBitmap(bitmap);

                        }
                    });
                }
            });
        }

    }

    public void reports(View view) {

        FirebaseFirestore.getInstance()
                .collection("Reports")
                .whereEqualTo("uid", auth.getCurrentUser().getUid())
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
                    ListView listView = new ListView(MainActivity.this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
                    listView.setAdapter(adapter);

                    new AlertDialog.Builder(MainActivity.this)
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

                            final AlertDialog.Builder builder4 = new AlertDialog.Builder((MainActivity.this));
                            LayoutInflater inflater4 = (MainActivity.this).getLayoutInflater();
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

    }

    public void logout(View view) {

        startActivity(new Intent(MainActivity.this, LoginScreenActivity.class));
        finish();

    }
}