package com.example.safespace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class Introduction extends AppCompatActivity {
    ImageView bg;
    TextView name;
    LottieAnimationView lottieAnimationView;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        timer=new Timer();

        bg=findViewById(R.id.back);
        name=findViewById(R.id.textView);
        lottieAnimationView=findViewById(R.id.lottieAnimationView);

        bg.animate().translationY(-2800).setDuration(1000).setStartDelay(4000);

        name.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(Introduction.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },5000);
    }
}