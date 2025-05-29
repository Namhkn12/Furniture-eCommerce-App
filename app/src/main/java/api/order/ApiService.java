package api.order;

import api.order.entity.CartResponse;
import api.order.entity.OrderListResponse;
import api.order.entity.OrderResponse;
import api.order.entity.ProductResponse;
import api.order.entity.UpdateCartRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Cart endpoints
    @GET("cart")
    Call<CartResponse> getCart();

    @POST("cart/items")
    Call<CartResponse> addToCart(@Body UpdateCartRequest request);

    @PUT("cart/items/{itemId}")
    Call<CartResponse> updateCartItem(@Path("itemId") int itemId, @Body UpdateCartRequest request);

    @DELETE("cart/items/{itemId}")
    Call<CartResponse> removeCartItem(@Path("itemId") int itemId);

    @POST("cart/apply-coupon")
    Call<CartResponse> applyCoupon(@Query("code") String couponCode);

    // Order endpoints
    @GET("orders")
    Call<OrderListResponse> getOrders();

    @GET("orders/{orderId}")
    Call<OrderResponse> getOrderDetails(@Path("orderId") String orderId);

    @POST("orders")
    Call<OrderResponse> createOrder();

    @POST("orders/{orderId}/reorder")
    Call<CartResponse> reorder(@Path("orderId") String orderId);

    // Product recommendations
    @GET("products/recommendations")
    Call<ProductResponse> getRecommendations();
}
