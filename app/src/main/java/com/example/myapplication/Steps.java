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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


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
    List<HashMap<String, String>> listViewArray;;

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
        stepField.setText("Success");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        stepsView = inflater.inflate(R.layout.fragment_steps,container,false);
        stepsView.setBackgroundColor(Color.WHITE);
       //old location
        Button addSteps = (Button) stepsView.findViewById(R.id.stepsButton);
        final EditText stepField = stepsView.findViewById(R.id.stepsTakenView);
        stepsDB = Room.databaseBuilder(getActivity().getApplicationContext(), StepstakenDatabase.class, "steps_taken_database").fallbackToDestructiveMigration().build();
        ArrayList<Stepstaken> listOfSteps = new ArrayList<>();
        try {
           listOfSteps = new ReadDatabase().execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        stepsListView = stepsView.findViewById(R.id.mobile_list);

         listViewArray = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> map = new HashMap<String,String>();
        for (Stepstaken step:listOfSteps) {
            map = new HashMap<String,String>();
            map.put("Time", step.getDate());
            map.put("StepsTaken", Integer.toString(step.getStepstaken()));
            listViewArray.add(map);
        }


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
                // StepstakenDatabase database = (StepstakenDatabase) objects[0];

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
                 steps= stepsDB.stepsTakenDao().getAll();
            }catch (Exception e){
                e.printStackTrace();
            }
            if ((steps.isEmpty() || steps == null) )
                return null;
            return (ArrayList<Stepstaken>) steps;
        }

    }
}

