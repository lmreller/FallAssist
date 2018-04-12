package com.struggleassist.View.Activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Controller.FallDetection;
import com.struggleassist.Controller.MessageListener;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Contents.HomeContent;
import com.struggleassist.View.Contents.IncidentReportContent;
import com.struggleassist.View.Contents.SettingsContent;
import com.struggleassist.View.Contents.ViewProfileContent;
import com.struggleassist.View.Notifications.NotificationController;

public class MainActivity extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    private static SharedPreferences settings;
    private static boolean fallDetectionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Set Home Screen
        fragmentManager = getSupportFragmentManager();
        fragment = new HomeContent();
        fragmentManager.beginTransaction()
                .replace(R.id.main_contentFrame,fragment)
                .commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        frameLayout = (FrameLayout) findViewById(R.id.main_contentFrame);

        settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
        fallDetectionPref = settings.getBoolean("pref_enable_fall_detection", false);

        Intent startIntent = new Intent(MainActivity.this, FallDetection.class);
        startIntent.setAction(NotificationController.IDLE_ACTION);

        if (fallDetectionPref)
            startService(startIntent);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Listen for items selected
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        drawerLayout.closeDrawers();
                        fragment = null;

                        //Remove all items from the backstack, except for the home page
                        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        //Update UI based on item selected
                        switch(item.getItemId()){
                            case R.id.nav_home:
                                //fragment = new HomeContent();
                                //Pop back stack to go to the home page, do not create new home page
                                break;
                            case R.id.nav_profile:
                                fragment = new ViewProfileContent();
                                break;
                            case R.id.nav_incident_report:
                                fragment = new IncidentReportContent();
                                break;
                            case R.id.nav_settings:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_contentFrame,new SettingsContent())
                                        .addToBackStack(BACK_STACK_ROOT_TAG)
                                        .commit();
                                break;
                            default:
                                break;
                        }
                        if(fragment!=null){
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_contentFrame,fragment)
                                    .addToBackStack(BACK_STACK_ROOT_TAG)
                                    .commit();
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isFallDetectionActive() {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("FallDetection.class".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void resetDB(View v){
        DatabaseController db = new DatabaseController(ViewContext.context);
        db.reset();
        ViewContext.getContext().deleteDatabase("User");
        ViewContext.getContext().deleteDatabase("Records");
    }

}
