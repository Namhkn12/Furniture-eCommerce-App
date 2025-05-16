package model.user;

import java.io.Serializable;

public class UserAddress implements Serializable {
    private int id;
    private String name;
    private String phoneNumber;
    private String road;
    private String city;

    public UserAddress(int id, String name, String phoneNumber, String road, String city) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.road = road;
        this.city = city;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRoad() {
        return road;
    }

    public String getCity() {
        return city;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullAddress() {
        return road + ", " + city;
    }
}

