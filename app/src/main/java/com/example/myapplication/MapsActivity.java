package com.example.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String latitude, longitude;
    private LatLng homeLocation;
    private ArrayList<LocationDetails> parks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        String completeAddress = null;

        try {
            completeAddress = bundle.get("address").toString() + " " + bundle.get("postcode").toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        getLocationFromAddress(completeAddress);
        parks = null;

        try{
            parks = new ParkFinder().execute(this.homeLocation.latitude,this.homeLocation.longitude).get();
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    public void getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                throw new Exception();
            }

            Address location = address.get(0);
            homeLocation = new LatLng(location.getLatitude(), location.getLongitude() );


        }catch (Exception e){
            e.printStackTrace();
            homeLocation = new LatLng(-34, 151);
        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(homeLocation).title("Home"));

        for (int i =0 ; i< parks.size();i++)
            displayParks(parks.get(i));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLocation, 10));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLocation));



    }

    private void displayParks(LocationDetails park){
        try {
            mMap.addMarker(new MarkerOptions().position(new LatLng(park.getLatitude(), park.getLogitude())).title(park.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(park.getDetails()).draggable(true));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


class ParkFinder extends AsyncTask<Object, Void, ArrayList<LocationDetails>> {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private static final String keyAndType = "&radius=5000&type=park&key=AIzaSyCz7hedipv9239FSw-iCRASvI2YJdQv6zY";

    @Override
    protected ArrayList<LocationDetails> doInBackground(Object[] objects) {

        URL credential;
        String latitude = objects[0].toString();
        String logitude = objects[1].toString();
        String textResult = "";
        HttpURLConnection connection = null;
        ArrayList<LocationDetails> locations = new ArrayList<LocationDetails>();

        try {
            // credential = new URL("http://10.0.2.2:8080/assgn/webresources/restws.appuser/findByName/nik");
            credential = new URL(BASE_URL +latitude +","+ logitude + keyAndType);
            connection =  (HttpURLConnection) credential.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            if(responseCode!=200){

            }else{
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    textResult += scanner.nextLine();
                }
                JsonParser parser = new JsonParser();

                JsonArray jsonArray = parser.parse(textResult).getAsJsonObject().get("results").getAsJsonArray();
                for(int i =0 ; i< jsonArray.size();i++){
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    try {
                    float latitude1 = jsonObject.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsFloat();
                    float logitude1 = jsonObject.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsFloat();

                        String name = jsonObject.get("name").getAsString();
                        String vicinity = jsonObject.get("vicinity").getAsString();
                        String rating = jsonObject.get("rating").getAsString();
                        String openNow = "closed";
                        if (jsonObject.get("opening_hours").getAsJsonObject().get("open_now").getAsString().equalsIgnoreCase("true"))
                            openNow = "Open";
                        locations.add(new LocationDetails(name, openNow, vicinity, rating, latitude1, logitude1));
                    }catch (Exception e){
                        e.printStackTrace();
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
        return locations;
    }

}

class LocationDetails{


    private String name, open, vicinity, rating;
    private float latitude, logitude;


    public LocationDetails(String name, String open, String vicinity, String rating, float latitude, float logitude) {
        this.name = name;
        this.open = open;
        this.vicinity = vicinity;
        this.rating = rating;
        this.latitude = latitude;
        this.logitude = logitude;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getRating() {
        return rating;
    }

    public String getDetails(){
        return this.vicinity +System.getProperty("line.separator") + this.rating +System.getProperty("line.separator") + this.open;
    }

    public String getName() {
        return name;
    }

    public String getOpen() {
        return open;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLogitude() {
        return logitude;
    }
}

