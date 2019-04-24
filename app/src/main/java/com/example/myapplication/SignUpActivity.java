package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private EditText dateText, heightText, weightText;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.setTitle("Sign Up");

        dateText = findViewById(R.id.dateEditText);
        dateText.setInputType(InputType.TYPE_NULL);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateText.setText(dayOfMonth+"-"+(month + 1) +"-"+year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        heightText = findViewById(R.id.heightText);

        //Spinner
        displaySpinner();


    }

    private void displaySpinner(){
        List<Integer> levelOfActivity = new ArrayList<Integer>();
        levelOfActivity.add(1);
        levelOfActivity.add(2);
        levelOfActivity.add(3);
        levelOfActivity.add(4);
        levelOfActivity.add(5);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item,levelOfActivity);

        spinner.setAdapter(arrayAdapter);
    }

}
