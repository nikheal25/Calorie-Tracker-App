package com.example.myapplication;

import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalorieTrackerScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalorieTrackerScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalorieTrackerScreen extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int goal, steps, consumedCals, burnedCals;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calorie_tracker_screen, container, false);
        view.setBackgroundColor(Color.WHITE);
        getActivity().setTitle("Calorie Tracker Screen");
        try {
            ((TextView) view.findViewById(R.id.goalTrackerScreen)).setText(Integer.toString(getArguments().getInt("Goal")));
            //((TextView) view.findViewById(R.id.goalTrackerScreen)).setText(getArguments().getInt("Goal"));
            ((TextView) view.findViewById(R.id.stepsTrackerScreen)).setText(Integer.toString(getArguments().getInt("Steps")));
            ((TextView) view.findViewById(R.id.burnedTrackerScreen)).setText(Integer.toString(getArguments().getInt("BurnedCalories")));
            ((TextView) view.findViewById(R.id.consumedTrackerScreen)).setText(Integer.toString(getArguments().getInt("ConsumedCalories")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }



//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
