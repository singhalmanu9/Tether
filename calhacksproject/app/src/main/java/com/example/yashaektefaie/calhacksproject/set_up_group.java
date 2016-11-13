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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class set_up_group extends Activity {
    public String group_code;
    private double lat, lng;
    private boolean is_safe;
    public String user_name;

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
            DatabaseReference myRef = database.getReference(group_code);

            Log.i("Status","Received user info" + user_name);

            Log.i("INFO",""+user_name);
            User user = new User();

            Log.i("Status", "Updating user long/lat");
            user.setName(user_name);
            user.setLatitude(lat);
            user.setLongitude(lng);
            user.setIsSafe(true);

            myRef.child(user.getName()).setValue(user);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    ArrayAdapter<String> adapter;


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
                    tableArray.add(Boolean.toString(user.getIsSafe()));
                    tableArray.add(Double.toString(user.getLatitude()));
                    tableArray.add(Double.toString(user.getLongitude()));
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



