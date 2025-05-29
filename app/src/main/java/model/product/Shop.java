package model.product;

import java.io.Serializable;

public class Shop implements Serializable {
    private int shopId;
    private String shopName;
    private String shopLocation;
    private String shopPhoneNumber;

    public Shop(int shopId, String shopName, String shopLocation, String shopPhoneNumber) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopLocation = shopLocation;
        this.shopPhoneNumber = shopPhoneNumber;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
    }

    public String getShopPhoneNumber() {
        return shopPhoneNumber;
    }

    public void setShopPhoneNumber(String shopPhoneNumber) {
        this.shopPhoneNumber = shopPhoneNumber;
    }
}
