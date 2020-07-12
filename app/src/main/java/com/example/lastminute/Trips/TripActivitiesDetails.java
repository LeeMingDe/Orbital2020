package com.example.lastminute.Trips;
public class TripActivitiesDetails {
    String activityName, activityPlace, activityDate, activityTime, activityDescription, userID;

    public TripActivitiesDetails() {
    }

    public TripActivitiesDetails(String activityName, String activityPlace, String activityDate, String activityTime, String activityDescription, String userID) {
        this.activityName = activityName;
        this.activityPlace = activityPlace;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.activityDescription = activityDescription;
        this.userID = userID;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityPlace() {
        return activityPlace;
    }

    public void setActivityPlace(String activityPlace) {
        this.activityPlace = activityPlace;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "TripActivitiesDetails{" +
                "activityName='" + activityName + '\'' +
                "activityPlace='" + activityPlace + '\'' +
                "activityDate ='" + activityDate + '\'' +
                "activityTime ='" + activityTime + '\'' +
                "activityDescription ='" + activityTime + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
