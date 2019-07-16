package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GoogleAPI extends AsyncTask<Object, Void, ArrayList<Object>> {
    private static final String BASE_URL = "https://www.googleapis.com/customsearch/v1?q=";
    private static  final String END_URL = "--------PUT YOUR KEY HERE------------";

    @Override
    protected ArrayList<Object> doInBackground(Object[] objects) {
//        JsonObject returnValue= null;
        URL searchEngine;
        String searchItem = objects[0].toString();
        ArrayList<Object> returnValue = new ArrayList<Object>();

        String textResult = "";
        HttpURLConnection connection = null;

        try {

            searchEngine = new URL(BASE_URL + searchItem+END_URL);
            connection =  (HttpURLConnection) searchEngine.openConnection();
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
                String info = "";
                try{
                    info = parser.parse(textResult).getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("snippet").getAsString();
                    info = info.replace("...","");
                }catch (Exception e){
                    e.printStackTrace();
                }
                returnValue.add(info);
                Bitmap bmp = null;
                try{
                    String imageURL = parser.parse(textResult).getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("pagemap").getAsJsonObject().get("metatags").getAsJsonArray().get(0).getAsJsonObject().get("og:image").getAsString();
                    InputStream in = new URL(imageURL).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                }catch (Exception e){
                    e.printStackTrace();
                }
                returnValue.add(bmp);
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
