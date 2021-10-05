package com.nova.rawad.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nova.rawad.R;
import com.nova.rawad.RequestsClass;
import com.nova.rawad.TrpipClass;
import com.nova.rawad.VisaActivity;
import com.nova.rawad.classDate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyRequestActivity extends AppCompatActivity {

    public static ArrayList<RequestsClass> requests ;
    public static RequestsClass requestsClass ;
    ListView listView ;

    String pictureImagePath1 = "";
    Bitmap bitmap1 = null;
    String DownloadUrl1= "";
    ImageView img_dia1 = null;
    int index = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);
        init();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestsClass = requests.get(position);
                startActivity(new Intent(getApplicationContext(),Trip2Activity.class));
            }
        });
    }

    void init(){
        listView= findViewById(R.id.listView);
        getAll();
    }

    void getAll(){
        requests = new ArrayList<>();
        ArrayList<String>strtrpipClasses = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Requests").whereEqualTo("userId" ,
                getSharedPreferences("User",MODE_PRIVATE).getString("id",""))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                    requests.add(0,d.toObject(RequestsClass.class));
                    strtrpipClasses.add(
                            d.toObject(RequestsClass.class).userName+"\n"
                                    +d.toObject(RequestsClass.class).dateOfPermit
                    );
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,strtrpipClasses);
                listView.setAdapter(adapter);
            }
        });
    }

    public void Search() {

        if (requests == null)
            return;
        final AlertDialog.Builder builder = new AlertDialog.Builder((MyRequestActivity.this));
        LayoutInflater inflater = (MyRequestActivity.this).getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_visa2, null));
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

        act.setText(requests.get(index).transId);

        Spinner pconfirm = dialog2.findViewById(R.id.txvDate6);
        Button btn_sea = dialog2.findViewById(R.id.btn_sea);
        Button btn_edit = dialog2.findViewById(R.id.btn_edit);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestsClass = requests.get(index);
                startActivity(new Intent(getApplicationContext(),Trip2Activity.class));
            }
        });

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
            Toast.makeText(MyRequestActivity.this, "لم يتم العثور على الراكب", Toast.LENGTH_SHORT).show();

        img_dia1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MyRequestActivity.this)
                        .setMessage("يرجى إختيار أسلوب الإدخال..")
                      .setNeutralButton("عرض الصورة", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        VisaActivity.ShowImageDialog a = new VisaActivity.ShowImageDialog(Uri.parse(DownloadUrl1));
//                        a.show();
                    }
                }).create().show();

            }
        });

//        btn_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Map<String, Object> map = new HashMap<>();
//                map.put("userName", name.getText().toString());
//                map.put("dateOfRequest", req.getText().toString());
//                map.put("dateOfPermit", conf.getText().toString());
//
//                if(pconfirm.getSelectedItemPosition() == 0)
//                    map.put("payment", "1");
//                else
//                    map.put("payment", "0");
//
//                if(!DownloadUrl1.equals(""))
//                    map.put("img", DownloadUrl1);
//
//                if(trans.getText().toString().equals("")){
//                    trans.setText(classDate.currentTimeAtMs());
//                    map.put("transId", trans.getText().toString());
//                    map.put("userId", requests.get(index).userId);
//                    Toast.makeText(MyRequestActivity.this, "تم إضافة تأشيرة جديدة", Toast.LENGTH_SHORT).show(); }
//
//                FirebaseFirestore.getInstance().collection("Requests")
//                        .document(trans.getText().toString())
//                        .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            Toast.makeText(MyRequestActivity.this, "تم تعديل البيانات", Toast.LENGTH_SHORT).show();
//                            recreate();}
//                        else
//                            Toast.makeText(MyRequestActivity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        });

    }

}
