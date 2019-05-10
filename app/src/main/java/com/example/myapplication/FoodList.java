package com.example.myapplication;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodList extends Fragment implements View.OnClickListener{
    private View view;
    private OnFragmentInteractionListener mListener;
    ArrayList<Food> foods;
    ArrayList<String> foodItems;
    Spinner subSpinner, spinner;
    private EditText searchFoodItemName;
    private TextView foodId, foodName, foodCalories, foodUnit, foodAmount, foodfat;
    private Button searchButton;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food_list, container, false);
        view.setBackgroundColor(Color.WHITE);

        spinner = (Spinner) view.findViewById(R.id.foodCategory1);
        subSpinner = (Spinner) view.findViewById(R.id.spinner2);

        foodId = (TextView) view.findViewById(R.id.textViewFoodItemNo2);
        foodName = (TextView) view.findViewById(R.id.textViewFoodItemName);
        foodCalories = (TextView) view.findViewById(R.id.textViewFooditemCalories);
        foodUnit = (TextView) view.findViewById(R.id.textViewFoodItemServing);
        foodAmount = (TextView) view.findViewById(R.id.textViewFoodItemAmount);
        foodfat = (TextView) view.findViewById(R.id.textViewFoodItemFat);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.food_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        try {
            FoodTableQuery foodTableQuery = new FoodTableQuery();
            foods = new ArrayList<Food>();
            foods = foodTableQuery.execute().get();
        }catch (Exception e){

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getSelectedItem().toString();

                String[] specificFoodItems = returnSpecificCategories(selectedCategory);
                ArrayAdapter<String> subArrayAdapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.simple_spinner_item, specificFoodItems);

                subArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subArrayAdapter.notifyDataSetChanged();
                subSpinner.setAdapter(subArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Search operation
        searchFoodItemName = (EditText) view.findViewById(R.id.newFoodItemName);
        searchButton = (Button) view.findViewById(R.id.buttonNewFoodItemSearch);

        searchButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        try{
            NDBAccess ndbAccess =new NDBAccess();
            String foodItemName = searchFoodItemName.getText().toString();
            if(foodItemName.length()>0){
                ArrayList<String> resultSet = ndbAccess.execute(foodItemName).get();
                if (resultSet.size() != 6){
                    throw new Exception();
                }
                foodName.setText(resultSet.get(0));
                foodId.setText(resultSet.get(1));
                foodCalories.setText(resultSet.get(2));
                foodUnit.setText(resultSet.get(3));
                foodAmount.setText(resultSet.get(4));
                foodfat.setText(resultSet.get(5));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String[] returnSpecificCategories(String category){
        foodItems = new ArrayList<String>();


        if(category.equals("Other")){
            Resources resources = getResources();
            String[] categoryArray = resources.getStringArray(R.array.food_category);
            for (Food food:foods) {
                if(!Arrays.asList(categoryArray).contains(food.getFoodCategory()))
                    foodItems.add(food.getFoodName());
            }
        }else {
            for (Food food:foods) {
                if(food.getFoodCategory().equals(category))
                    foodItems.add(food.getFoodName());
            }
        }
        String[] temp = foodItems.toArray(new String[0]);
        return temp;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Food Items");
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class NDBAccess extends AsyncTask<String,Void, ArrayList<String>> {
    private static final String BASE_URL = "https://api.nal.usda.gov/ndb/search/?format=json&q=";
    private static final String END_URL = "&sort=n&max=1&offset=0&api_key=12qSIATkTYDzE5u3KLxL5iUqAEHN8MZYJuyQHn5N";

    private static final String QUERY_BASE_URL = "https://api.nal.usda.gov/ndb/reports/?ndbno=";
    private static final String QUERY_END_URL = "&type=f&format=json&api_key=12qSIATkTYDzE5u3KLxL5iUqAEHN8MZYJuyQHn5N";

    private HttpURLConnection createConnectio(String startString, String middleString, String lastString){
        HttpURLConnection connection =null;
        URL ndbURL =null;
        try{
            ndbURL = new URL(startString+middleString+lastString);
            connection =  (HttpURLConnection) ndbURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    protected ArrayList<String> doInBackground(String... param) {
        String foodName = param[0];

        URL ndbURL;
        ArrayList<String> resultSet = new ArrayList<String>();
        resultSet.add(foodName);
        String textResult = "";
        HttpURLConnection connection = null;

        try {
            // credential = new URL("http://10.0.2.2:8080/assgn/webresources/restws.appuser/findByName/nik");
            ndbURL = new URL(BASE_URL+foodName+END_URL);
            connection =  (HttpURLConnection) ndbURL.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            if(responseCode!=200){
                resultSet.add("ERROR");
            }else {
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    textResult += scanner.nextLine();
                }
                JsonParser parser = new JsonParser();


                String ndbFoodId = parser.parse(textResult).getAsJsonObject().get("list").getAsJsonObject().get("item").getAsJsonArray().get(0).getAsJsonObject().get("ndbno").getAsString();
                resultSet.add(ndbFoodId);
                connection = createConnectio(QUERY_BASE_URL, ndbFoodId, QUERY_END_URL);

                textResult = "";
                inputStream = connection.getInputStream();
                scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    textResult += scanner.nextLine();
                }

                parser = new JsonParser();
                JsonObject jsonObject = (JsonObject) parser.parse(textResult).getAsJsonObject().get("report").getAsJsonObject().get("food").getAsJsonObject().get("nutrients").getAsJsonArray().get(0);

                resultSet.add(jsonObject.get("value").getAsString());
                resultSet.add(jsonObject.get("measures").getAsJsonArray().get(0).getAsJsonObject().get("label").getAsString());
                resultSet.add(jsonObject.get("measures").getAsJsonArray().get(0).getAsJsonObject().get("qty").getAsString());
                resultSet.add(jsonObject.get("measures").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString());
               // JsonReader jsonReader = new JsonReader(textResult);

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
        return resultSet;
    }

}

class FoodTableQuery extends AsyncTask<Void,Void, ArrayList<Food>> {
    private static final String BASE_URL = "http://10.0.2.2:8080/assgn/webresources/restws.food/findAll/";

    @Override
    protected ArrayList<Food> doInBackground(Void... param) {
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
        return foods;
    }

}

class Food{
    private int foodId;
    private double calorieAmount, fat, servingAmount;
    private String foodCategory, foodName, servingunit;

    public Food() {
    }

    public Food(int foodId, double calorieAmount, double fat, double servingAmount, String foodCategory, String foodName, String servingunit) {
        this.foodId = foodId;
        this.calorieAmount = calorieAmount;
        this.fat = fat;
        this.servingAmount = servingAmount;
        this.foodCategory = foodCategory;
        this.foodName = foodName;
        this.servingunit = servingunit;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public double getCalorieAmount() {
        return calorieAmount;
    }

    public void setCalorieAmount(double calorieAmount) {
        this.calorieAmount = calorieAmount;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getServingAmount() {
        return servingAmount;
    }

    public void setServingAmount(double servingAmount) {
        this.servingAmount = servingAmount;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getServingunit() {
        return servingunit;
    }

    public void setServingunit(String servingunit) {
        this.servingunit = servingunit;
    }
}

