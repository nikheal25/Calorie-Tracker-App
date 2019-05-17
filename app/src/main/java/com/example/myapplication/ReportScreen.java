package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportScreen extends Fragment {
    private View view;
    EditText datePicker,chartDate1, chartDate2;
    String dateSelected, barDate1, barDate2;
    PieChartView pieChartView;
    List<SliceValue> pieData;
    PieChartData pieChartData;
    int userid;
    BarChart chart;
    Button plotChartButton;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private int compareDates(String date1, String date2){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date dateFirst, dateSecond;
        try{
             dateFirst = dateFormat.parse(date1);
             dateSecond = dateFormat.parse(date2);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

        return (int) TimeUnit.DAYS.convert(dateSecond.getTime() - dateFirst.getTime(), TimeUnit.MILLISECONDS);
    }

    private void selectDate1(){
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog picker = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                calendar.set(year, month, dayOfMonth);
                barDate1 = dateFormat.format(calendar.getTime());
                chartDate1.setText(barDate1);

            }
        }, year, month, day);
        picker.show();
    }

    private void selectDate2(){
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog picker = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                calendar.set(year, month, dayOfMonth);
                barDate2 = dateFormat.format(calendar.getTime());
                chartDate2.setText(barDate2);

            }
        }, year, month, day);
        picker.show();
    }

    private void drawGraph(int totalDays){
        float[] values = {0,0,0,0,0};
        try {
            values = new BarchartData().execute(Integer.toString(this.userid), this.barDate1, this.barDate2).get();
        }catch (Exception e){
            e.printStackTrace();
        }
        ArrayList xVals = new ArrayList();

        xVals.add(this.barDate1);


        ArrayList yVals1 = new ArrayList();
        ArrayList yVals2 = new ArrayList();

        float calConsumedPerDay = values[1]/(totalDays+1);
        float calBurnedPerDay = (values[0] + values[2] + (values[3]*values[4]))/(totalDays+1);

        yVals1.add(new BarEntry(1, calBurnedPerDay));
        yVals2.add(new BarEntry(1, calConsumedPerDay));



        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "Calories Burned per day");
        set1.setColor(Color.parseColor("#003f5c"));
        set2 = new BarDataSet(yVals2, "Calories Consumed per day");
        set2.setColor(Color.parseColor("#ffa600"));
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        //  chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        //    chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(0, 0.1f, 1f);
        chart.getData().setHighlightEnabled(false);
        chart.invalidate();

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        //X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
//Y-axis
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_report_screen, container, false);
        view.setBackgroundColor(Color.WHITE);


        datePicker = view.findViewById(R.id.datePicker1);
        datePicker.setInputType(InputType.TYPE_NULL);
        chart = (BarChart) view.findViewById(R.id.barchart);

        try {
            userid = Integer.parseInt(((NavActivity) getActivity()).getUserId());
        }catch (Exception e){
            userid = 1;
            e.printStackTrace();
        }


        pieChartView = view.findViewById(R.id.chart);
        //BarData data = new BarData(getXAxisValues(), getDataSet());
        chartDate1 = view.findViewById(R.id.dates);
        chartDate2 = view.findViewById(R.id.dates2);

        chartDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate1();
            }
        });

        chartDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate2();
            }
        });

        plotChartButton = (Button) view.findViewById(R.id.button3);

        plotChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalDays = 0;
                if(barDate1!= null && barDate2 != null)
                    totalDays = compareDates(barDate1, barDate2);
                else{
                    Toast.makeText(getActivity(), "Please make sure both dates are filled",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (totalDays < 0){
                    Toast.makeText(getActivity(), R.string.date_invalid,
                            Toast.LENGTH_LONG).show();
                    return;
                }

               drawGraph(totalDays);



            }


        });


        //TODO remove the lines below
        pieData = new ArrayList<>();
