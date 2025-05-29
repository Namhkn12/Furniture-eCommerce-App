package api.product;

import java.util.List;

import model.product.Category;
import model.product.Product;
import model.product.Selling;
import model.product.Shop;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {

    // Lấy tất cả sản phẩm
    @GET("products")
    Call<List<Product>> getAllProducts();

    // Lấy chi tiết sản phẩm theo ID
    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    // Lấy tất cả danh mục
    @GET("categories")
    Call<List<Category>> getAllCategories();

    @GET("shop/list")
    Call<List<Shop>> getAllShops();

    @GET("selling/product/{id}")
    Call<List<Selling>> getSellingByProduct(@Path("id") int id);

    @GET("product/popular")
    Call<List<Product>> getRandomProduct();

    @GET("product/search")
    Call<List<Product>> searchProductByName(@Query("query") String query, @Query("category") List<Integer> categoryIds);
}
