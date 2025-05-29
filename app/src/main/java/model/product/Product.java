package model.product;

import java.io.Serializable;

public class Product implements Serializable {
    private int productId;
    private String productName;
    private Category productCategory;
    private String productDescription;
    private long productCost;

    public Product(int productId, String productName, Category productCategory, String productDescription, long productCost) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.productCost = productCost;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Category getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(Category productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public long getProductCost() {
        return productCost;
    }

    public void setProductCost(long productCost) {
        this.productCost = productCost;
    }
}
