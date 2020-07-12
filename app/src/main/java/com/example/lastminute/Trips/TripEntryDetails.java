package com.example.lastminute.Trips;

public class TripEntryDetails {

    String tripName, tripPlace, startDate, endDate;
    String userID;

    public TripEntryDetails() {
    }

    public TripEntryDetails(String tripName, String tripPlace, String startDate, String endDate, String userID) {
        this.tripName = tripName;
        this.tripPlace = tripPlace;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripPlace() {
        return tripPlace;
    }

    public void setTripPlace(String tripPlace) {
        this.tripPlace = tripPlace;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "TripEntryDetails{" +
                "tripName='" + tripName + '\'' +
                "tripPlace='" + tripPlace + '\'' +
                "startDate ='" + startDate + '\'' +
                "endDate ='" + endDate + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }

}