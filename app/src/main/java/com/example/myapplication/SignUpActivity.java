package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.common.collect.ArrayTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class SignUpActivity extends AppCompatActivity {
    private EditText dateText, heightText, weightText, firstnameText, lastnameText, emailText, addressText, postcodeText, usenameText, passwordText;
    private Spinner spinner;
    private Button submitButton;
    private AwesomeValidation awesomeValidation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.setTitle("Sign Up");

        //edittext field
        firstnameText = findViewById(R.id.firstNameEditText);
        lastnameText = findViewById(R.id.surnameEditText);
        emailText = findViewById(R.id.emailEditText);
        dateText = findViewById(R.id.dateEditText);
        dateText.setInputType(InputType.TYPE_NULL);
        heightText = findViewById(R.id.heightText);
        weightText = findViewById(R.id.weightText);
        addressText = findViewById(R.id.addressText);
        postcodeText = findViewById(R.id.postcodeEditText);
        usenameText = findViewById(R.id.usernameEditText);
        passwordText = findViewById(R.id.passwordText);

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



        //Spinner
        displaySpinner();

        //button
        submitButton = (Button) findViewById(R.id.SignUpFinish);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.firstNameEditText, "[a-zA-Z]+", R.string.name_error);
        awesomeValidation.addValidation(this, R.id.firstNameEditText, "^\\w{0,20}$", R.string.name_length_error);
        awesomeValidation.addValidation(this, R.id.surnameEditText, "[a-zA-Z\\\\s]+", R.string.name_error);

        awesomeValidation.addValidation(this, R.id.emailEditText, "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", R.string.email_error);

        awesomeValidation.addValidation(this, R.id.heightText, "^[0-9]+$", R.string.number_error);
        awesomeValidation.addValidation(this, R.id.weightText, "^[0-9]+$", R.string.number_error);

        awesomeValidation.addValidation(this, R.id.addressText, "[a-zA-Z0-9]+", R.string.address_error);

        awesomeValidation.addValidation(this, R.id.postcodeEditText, "^[0-9]{4}$", R.string.postcode_error);

        awesomeValidation.addValidation(this, R.id.usernameEditText, "[a-zA-Z0-9]+", R.string.username_error);
        awesomeValidation.addValidation(this, R.id.passwordText, "[a-zA-Z0-9]+", R.string.password_error);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( awesomeValidation.validate()){
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(firstnameText.getText().toString());
                    arrayList.add(lastnameText.getText().toString());
                    arrayList.add(emailText.getText().toString());
                    arrayList.add(dateText.getText().toString());
                    arrayList.add(heightText.getText().toString());
                    arrayList.add(weightText.getText().toString());
                    arrayList.add(addressText.getText().toString());
                    arrayList.add(postcodeText.getText().toString());
                    arrayList.add(usenameText.getText().toString());
                    arrayList.add(passwordText.getText().toString());
                    addData(arrayList);
                }else{

                }
            }
        });

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

    private void addData(ArrayList<String> list){

    }

    class SignUpCall extends AsyncTask<ArrayList<String>, Void, String>{
        private static final String BASE_URL = "http://10.0.2.2:8080/assgn/webresources/restws.credential/findByUserName/";

        @Override
        protected String doInBackground(ArrayList<String>... arrayLists) {
            JsonObject returnValue= null;
            URL credential;
            ArrayList<Food> foods = null;
            String textResult = "";
            HttpURLConnection connection = null;

            try {
                // credential = new URL("http://10.0.2.2:8080/assgn/webresources/restws.appuser/findByName/nik");
                credential = new URL(BASE_URL);
                connection =  (HttpURLConnection) credential.openConnection();
                connection.setRequestMethod("GET");



                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();

                if(responseCode!=200){
                    returnValue = null;
                }else{
                    InputStream inputStream = connection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);
                    while (scanner.hasNextLine()) {
                        textResult += scanner.nextLine();
                    }
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = (JsonArray) parser.parse(textResult);

                    foods = new ArrayList<Food>();

                    if(jsonArray!=null){
                        for(int i =0; i<jsonArray.size();i++){
                            int foodId = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("foodId").getAsInt();
                            Food food = new Food();
                            food.setFoodId(foodId);
                            food.setFoodCategory(jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("foodCategory").getAsString());
                            food.setFoodName(jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("foodName").getAsString());
                            foods.add(food);
                        }

                    }


                }

            } catch (MalformedURLException e) {

                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}
