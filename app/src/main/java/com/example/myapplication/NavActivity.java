package com.example.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Steps.OnFragmentInteractionListener {

    //Shared preference
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //
        setTitle("Calorie Tracker");
        String name = "User";
        TextView userName = findViewById(R.id.textView2);
        try {
            Bundle bundle = getIntent().getExtras();

            name = bundle.get("UserName").toString();

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

        //update the goal
        Button updateGoal = (Button) findViewById(R.id.updateGoal);
        updateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView timeTextView = (TextView) findViewById(R.id.dailyGoalTextField);
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
               fragment = new MyDietScreen();
               break;
           case R.id.nav_steps:
               fragment = new Steps();
               break;
           case R.id.nav_food:
               fragment = new FoodList();
               break;
       }
//      if(fragment != null){
//           FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
//           frameLayout.removeAllViews();
//           FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//           fragmentTransaction.replace(R.id.frame_layout, fragment);
//           fragmentTransaction.commit();
//       }
        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout,
                    fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
