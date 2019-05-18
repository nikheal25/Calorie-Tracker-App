package com.example.myapplication;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Steps.OnFragmentInteractionListener {

    private String userId, userAddress, userPostcode;
    private int globalGoal, stepsGlobal, totalCaloriesBurned, totalCaloriesConsumed;

    public int getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public int getTotalCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesBurned(int totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public void setTotalCaloriesConsumed(int totalCaloriesConsumed) {
        this.totalCaloriesConsumed = totalCaloriesConsumed;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserPostcode() {
        return userPostcode;
    }

    public int getGlobalGoal() {
        return globalGoal;
    }

    public int getStepsGlobal() {
        return stepsGlobal;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setStepsGlobal(int stepsGlobal) {
        this.stepsGlobal = stepsGlobal;
    }

    public String getUserId() {
        return userId;
    }



    //Shared preference
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTimeInMillis(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
            cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
            cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
            Intent intent1 = new Intent(NavActivity.this, AlarmReceiver.class);
            PendingIntent pintent = PendingIntent.getService(NavActivity.this, 0, intent1, 0);
            intent1.putExtra("step", this.getStepsGlobal());
            intent1.putExtra("goal", this.getGlobalGoal());
            intent1.putExtra("consumed", this.getTotalCaloriesConsumed());
            intent1.putExtra("burned", this.getTotalCaloriesBurned());

            AlarmManager alarm = (AlarmManager) NavActivity.this.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30 * 1000, pintent);
        }catch (Exception e){
            e.printStackTrace();
        }


        setTitle("Calorie Tracker");
        String name = "User";
        TextView userName = findViewById(R.id.textView2);
        try {
            Bundle bundle = getIntent().getExtras();

            name = bundle.get("UserName").toString();
            userId = bundle.get("UserId").toString();
            userAddress = bundle.get("address").toString();
            userPostcode = bundle.get("postcode").toString();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            userName.setText("Welcome " + name);
        }

        //Image draw
        drawImage();

        //Time and Date
        SimpleDateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        SimpleDateFormat timeFormat =  new SimpleDateFormat("HH:mm:ss");
        final String time = timeFormat.format(Calendar.getInstance().getTime());

        displayDateTime(date, time);

        //reading the shared preferences
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int goalOfTheDay = getGoalOfTheDay(date.toString());
        displayGoalOfTheDay(goalOfTheDay);
        this.globalGoal = goalOfTheDay;


        final TextView timeTextView = (TextView) findViewById(R.id.dailyGoalTextField);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempGoal = timeTextView.getText().toString();
                try{
                    int goal = Integer.parseInt(tempGoal);
                    if(goal > 0){
                        updateGoalOfTheDay(goal);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }



    private void updateGoalOfTheDay(int goal){
        SimpleDateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(date, goal);
        editor.apply();
    }

    private int getGoalOfTheDay(String date){
        return sharedPreferences.getInt(date, 0);
    }

    private void displayGoalOfTheDay(int goal){
        TextView timeTextView = (TextView) findViewById(R.id.dailyGoalTextField);
        timeTextView.setText(Integer.toString(goal));
    }

    private void displayDateTime(String date, String time){
        TextView dateTextView = (TextView) findViewById(R.id.dateText);
        dateTextView.setText(date);

        TextView timeTextView = (TextView) findViewById(R.id.timeText);
        timeTextView.setText(time);
    }

    private void drawImage(){
        //Image
        ImageView image = (ImageView) findViewById(R.id.fitnessImageView);
        image.setImageResource(R.drawable.fitness);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;


       switch (id){
           case R.id.nav_my_diet:
               fragment = new FoodList();
               break;

           case R.id.nav_steps:
               fragment = new Steps();
               break;

           case R.id.nav_tracker:
               Bundle bundle1 = new Bundle();
               bundle1.putInt("Steps", this.stepsGlobal);
               bundle1.putInt("Goal", this.globalGoal);
                //
               int[] result = {0,0,0};
               try {
                   SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                   String subQuery = "remainingCalories/";
                   String idAndDate = this.userId + "/" + dateFormat.format(Calendar.getInstance().getTime());
                   result = new ReportQuery().execute(subQuery + idAndDate).get();
               }catch (Exception e){
                   e.printStackTrace();
               }
               this.setTotalCaloriesConsumed(result[0]);
               this.setTotalCaloriesBurned(result[1]);
               bundle1.putInt("ConsumedCalories",result[0]);
               bundle1.putInt("BurnedCalories", result[1]);

               fragment = new CalorieTrackerScreen();
               fragment.setArguments(bundle1);
               break;

           case R.id.nav_report:
               fragment = new ReportScreen();
               break;

           case R.id.nav_map:
               Intent intentDash = new Intent(NavActivity.this, MapsActivity.class);
               Bundle bundle = new Bundle();
               bundle.putString("address", this.userAddress);
               bundle.putString("postcode", this.userPostcode);
               intentDash.putExtras(bundle);
               startActivity(intentDash);
               break;


       }

        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout,
                    fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class AlarmReceiver extends IntentService {
        public AlarmReceiver(String name) {
            super(name);
        }

        protected void onHandleIntent(Intent workIntent) {
            try{
                new insertToDB().execute(workIntent.getIntExtra("step", 0), workIntent.getIntExtra("goal", 0), workIntent.getIntExtra("burned", 0), workIntent.getIntExtra("consumed", 0));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
