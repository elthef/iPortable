package com.example.wifi_no_internet;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    public String name,dateLastWeight;
    public float weight;
    long dbID;

    public Patient(long dbID, String name, String dateLastWeight, float weight) {
        this.dbID = dbID;
        this.name = name;
        this.dateLastWeight = dateLastWeight;
        this.weight = weight;
    }

    public Patient(String name, String dateLastWeight, float weight) {
        this.name = name;
        this.dateLastWeight = dateLastWeight;
        this.weight = weight;
    }

    public Patient(Parcel parsel){
        dbID = parsel.readLong();
        name=parsel.readString();
        weight = parsel.readFloat();
        dateLastWeight = parsel.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateLastWeight() {
        return dateLastWeight;
    }

    public void setDateLastWeight(String dateLastWeight) {
        this.dateLastWeight = dateLastWeight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getDbID() {
        return dbID;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dbID);
        dest.writeString(name);
        dest.writeFloat(weight);
        dest.writeString(dateLastWeight);
    }

    public static final Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>(){
        @Override
        public Patient createFromParcel(Parcel parcel) {
            return new Patient(parcel);
        }
        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

}
