package com.example.safespace;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class sharefile extends AppCompatActivity {
    private EditText email, subject, message;
    private Button button;
    FirebaseFirestore fStore;
    String ems,nm,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharefile);

        email = findViewById(R.id.email);
        Bundle bundle = getIntent().getExtras();


        ems = bundle.getString("email");
        nm=bundle.getString("name");
        url=bundle.getString("url");
        button = findViewById(R.id.btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senEmail();
            }
        });
    }
    private void senEmail() {
        String mEmail = email.getText().toString();
        String mMessage = "The file is shared in your account ";
        String mSubject="Safe Space";
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mEmail, mSubject, mMessage);
        fStore = FirebaseFirestore.getInstance();
        final String[] tt = new String[1];
        String uid="";
        DocumentReference doc= fStore.collection("users").document(mEmail);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
       //         uid=value.getString("uid").toString();
                DatabaseReference databaseReference;
                databaseReference = FirebaseDatabase.getInstance().getReference(value.getString("uid").toString()).child("Files");
                putPDF putPDF=new putPDF(nm,url);
                databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);

                }
        });

        javaMailAPI.execute();
        //Toast.makeText(this, "Send mail", Toast.LENGTH_SHORT).show();
        Intent i1=new Intent(this,DownloadPDF.class);
        i1.putExtra("emails",ems);
        startActivity(i1);
    }
}