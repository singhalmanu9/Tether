package com.example.yashaektefaie.calhacksproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void create_group(View v){
        Log.i("Status", "Creating group");
        EditText mEdit   = (EditText)findViewById(R.id.name);
        String user_name = mEdit.getText().toString();

        String group_code = Integer.toString(random_code());

        Log.i("Status","Creating new group off of new random generated code");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(group_code);

        User user = new User();

        user.setName(user_name);

        user.setIsSafe(true);

        myRef.child(user_name).setValue(user);

        Intent intent = new Intent(this, set_up_group.class);
        intent.putExtra("user_name", user_name);
        intent.putExtra("group_code", group_code);
        startActivity(intent);
    }

    public void join_group(View v){
        Log.i("Status", "Joining Group");
        EditText mEdit   = (EditText)findViewById(R.id.name);
        String user_name = mEdit.getText().toString();
        Intent intent = new Intent(this, join_group.class);
        Log.i("INFO", ""+ user_name);
        intent.putExtra("user_name", user_name);
        startActivity(intent);

    }

    public int random_code(){
        ArrayList<Integer> used = new ArrayList<>();
        int code = ((int)((Math.random()*90000) + 10000));
        while(used.contains(code)){
            code = ((int)((Math.random()*90000) + 10000));
        }
        return code;
    }
}



