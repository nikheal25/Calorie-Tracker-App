package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logInButton = (Button) findViewById(R.id.loginButton);
        Button signUpButton = (Button) findViewById(R.id.button);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.emailField);
                EditText password = (EditText) findViewById(R.id.passwordField);
                URL appUser;
                String textResult = "";
                JsonObject jsonObject = null;
                try {
                   jsonObject = new Retrieve().execute(email.getText().toString(), password.getText().toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                email.setText("");
                password.setText("");
                if( jsonObject == null){
                    TextView errorText = findViewById(R.id.errorField);
                    errorText.setText("Wrong credentials");
                }else{
                    JsonObject appUserDetails = jsonObject.getAsJsonObject().get("appuser").getAsJsonObject();

                    Intent intentDash;
                    intentDash = new Intent(MainActivity.this, NavActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("UserName", jsonObject.getAsJsonObject().get("appuser").getAsJsonObject().getAsJsonPrimitive("userName").toString());
                    bundle.putString("UserId", jsonObject.getAsJsonObject().get("appuser").getAsJsonObject().getAsJsonPrimitive("userId").toString());
                    bundle.putString("UserDetails", jsonObject.toString());
                    intentDash.putExtras(bundle);
                    startActivity(intentDash);

                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
