package com.example.myapplication;

import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stepsDB = Room.databaseBuilder(getActivity().getApplicationContext(), StepstakenDatabase.class, "steps_taken_database").fallbackToDestructiveMigration().build();
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
        addSteps.setOnClickListener(this);
        return stepsView;
    }

    public interface OnFragmentInteractionListener {
    }
}

 class InsertDatabase extends AsyncTask<Object,Void, String>{
    @Override
    protected String doInBackground(Object... objects) {
        int stepsToAdd = Integer.parseInt(objects[0].toString());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateInDB = dateFormat.format(date);

        Stepstaken stepsTaken = new Stepstaken(dateInDB, stepsToAdd);
        StepstakenDatabase database = (StepstakenDatabase) objects[1];
        long returnVal = 0;
        try{
            returnVal = database.stepsTakenDao().insert(stepsTaken);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Long.toString(returnVal);
    }
}