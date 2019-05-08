package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodList extends Fragment {
    private View view;
    private OnFragmentInteractionListener mListener;
    ArrayList<Food> foods;
    ArrayList<String> foodItems;
    Spinner subSpinner, spinner;



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
        subSpinner = (Spinner) view.findViewById(R.id.spinner2); //TODO not working

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
        return view;
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

