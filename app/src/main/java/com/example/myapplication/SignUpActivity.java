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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {
    private EditText dateText, heightText, weightText, firstnameText, lastnameText, emailText, addressText, postcodeText, usenameText, passwordText, stepsPerMile;
    private Spinner spinner;
    private Button submitButton;
    private AwesomeValidation awesomeValidation;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.setTitle("Sign Up");

        //edittext field
        firstnameText = findViewById(R.id.firstNameEditText);
        stepsPerMile = findViewById(R.id.stepsPerMile);
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
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

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

        awesomeValidation.addValidation(this, R.id.surnameEditText, "[a-zA-Z\\\\s]+", R.string.name_error);

        awesomeValidation.addValidation(this, R.id.emailEditText, "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", R.string.email_error);

        awesomeValidation.addValidation(this, R.id.heightText, "^[0-9]+$", R.string.number_error);
        awesomeValidation.addValidation(this, R.id.weightText, "^[0-9]+$", R.string.number_error);
        awesomeValidation.addValidation(this, R.id.stepsPerMile, "^[0-9]+$", R.string.number_error);

        awesomeValidation.addValidation(this, R.id.addressText, "[\\w',-\\\\/.\\s]+", R.string.address_error);

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

                    int selectedId = radioSexGroup.getCheckedRadioButtonId();
                    radioSexButton = (RadioButton) findViewById(selectedId);
                    if(radioSexButton.getText().equals("Male")){
                        arrayList.add("M");
                    }else{
                        arrayList.add("F");
                    }
                    arrayList.add(spinner.getSelectedItem().toString());
                    arrayList.add(stepsPerMile.getText().toString());
                    addData(arrayList);
                    JsonObject jsonObject = null;
                    try{
                        jsonObject = new Retrieve().execute(usenameText.getText().toString(), passwordText.getText().toString()).get();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(jsonObject!=null){
                        Toast.makeText(getApplicationContext(),"username already exists",Toast.LENGTH_SHORT).show();
                    }else{
                        new SignUpCall().execute(arrayList);
                    }
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
        private static final String BASE_URL =  "http://10.0.2.2:8080/assgn/webresources/restws.appuser/";


        @Override
        protected String doInBackground(ArrayList<String>... arrayLists) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            ArrayList<String> temp = arrayLists[0];
            Appuser appuser = new Appuser();
            appuser.setUserName(temp.get(0));
            appuser.setUserSurname(temp.get(1));
            appuser.setUserEmail(temp.get(2));
            try {
                appuser.setUserDob(new SimpleDateFormat("dd-MM-yyyy").parse(temp.get(3)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            appuser.setUserId(new Random(1000).nextInt());
            appuser.setUserHeight((short) Integer.parseInt(temp.get(4)));
            appuser.setUserWeight((short) Integer.parseInt(temp.get(5)));
            appuser.setUserAddress(temp.get(6));
            appuser.setUserPostcode(Short.parseShort(temp.get(7)));
            appuser.setUserName(temp.get(8));
            appuser.setUserLevelOfActivity(Short.parseShort(temp.get(11)));
            appuser.setUserGender(temp.get(10).toCharArray()[0]);
            appuser.setUserStepsMile(Integer.parseInt(temp.get(12)));

            Gson gson =new Gson();
            String userDetails=gson.toJson(appuser);
            HttpURLConnection connection = null;
            URL link = null;
            try{
                link = new URL(BASE_URL );
                connection =  (HttpURLConnection) link.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(userDetails.getBytes().length);

                connection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(userDetails);
                writer.flush();
                writer.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                connection.disconnect();
            }
            return null;
        }
    }
}

class Appuser {

    private Integer userId;

    private String userName;

    private String userSurname;

    private String userEmail;

    private Date userDob;

    private short userHeight;

    private short userWeight;

    private Character userGender;

    private String userAddress;

    private short userPostcode;

    private short userLevelOfActivity;

    private int userStepsMile;




    public Appuser() {
    }

    public Appuser(Integer userId) {
        this.userId = userId;
    }

    public Appuser(Integer userId, String userName, String userSurname, String userEmail, Date userDob, short userHeight, short userWeight, Character userGender, String userAddress, short userPostcode, short userLevelOfActivity, int userStepsMile) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userDob = userDob;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.userGender = userGender;
        this.userAddress = userAddress;
        this.userPostcode = userPostcode;
        this.userLevelOfActivity = userLevelOfActivity;
        this.userStepsMile = userStepsMile;

    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserDob(Date userDob) {
        this.userDob = userDob;
    }

    public void setUserHeight(short userHeight) {
        this.userHeight = userHeight;
    }

    public void setUserWeight(short userWeight) {
        this.userWeight = userWeight;
    }

    public void setUserGender(Character userGender) {
        this.userGender = userGender;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setUserPostcode(short userPostcode) {
        this.userPostcode = userPostcode;
    }

    public void setUserLevelOfActivity(short userLevelOfActivity) {
        this.userLevelOfActivity = userLevelOfActivity;
    }

    public void setUserStepsMile(int userStepsMile) {
        this.userStepsMile = userStepsMile;
    }


}