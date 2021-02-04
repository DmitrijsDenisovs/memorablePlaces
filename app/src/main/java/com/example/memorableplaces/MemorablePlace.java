package com.example.memorableplaces;

import java.io.Serializable;

public class MemorablePlace implements Serializable {
    protected double latitude;
    protected double longitude;
    protected String name;
    protected String description;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){ this.description = description; }


    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }

    public MemorablePlace(double longitude, double latitude, String name){
        setLatitude(latitude);
        setLongitude(longitude);
        setName(name);
    }
    public MemorablePlace(double longitude, double latitude, String name, String description){
        setLatitude(latitude);
        setLongitude(longitude);
        setName(name);
        setDescription(description);
    }

}
