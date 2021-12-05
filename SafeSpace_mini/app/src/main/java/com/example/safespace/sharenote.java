package com.example.safespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safespace.note.AddNote;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class sharenote extends AppCompatActivity {
    private EditText email, subject, message;
    private Button button;
    FirebaseFirestore fStore;
    String ems,nm,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharenote);
        email = findViewById(R.id.email);
        Bundle bundle = getIntent().getExtras();


        ems = bundle.getString("email");
        nm=bundle.getString("title");
        url=bundle.getString("content");
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
        String mMessage = "The Notes is shared in your account ";
        String mSubject="Safe Space";
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mEmail, mSubject, mMessage);
        fStore = FirebaseFirestore.getInstance();
        final String[] tt = new String[1];
        String uid="";
        DocumentReference doc= fStore.collection("users").document(mEmail);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                DocumentReference docref = fStore.collection("notes").document(value.getString("uid").toString()).collection("myNotes").document();
                Map<String,Object> note = new HashMap<>();
                note.put("title",nm);
                note.put("content",url);

                //note.put("date",java.time.LocalDate.now());
                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(.this, "Note Added.", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(AddNote.this, cipherText, Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(view.getContext(), MainNotes.class));
                        //overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

                        //onBackPressed();
                        Toast.makeText(sharenote.this,"Note Share successfully",Toast.LENGTH_SHORT).show();
                        Intent tt=new Intent(sharenote.this,MainNotes.class);
                        startActivity(tt);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(sharenote.this, "Error, Try again.", Toast.LENGTH_SHORT).show();
                        //progressBarSave.setVisibility(View.VISIBLE);
                    }
                });


            }
        });

        javaMailAPI.execute();
        //Toast.makeText(this, "Send mail", Toast.LENGTH_SHORT).show();
        Intent i1=new Intent(this,DownloadPDF.class);
        i1.putExtra("emails",ems);
        startActivity(i1);
    }
}