//        pieData.add(new SliceValue(30, Color.parseColor("#003f5c")).setLabel("Calories Consumed "));
//        pieData.add(new SliceValue(30, Color.parseColor("#7a5195")).setLabel("Calories Burned "));
//        pieData.add(new SliceValue(30, Color.parseColor("#ffa600")).setLabel("Remaining Calories "));


        pieChartData = new PieChartData(pieData);
        pieChartView.setPieChartData(pieChartData);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        calendar.set(year, month, dayOfMonth);
                        dateSelected = dateFormat.format(calendar.getTime());
                        datePicker.setText(dateSelected);
                        adjustPieChart();
                    }
                }, year, month, day);
                picker.show();
            }
        });
        return view;
    }

    private void adjustPieChart(){
        String subQuery = "remainingCalories/";
                String idAndDate = this.userid+"/"+this.dateSelected;
        try {
            int[] result = new ReportQuery().execute(subQuery+idAndDate).get();
            pieData = new ArrayList<>();
            int total = result[0]+result[1]+result[2];
            int percentOne = (100*result[0])/total;
            int percentTwo = (100*result[1])/total;
            int percentThree = (100*result[2])/total;
            pieData.add(new SliceValue(result[0], Color.parseColor("#003f5c")).setLabel("Calories Consumed "+ percentOne +" %"));
            pieData.add(new SliceValue(result[1], Color.parseColor("#7a5195")).setLabel("Calories Burned "+percentTwo + " %"));
            pieData.add(new SliceValue(result[2], Color.parseColor("#ffa600")).setLabel("Remaining Calories "+percentThree + " %"));

            pieChartData = new PieChartData(pieData);
            pieChartData.setHasCenterCircle(true).setCenterText1("Report").setCenterText1FontSize(30).setCenterText1Color(Color.parseColor("#0097A7"));
            pieChartData.setHasLabels(true).setValueLabelTextSize(10);
            pieChartView.setPieChartData(pieChartData);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Report Screen");
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

class ReportQuery extends AsyncTask<String, Void, int[]> {
    private static final String BASE_URL = "http://10.0.2.2:8080/assgn/webresources/restws.appuser/";

    @Override
    protected int[] doInBackground(String[] objects) {
        String values= null;
        URL url;
        int[] returnArray = {0,0,0};

        HttpURLConnection connection = null;

        try {
            url = new URL(BASE_URL + objects[0]);
            connection =  (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            if(!(responseCode!=200)){
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);

                values = scanner.nextLine();

                JsonParser parser = new JsonParser();
                returnArray[0] = Math.abs(parser.parse(values).getAsJsonObject().get("Total Calories Consumed").getAsInt());
                returnArray[1] = Math.abs(parser.parse(values).getAsJsonObject().get("Total Calories Burned").getAsInt());
                returnArray[2] = Math.abs(parser.parse(values).getAsJsonObject().get("Renaining Calories").getAsInt());
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
        return returnArray;
    }

}

class BarchartData extends AsyncTask<String, Void, float[]> {
    private static final String BASE_URL = "http://10.0.2.2:8080/assgn/webresources/restws.appuser/";
    private static final String query1 = "calBurnedAtRest/";
    private static final String query2 = "returnStepsTaken/";
    private static final String query3 = "calculateCalBurnedPerStep/";

    @Override
    protected float[] doInBackground(String[] objects) {
        String values= null;
        URL url;
        float[] returnArray = {0,0,0, 0 , 0 };

        HttpURLConnection connection = null;

        try {
            url = new URL(BASE_URL + query1 + objects[0]);
            connection =  (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            if(!(responseCode!=200)){
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);

                values = scanner.nextLine();
                returnArray[0] = Float.parseFloat(values);
            }
            try{
                url = new URL(BASE_URL + query2 + objects[0] +"/"+ objects[1] +"/"+objects[2] );
                connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                responseCode = connection.getResponseCode();

                if(!(responseCode!=200)){
                    InputStream inputStream = connection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);

                    values = scanner.nextLine();
                    JsonParser parser = new JsonParser();
                    returnArray[1] = parser.parse(values).getAsJsonObject().get("Total Calories Consumed").getAsFloat();
                    returnArray[2] = parser.parse(values).getAsJsonObject().get("Total Calories Burned").getAsFloat();
                    returnArray[3] = parser.parse(values).getAsJsonObject().get("Total Steps Taken").getAsFloat();

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                url = new URL(BASE_URL + query3 + objects[0] );
                connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                responseCode = connection.getResponseCode();

                if(!(responseCode!=200)){
                    InputStream inputStream = connection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);

                    values = scanner.nextLine();
                    returnArray[4] =  Float.parseFloat(values);
                }
            }catch (Exception e){
                e.printStackTrace();
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
        return returnArray;
    }

}

