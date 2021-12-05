package com.example.safespace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login,register;
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        login.setOnClickListener(view -> opening());
        register.setOnClickListener(view -> openings());
    }
    public void opening(){
        Intent i=new Intent(this,Login.class);
        startActivity(i);
    }
    public void openings(){
        Intent intent=new Intent(this,Register.class);
        startActivity(intent);
    }
}