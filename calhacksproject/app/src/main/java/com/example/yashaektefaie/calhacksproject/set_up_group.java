package com.example.yashaektefaie.calhacksproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class set_up_group extends Activity {
    public String group_code;
    private double lat, lng;
    private boolean is_safe = true;
    public String user_name;
    public boolean calculations = false;
    public int safety_radius = 10;

    protected LocationManager locationManager;

    // -- Location Listener -- //

    protected LocationListener locationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.i("Location_listener", "Latitude:" + lat + ", Longitude:" + lng);

            // -- Upload latitute and longitute real time to database --//

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference(group_code);

            Log.i("Status","Received user info" + user_name);

            Log.i("INFO",""+user_name);
            final User user = new User();

            Log.i("Status", "Updating user long/lat");
            user.setName(user_name);
            user.setLatitude(lat);
            user.setLongitude(lng);


            //check to see if party was pressed, if so calculations should be true
            if (calculations){
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<String> tableArray = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            User user = postSnapshot.getValue(User.class);;
                            tableArray.add(Double.toString(user.getLatitude()));
                            tableArray.add(Double.toString(user.getLongitude()));
                        }
                        is_safe = run_calculations(tableArray);
                        Log.i("IS_SAFE HAS BEEN SET TO",""+is_safe);

                        Log.i("Calculation result",""+is_safe);

                        user.setIsSafe(is_safe);
                        myRef.child(user.getName()).setValue(user);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });

            }



        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    ArrayAdapter<String> adapter;

    public boolean run_calculations(ArrayList t){

        Log.i("longitudes","" + t.get(0));
        Log.i("longitudes","" + t.get(2));

        Log.i("latitudes","" + t.get(1));
        Log.i("latitudes","" + t.get(3));

        double x1 = Double.parseDouble(t.get(1).toString()) * 112645.08;
        double y1 = Double.parseDouble(t.get(0).toString()) * 112645.08;

        double x2 = Double.parseDouble(t.get(3).toString()) * 112645.08;
        double y2 = Double.parseDouble(t.get(2).toString()) * 112645.08;

        double distance = Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
        Log.i("x1", " " +x1);
        Log.i("x2", " " +x2);
        Log.i("21", " " +y1);
        Log.i("y2", " " +y2);

        Log.i("DISTANCE", "" + distance);

        if (distance < safety_radius){
            Log.i("Distance","DISTANCE IS LESS THAN SAFETY RADIUS");
            return true;
        }else{
            Log.i("DISTANCE","Distance is more than safety radius");
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_group);



        //--Set up Location Manager --//


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }


        //--Continue with group generation key --//


        Log.i("Status","generating group key");
        Intent intent = getIntent();
        group_code = intent.getStringExtra("group_code");
        user_name = intent.getStringExtra("user_name");
        update_group_code();
        display_current_users();
    }

    public void update_group_code(){
        TextView v = (TextView) findViewById(R.id.group_name);
        v.setText(group_code);
    }

    public void display_current_users(){

        //Array where first term is the person name and the second is the safety
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(group_code);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> tableArray = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    tableArray.add(user.getName());
                    boolean temporary = user.getIsSafe();
                    if (temporary) {
                        tableArray.add("\t\t\t\tStatus: Safe");
                    } else {
                        tableArray.add("\t\t\t\tStatus: Not Safe");
                        tableArray.add(Boolean.toString(user.getIsSafe()));
                        tableArray.add(Double.toString(user.getLatitude()));
                        tableArray.add(Double.toString(user.getLongitude()));
                    }
                }
                Log.i("TABLE ARRAY", ""+tableArray);
                set_up_listview(tableArray);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void set_up_listview(ArrayList<String> tableArray){


        ListView myListView = (ListView) findViewById(R.id.status_update);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                tableArray);
        myListView.setAdapter(adapter);

    }


    public void party(View v){

        Log.i("Status","Initializing Party");

        //Get Rid of Button
        Button b = (Button) findViewById(R.id.party);
        b.setVisibility(GONE);

        //initiate calculations
        calculations = true;

    }

}

class User {

    public User(){

    }

    String name;
    double longitude;
    double latitude;
    boolean isSafe = true;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public boolean getIsSafe(){
        return isSafe;
    }

    public void setIsSafe(boolean isSafe){
        this.isSafe = isSafe;
    }

}
