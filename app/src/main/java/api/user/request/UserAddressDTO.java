package api.user.request;

public class UserAddressDTO {
    private String name;
    private String city;
    private String road;
    private String phoneNumber;

    public UserAddressDTO(String name, String city, String road, String phoneNumber) {
        this.name = name;
        this.city = city;
        this.road = road;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getRoad() {
        return road;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
