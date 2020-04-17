package com.example.gps;
/*POJO class created for storing the RFID details to firebase
 * Includes 2 constructor. 1 for all global variables and other an empty constructor
 * Includes getter and setter methods to obtain and set variables if required.
 */
public class RFIDDetails {
    String date;//stores the date of scanning of the RFID card
    String time;//stores the time of scanning of the RFID card
    String uid;//stores the UID ie the unique identification number of particular user

    /*constructor to intialize and assign value to global variables
        *created_at: is received from the Thingspeak channel and contains date and time at which the entry is done in the channel.
        * created_at is split into date and time in the constructor.
        * uid : is the identification number of the RFID card unique for each user.
     */
    public RFIDDetails(String created_at, String uid) {
        this.date = created_at.substring(0,10);
        this.time=created_at.substring(11,19);
        this.uid = uid;
    }

    public RFIDDetails() {
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
