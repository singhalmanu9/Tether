package com.example.yashaektefaie.calhacksproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.List;

import static com.example.yashaektefaie.calhacksproject.R.id.group_code;


public class join_group extends AppCompatActivity {

    private double lat, lng;
    protected LocationManager locationManager;
    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);


        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");


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

    }

    // -- Location Listener -- //

    protected LocationListener locationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.i("Location_listener", "Latitude:" + lat + ", Longitude:" + lng);


        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };


    public void check_group(View v){
        Log.i("Status","we are checking to find if your group exists");
        EditText ext = (EditText) findViewById(group_code);
        final String group_code = ext.getText().toString();

        //Reads group number and adds you to the group if it finds a match
        //Value event listener for realtime data update

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> groupCodes = new ArrayList<String>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    groupCodes.add(postSnapshot.getKey());
                }

                //do something with the numbers
                Log.i("group numbers","" + groupCodes);
                if (groupCodes.contains(group_code)){
                    Log.i("Status","Group has been found joining session....");
                    add_group_member(database, group_code);
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void add_group_member(FirebaseDatabase b, String group_code){
        DatabaseReference myRef = b.getReference(group_code);

        Log.i("Status","Received user info" + user_name);

//        DatabaseReference myRef = b.getReference(group_code);
        Log.i("INFO"," "+user_name);
        User user = new User();

        Log.i("Status", "Creating NEW USER");
        user.setName(user_name);
        user.setLatitude(lat);
        user.setLongitude(lng);

        Log.i("STATUS","GET NAME" + user.getName());

        myRef.child(user.getName()).setValue(user);

        Log.i("Status","Sending you to final group screen");
        Intent intent2 = new Intent(this,set_up_group.class);
        intent2.putExtra("group_code", group_code);
        intent2.putExtra("user_name", user_name);
        startActivity(intent2);
    }

}

