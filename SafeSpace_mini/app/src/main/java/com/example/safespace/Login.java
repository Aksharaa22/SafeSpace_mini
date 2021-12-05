package com.example.safespace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText email,passw;
TextView fog;
    Button bt;
    FirebaseAuth mauth;
    ProgressDialog mloadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.emailid);
        passw= (EditText) findViewById(R.id.pwd);

        bt=(Button) findViewById(R.id.login);
        mauth= FirebaseAuth.getInstance();
        mloadingbar= new ProgressDialog(Login.this);
        fog=(TextView)findViewById(R.id.textView4);
        fog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                if (em.isEmpty() || !em.contains("@")) {
                    showError(email, "not valid");
                }
                else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(em)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Mail sent", Toast.LENGTH_SHORT).show();
                                        // Log.d(TAG, "Email sent.");
                                    }
                                }
                            });
                }
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });
    }

    private void checkCredentials() {

        String em = email.getText().toString();
        String p = passw.getText().toString();

        if (em.isEmpty() || !em.contains("@")) {
            showError(email, "not valid");
        }
        else if (p.isEmpty() || p.length() < 7) {
            showError(passw, "not valid");
        }
        else {
            mloadingbar.setTitle("Login");
            mloadingbar.setMessage("Please wait while check your credentials");
            mloadingbar.setCanceledOnTouchOutside(false);
            mloadingbar.show();
            mauth.signInWithEmailAndPassword(em,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        mloadingbar.dismiss();
                        Intent i= new Intent(Login.this,MainNotes.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("email",em);
                        startActivity(i);

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