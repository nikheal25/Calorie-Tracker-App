package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Steps.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Steps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Steps extends Fragment {
    private View stepsView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Steps Taken");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        stepsView = inflater.inflate(R.layout.fragment_steps,container,false);
        stepsView.setBackgroundColor(Color.WHITE);
        return stepsView;
    }
}
