package api.order.entity;



public class RecommendationItem {
    private int id;
    private String name;
    private double price;
    private int imageResource;
    private boolean isFavorite;

    public RecommendationItem(int id, String name, double price, int imageResource, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
