package com.example.gps;
/*POJO class created for storing the GPS details to firebase
* Includes 2 constructor. 1 for all global variables and other an empty constructor
* Includes getter and setter methods to obtain and set variables if required.
*/
public class GpsDetails {
    Double latitude;
    Double longitude;
    Double speed;
    Double altitude;
    String date;
    String time;
    String email;

    /*
       * The constructor is used to initialize and assign values to the global variables.
       * All variables are stored in string format.
       * Change and use if required.

     */
    public GpsDetails(Double latitude, Double longitude, Double speed, Double altitude, String email,String date, String time) {
        this.latitude=latitude;
        this.longitude=longitude;
        this.altitude=altitude;
        this.speed=speed;
        this.date=date;
        this.time=time;
        this.email=email;
    }

    //Getter and setter for all global variables.

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
}
