package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setTitle("Calorie");
        String name = "User";
        TextView userName = findViewById(R.id.userName);
        try {
            Bundle bundle = getIntent().getExtras();

            name = bundle.getParcelable("UserName").toString();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            userName.setText("Welcome " + name);
        }
        //Image
        ImageView image = (ImageView) findViewById(R.id.fitnessImageView);
        image.setImageResource(R.drawable.fitness);

        //Date

        SimpleDateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        SimpleDateFormat timeFormat =  new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(Calendar.getInstance().getTime());

        displayDateTime(date, time);
    }

    private void displayDateTime(String date, String time){
        TextView dateTextView = (TextView) findViewById(R.id.currentDateView);
        dateTextView.setText(date);

        TextView timeTextView = (TextView) findViewById(R.id.currentTimeView2);
        timeTextView.setText(time);
    }
}
