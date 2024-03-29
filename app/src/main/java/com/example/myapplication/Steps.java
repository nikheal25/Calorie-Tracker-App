package com.example.myapplication;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Steps.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Steps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Steps extends Fragment implements View.OnClickListener {
    private View stepsView;
    public StepstakenDatabase stepsDB = null;
    ListView stepsListView;
    SimpleAdapter myListAdapter;
    String[] colHEAD = new String[] {"Time","StepsTaken"};
    List<HashMap<String, String>> listViewArray;
    ArrayList<Stepstaken> listOfSteps;
    private  int totalSteps = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Steps Taken");
    }
    @Override
    public void onClick(View v) {
        EditText stepField = v.findViewById(R.id.stepsTakenView);
        String steps = stepField.getText().toString();
        InsertDatabase database = new InsertDatabase();
        database.execute(steps);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        stepsView = inflater.inflate(R.layout.fragment_steps,container,false);
        stepsView.setBackgroundColor(Color.WHITE);

        Button addSteps = (Button) stepsView.findViewById(R.id.stepsButton);
        final EditText stepField = stepsView.findViewById(R.id.stepsTakenView);
        Button updateToDBButton = (Button) stepsView.findViewById(R.id.updateToDatabaseButton);

        stepsDB = Room.databaseBuilder(getActivity().getApplicationContext(), StepstakenDatabase.class, "steps_taken_database").fallbackToDestructiveMigration().build();
        listOfSteps = new ArrayList<>();
        try {
           listOfSteps = new ReadDatabase().execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        stepsListView = stepsView.findViewById(R.id.mobile_list);
try {
    totalSteps = 0;
    listViewArray = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map = new HashMap<String, String>();
    for (Stepstaken step : listOfSteps) {
        map = new HashMap<String, String>();
        map.put("Time", step.getDate());
        totalSteps += step.getStepstaken();
        map.put("StepsTaken", Integer.toString(step.getStepstaken()));
        listViewArray.add(map);
    }
}catch (Exception e){
    e.printStackTrace();
}
        ((NavActivity)getActivity()).setStepsGlobal(totalSteps);
        int[] dataCell = new int[] {R.id.stepsTime,R.id.stepsCount};
        myListAdapter =  new SimpleAdapter(this.getActivity(),listViewArray,R.layout.list_view,colHEAD,dataCell);
        stepsListView.setAdapter(myListAdapter);
        addSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String steps = stepField.getText().toString();
                    InsertDatabase database = new InsertDatabase();
                    database.execute(steps);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        updateToDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                 new insertToDB().execute(((NavActivity)getActivity()).getStepsGlobal(), ((NavActivity)getActivity()).getGlobalGoal(), ((NavActivity)getActivity()).getTotalCaloriesBurned(), ((NavActivity)getActivity()).getTotalCaloriesConsumed());

                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    new DeleteDatabase().execute();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    listOfSteps = new ArrayList<>();
                }
            }
        });
        return stepsView;
    }

    public interface OnFragmentInteractionListener {
    }

    private class InsertDatabase extends AsyncTask<Object,Void, String>{
        @Override
        protected String doInBackground(Object... objects) {
            int stepsToAdd = Integer.parseInt(objects[0].toString());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            long returnVal = 0;

            String dateInDB = dateFormat.format(date);
            try{
                Stepstaken stepsTaken = new Stepstaken(dateInDB, stepsToAdd);

                returnVal = stepsDB.stepsTakenDao().insert(stepsTaken);
            }catch (Exception e){
                e.printStackTrace();
            }

            return Long.toString(returnVal);
        }
    }

    private class ReadDatabase extends AsyncTask<Void, Void, ArrayList<Stepstaken>> {
        @Override
        protected ArrayList<Stepstaken> doInBackground(Void... params) {
            List<Stepstaken> steps=null;
            try {
                if(stepsDB!=null)
                 steps= stepsDB.stepsTakenDao().getAll();
            }catch (Exception e){
                e.printStackTrace();
            }
            if ((steps.isEmpty() || steps == null) )
                return null;
            return (ArrayList<Stepstaken>) steps;        }

    }

    private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            stepsDB.stepsTakenDao().deleteAll();
            return null;
        }
    }


}

class insertToDB extends AsyncTask<Integer, Void, Void>{
    private static final String BASE_URL =  "http://10.0.2.2:8080/assgn/webresources/restws.report/savedb";

    @Override
    protected Void doInBackground(Integer... param) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        // JsonObject jsonObject = new JsonObject();
        Report report = new Report();
        try {
            report.setReportId(new Random().nextInt());
            report.setTotalSteps(param[0]);
            report.setCalorieGoal(param[1]);
            report.setReportDate(dateFormat.parse(String.valueOf(date.getTime())));
            report.setTotalCaloriesBurn(param[2]);
            report.setCalorieConsumed(param[3]);
        }catch (Exception e){
            e.printStackTrace();
        }

        Gson gson =new Gson();
        String reportString=gson.toJson(report);

        HttpURLConnection connection = null;
        URL link = null;
        try{
            link = new URL(BASE_URL );
            connection =  (HttpURLConnection) link.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(reportString.getBytes().length);

            connection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(reportString);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            connection.disconnect();
        }
        return null;
    }
}

class Report{
    private Integer reportId;

    private Date reportDate;

    private int totalCaloriesBurn;

    private int totalSteps;

    private int calorieGoal;

    private int calorieConsumed;

    public Report() {
    }

    public void setCalorieConsumed(int calorieConsumed) {
        this.calorieConsumed = calorieConsumed;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public void setTotalCaloriesBurn(int totalCaloriesBurn) {
        this.totalCaloriesBurn = totalCaloriesBurn;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }
}