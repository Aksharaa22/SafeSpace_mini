package com.example.safespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Files extends AppCompatActivity {
    EditText editText,filename;
    Button btn;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String x;
    FirebaseUser mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        editText=findViewById(R.id.editText);
        filename=findViewById(R.id.filename);
        btn=findViewById(R.id.btn);
        Bundle p = getIntent().getExtras();
        x = p.getString("email");
        String uid = user.getUid();
        String emails=user.getEmail();
        Toast.makeText(this,emails,Toast.LENGTH_SHORT).show();
        //String userID = mAuth.DatabaseReference.getuID.("rashmiaug16@gmail.com");

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(uid).child("Files");
        //btn.setEnabled(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPDF();
            }
        });
    }
    private void selectPDF() {
        Intent intent=new Intent();
        intent.setType("application/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            btn.setEnabled(true);
            //editText.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            editText.setText("File is selected");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadPDFFileFirebase(data.getData());
                }
            });
        }
    }
    private void uploadPDFFileFirebase(Uri data) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();
        //final ProgressDialog progressDialog=new ProgressDialog(this);
        //progressDialog.setTitle("File is loading...");
        //String n="upload"+System.currentTimeMillis()+".pdf";
        String n=filename.getText().toString();
        StorageReference reference=storageReference.child(n);

        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri=uriTask.getResult();
                        putPDF putPDF=new putPDF(n,uri.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);
                        Toast.makeText(Files.this,"Success:File Upload",Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        Intent intent=new Intent(Files.this, DownloadPDF.class);
                        startActivity(intent);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //double progress=(100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                //progressDialog.setMessage("File Uploaded.. "+(int)progress+"%");
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(Files.this,"Failed:File not Uploaded",Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}