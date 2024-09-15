package eaut.myapp.appplant.retrofit;



import eaut.myapp.appplant.model.CartModel;
import eaut.myapp.appplant.model.ProductModel;
import eaut.myapp.appplant.model.UserModel;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ApiAppPlant {

    //POST DATA
    @POST("dangky.php")
    @FormUrlEncoded
    Observable<UserModel> dangky(
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("password") String password,
            @Field("uid") String uid
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangnhap(
            @Field("email") String email,
            @Field("password") String password

    );

    @GET("gettree.php")
    Observable<ProductModel> getsp();

    @FormUrlEncoded
    @POST("updatetoken2.php")
    Observable<UserModel> updatetoken(
            @Field("id_user") int id,
            @Field("token") String token
    );
    @FormUrlEncoded
    @POST("addcart.php")
    Observable<CartModel> addCart(
            @Field("id_user") int id_user,
            @Field("id_tree") int id_tree,
            @Field("quantity") int quantity,
            @Field("price") float price_tree

    );

    @FormUrlEncoded
    @POST("addbill.php")
    Observable<UserModel> addbill(
            @Field("id_user") int id_user,
            @Field("status") String status,
            @Field("total_price") float total_price,
            @Field("address_shipping") String address_shipping
    );

    @FormUrlEncoded
    @POST("save_order.php")
    Observable<UserModel> saveOrder(
            @Field("orderId") String orderId,
            @Field("id_user") int id_user,
            @Field("cartList") String cartListJson,
            @Field("status") String status,
            @Field("price") float price,
            @Field("address_shipping") String address_shipping,
            @Field("timestamp") long timestamp
    );

    @FormUrlEncoded
    @POST("searchtree.php")
    Observable<ProductModel> searchTree(@Field("name_tree") String name_tree);
}