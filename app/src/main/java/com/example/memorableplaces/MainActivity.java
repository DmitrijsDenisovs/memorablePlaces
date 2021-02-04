package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static String addPlaceText = "Add a new place...";
    ArrayList<MemorablePlace> memorablePlaces = new ArrayList<MemorablePlace>();
    ListView placesListView;
    SharedPreferences sharedPreferences;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.memorableplaces.sharedpreferences", Context.MODE_PRIVATE);
        try {
            memorablePlaces = (ArrayList<MemorablePlace>) ObjectSerializer.deserialize(sharedPreferences.getString("list", ObjectSerializer.serialize(new ArrayList<MemorablePlace>())));
            Log.i("Places", memorablePlaces.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            Log.i("Error", "error");
        }
        placesListView = findViewById(R.id.listView);
        updateListView();
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                if(parent.getChildCount() == position + 1){
                    intent.putExtra("requestCode", 1);
                    startActivityForResult(intent, 1);
                }
                else{
                    Log.i("Position", Integer.toString(position));
                    intent.putExtra("placeToShow", memorablePlaces.get(position));
                    intent.putExtra("requestCode", 2);
                    startActivityForResult(intent, 2);
                }
            }
        });
    }
   
    protected void updateListView(){
            ArrayList<String> names = new ArrayList<String>();
            for (MemorablePlace place : memorablePlaces) {
                names.add(place.getName());
            }
            names.add(addPlaceText);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
            placesListView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && data.getSerializableExtra("place") != null) {
            MemorablePlace resultPlace = (MemorablePlace) data.getSerializableExtra("place");
            boolean placeDouble = false;
            for(MemorablePlace place : memorablePlaces){
                if(place.getLatitude() == resultPlace.getLatitude() && place.getLongitude() == resultPlace.getLongitude()){
                    placeDouble = true;
                    Toast.makeText(this, "Place's been already added", Toast.LENGTH_SHORT).show();
                }
            }
            if(placeDouble == false) {
                memorablePlaces.add(resultPlace);
                try {
                    sharedPreferences.edit().putString("list", ObjectSerializer.serialize(memorablePlaces)).apply();
                    Log.i("Obj", ObjectSerializer.serialize(memorablePlaces));
                }
                catch(Exception e){
                    Log.i("Error", "error");
                    e.printStackTrace();
                }
                updateListView();
            }
        }
    }
}