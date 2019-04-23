package com.example.myapplication;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class Retrieve extends AsyncTask<Object, Void, JsonObject> {
    private static final String BASE_URL = "http://10.0.2.2:8080/assgn/webresources/restws.credential/findByUserName/";

    @Override
    protected JsonObject doInBackground(Object[] objects) {
        JsonObject returnValue= null;
        URL credential;
        String userName = objects[0].toString();
        String passwordTyped = objects[1].toString();
        String textResult = "";
        HttpURLConnection connection = null;

        try {
           // credential = new URL("http://10.0.2.2:8080/assgn/webresources/restws.appuser/findByName/nik");
            credential = new URL(BASE_URL + userName);
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
                JsonObject jsonObject = (JsonObject) jsonArray.get(0);
                String password = jsonObject.getAsJsonObject().getAsJsonPrimitive("passwordHash").getAsString();
                if(password.equals(passwordTyped)) {
                    returnValue = jsonObject;
                } else{
                    returnValue = null;
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
        return returnValue;
    }

}
