package com.nova.smartdetectorsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileScreen extends AppCompatActivity {

    EditText inputEmail, inputPassword, inputMobile, inputUsername;
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
        setContentView(R.layout.activity_profile_screen);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputMobile = findViewById(R.id.mobile);
        inputUsername = findViewById(R.id.nameField);
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

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String username = inputUsername.getText().toString().trim();
                final String mobile = inputMobile.getText().toString().trim();

                if(email.equals("") || password.equals("") || username.equals("") || mobile.equals("")){
                    Toast.makeText(ProfileScreen.this, "Don't Leave Any Blanks Please.", Toast.LENGTH_SHORT).show();
                    pb.dismiss(); }

                else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("username", username);
                    map.put("mobile", mobile);
                    map.put("pic", accountInfoClass.pic);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Users")
                            .document(auth.getCurrentUser().getUid())
                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful())
                                Toast.makeText(ProfileScreen.this, "Error, Please Try Again Later.", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(ProfileScreen.this, "Welcome "+map.get("username")+" Your Account Has Been Updated.", Toast.LENGTH_LONG).show();
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
        db.collection("Users")
                .document(auth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                accountInfoClass = documentSnapshot.toObject(AccountInfoClass.class);

                inputUsername.setText(accountInfoClass.username);
                inputEmail.setText(accountInfoClass.email);
                inputMobile.setText(accountInfoClass.mobile);
                inputPassword.setText(accountInfoClass.password);

                Uri myUri = Uri.parse(accountInfoClass.pic);
                Picasso.with(ProfileScreen.this).load(myUri).placeholder(R.mipmap.ic_launcher_round).into(pic);

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

            String path = MediaStore.Images.Media.insertImage(ProfileScreen.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
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
                            Toast.makeText(ProfileScreen.this, "Profile Image Uploaded.", Toast.LENGTH_SHORT).show();

                            pic.setImageBitmap(bitmap);

                        }
                    });
                }
            });
        }

    }

}
