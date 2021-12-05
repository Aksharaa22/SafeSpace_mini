package com.example.safespace;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Images extends AppCompatActivity {
    EditText imagename;
    ImageView b;
    Button btn,ubtn;
    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        imagename=findViewById(R.id.imagename);
        b=findViewById(R.id.firebaseimage);
        btn=findViewById(R.id.selectImagebtn);
        ubtn=findViewById(R.id.uploadImagebtn);
        //Bundle p = getIntent().getExtras();
        //String x = p.getString("email");
        String uid = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(uid).child("Images");
        //btn.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,  100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri  = data.getData();
            b.setImageURI(imageUri);
            ubtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadImage(data.getData());
                }
            });
        }
    }
    private void uploadImage(Uri data) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Image Uploading...");
        progressDialog.show();
        String fileName=imagename.getText().toString();
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        //Date now = new Date();
        //String fileName = formatter.format(now);
        //storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
        StorageReference reference=storageReference.child(fileName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri=uriTask.getResult();
                        putImage putimage=new putImage(fileName,uri.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(putimage);
                        b.setImageURI(null);
                        Toast.makeText(Images.this,"Success:Image is uploaded", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        Intent intent=new Intent(Images.this,ImagesActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(Images.this,"Failed:Image is not Uploaded",Toast.LENGTH_SHORT).show();
            }
        });
    }
}