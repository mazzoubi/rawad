package com.nova.smartdetectorsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterScreen extends AppCompatActivity {

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
            RequestQueue mRequestQue = Volley.newRequestQueue(RegisterScreen.this);

            final StringRequest stringReq = new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{
                        JSONObject obj = new JSONObject(response);
                        String str = obj.getString("message");

                        new AlertDialog.Builder(RegisterScreen.this)
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

    EditText inputSSN, inputName, inputMobile, inputEmail, inputAddress, inputAge, inputPassword;
    Button btnSignUp;
    CircleImageView pic;
    ProgressDialog pb;

    Bitmap bitmap;
    String DownloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_screen);

        inputSSN = findViewById(R.id.ssn);
        inputName = findViewById(R.id.name);
        inputMobile = findViewById(R.id.mobile);
        inputEmail = findViewById(R.id.email);
        inputAddress = findViewById(R.id.address);
        inputAge = findViewById(R.id.age);
        inputPassword = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.sign_up_button);
        pic = findViewById(R.id.img);

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
                    Toast.makeText(RegisterScreen.this, "Don't Leave Any Blanks Please.", Toast.LENGTH_SHORT).show();
                    pb.dismiss(); }

                else {
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegisterScreen.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful())
                                Toast.makeText(RegisterScreen.this, "Error, Please Try Again Later.", Toast.LENGTH_SHORT).show();

                            else {

                                Map<String, String> map = new HashMap<>();
                                map.put("ssn", SSN);
                                map.put("name", Name);
                                map.put("mobile", Mobile);
                                map.put("email", Email);
                                map.put("address", Address);
                                map.put("age", Age);
                                map.put("vaccine", "none");
                                map.put("password", Password);
                                map.put("uid", auth.getUid());
                                map.put("pic", DownloadUrl);
                                map.put("create", classDate.date());

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Accounts")
                                        .document(map.get("uid"))
                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (!task.isSuccessful())
                                            Toast.makeText(RegisterScreen.this, "Error, Please Try Again Later.", Toast.LENGTH_SHORT).show();
                                        else{
                                            new CreateProfileAsyncTask(Name, DownloadUrl).execute();
                                            Toast.makeText(RegisterScreen.this, "Welcome "+map.get("name")+" Your Account Has Been Created.", Toast.LENGTH_LONG).show();
                                            RegisterScreen.this.finish(); }
                                    }
                                });
                            } }
                    });
                }
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

            String path = MediaStore.Images.Media.insertImage(RegisterScreen.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
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
                            Toast.makeText(RegisterScreen.this, "Profile Image Uploaded.", Toast.LENGTH_SHORT).show();
                            pic.setImageBitmap(bitmap);

                        }
                    });
                }
            });
        }

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
