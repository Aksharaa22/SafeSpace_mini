package com.example.safespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class Register extends AppCompatActivity {
    EditText name,email,pass,cpass;
    Button bt;
    TextView r;
    private FirebaseAuth mauth;
    private ProgressDialog mloadingbar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=(EditText) findViewById(R.id.name);
        email=(EditText) findViewById(R.id.emailid);
        pass=(EditText) findViewById(R.id.pwd);
        cpass=(EditText) findViewById(R.id.cpwd);
        bt=(Button) findViewById(R.id.register);
        db=FirebaseFirestore.getInstance();
        mauth=FirebaseAuth.getInstance();
        mloadingbar=new ProgressDialog(Register.this);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });
    }

    private void checkCredentials() {
        String uname=name.getText().toString();
        String em=email.getText().toString();
        String p= pass.getText().toString();
        String cp=cpass.getText().toString();
        String uid= mauth.getUid();
        if(uname.isEmpty()||uname.length()<7)
        {
            showError(name,"not valid");
        }else if(em.isEmpty()||!em.contains("@"))
        {
            showError(email,"not valid");
        }
        else if(p.isEmpty()||p.length()<7)
        {
            showError(pass,"not valid");
        }
        else if(cp.isEmpty()||!cp.equals(p))
        {
            showError(cpass,"not equal");
        }
        else
        {
            mloadingbar.setTitle("Registration");
            mloadingbar.setMessage("Please wait while check your credentials");
            mloadingbar.setCanceledOnTouchOutside(false);
            mloadingbar.show();

            mauth.createUserWithEmailAndPassword(em,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("uname",uname);
                        map.put("email",em);
                        map.put("uid",mauth.getUid());
                        //db.collection(em).document(uname).set(map)
                        db.collection("users").document(em).set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(getApplicationContext(),"Successfull",Toast.LENGTH_SHORT).show();
                        mloadingbar.dismiss();
                        Intent i= new Intent(Register.this,Login.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}