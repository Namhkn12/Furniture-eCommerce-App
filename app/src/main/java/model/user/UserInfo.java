package model.user;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class UserInfo implements Serializable {
    private int id;

    private String displayName;
    private String phoneNumber;
    private String gender;
    private String email;
    private LocalDate dateOfBirth;

    private List<UserAddress> addressList;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public List<UserAddress> getAddressList() {
        return addressList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddressList(List<UserAddress> addressList) {
        this.addressList = addressList;
    }
}
