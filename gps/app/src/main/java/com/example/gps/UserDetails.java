package com.example.gps;
/*POJO class created for storing the User details to firebase
 * Includes 2 constructor. 1 for all global variables and other an empty constructor
 * Includes getter and setter methods to obtain and set variables if required.
 */
public class UserDetails {
    String name;
    String email;
    String phone;
    String rfid;

    /*
     * The constructor is used to initialize and assign values to the global variables.
     * All variables are stored in string format.
     * Change and use if required.
     * Stores details to firebase and is used later to get RFID number for the user.
     * Details can also be used at client side to send notifications.
     */

    public UserDetails(String name, String email, String phone, String rfid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rfid = rfid;
    }

    //Empty Constructor
    public UserDetails() {
    }

    //Getter and setter for all global variables.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
