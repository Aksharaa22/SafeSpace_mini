package com.example.safespace.note;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.safespace.MainActivity;
import com.example.safespace.MainNotes;
import com.example.safespace.RemainderBroadcast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.safespace.R;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore fStore;
    EditText noteTitle,noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;
    String cipherText="";
int t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);
        progressBarSave = findViewById(R.id.progressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fab1 = findViewById(R.id.addRemainder);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //Toast.makeText(AddNote.this,"pop",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(AddNote.this);

                alert.setTitle("Set Time");
                alert.setMessage("Enter time in minutes");

// Set an EditText view to get user input
                final EditText input = new EditText(AddNote.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        t=Integer.parseInt(String.valueOf(value));
                        //Toast.makeText(AddNote.this,value,Toast.LENGTH_SHORT).show();
                        Toast.makeText(AddNote.this,"Reminder set!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AddNote.this, RemainderBroadcast.class);
                        PendingIntent pendingIntent=PendingIntent.getBroadcast(AddNote.this,0,intent,0);
                        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                        long timebtn=System.currentTimeMillis();

                        long tensec=1000*t*60;

                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                timebtn+tensec,pendingIntent);

                        // Do something with value!
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();



            }});

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(AddNote.this, "Can not Save note with Empty Field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarSave.setVisibility(View.VISIBLE);

                // save note

                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document();
                Map<String,Object> note = new HashMap<>();
String plainText=nContent;
int depth=3;
                int r=depth,len=plainText.length();
                System.out.print(len);
                int c=len/depth;
                char mat[][]=new char[r][c];
                int k=0;



                for(int i=0;i< c;i++)
                {
                    for(int j=0;j<r;j++)
                    {
                        if(k!=len)
                            mat[j][i]=plainText.charAt(k++);
                        else
                            mat[j][i]='X';
                    }
                }
                for(int i=0;i< r;i++)
                {
                    for(int j=0;j< c;j++)
                    {
                        cipherText+=mat[i][j];
                    }
                }
                note.put("title",nTitle);
                note.put("content",cipherText);

                //note.put("date",java.time.LocalDate.now());
                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddNote.this, "Note Added.", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(AddNote.this, cipherText, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(view.getContext(), MainNotes.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

                        onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNote.this, "Error, Try again.", Toast.LENGTH_SHORT).show();
                        progressBarSave.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.close){
            Toast.makeText(this,"Not Saved.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddNote.this, MainNotes.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

        }
        return super.onOptionsItemSelected(item);
    }
}
