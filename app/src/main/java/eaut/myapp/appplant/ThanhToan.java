package eaut.myapp.appplant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import eaut.myapp.appplant.model.Cart;
import eaut.myapp.appplant.model.Order;
import eaut.myapp.appplant.model.User;
import eaut.myapp.appplant.retrofit.ApiAppPlant;
import eaut.myapp.appplant.retrofit.RetrofitClient;
import eaut.myapp.appplant.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThanhToan extends AppCompatActivity {
    TextView TongTien, Email, Diachi;
    Toolbar toolbarThanhToan;
    AppCompatButton btnDatHang;
    private int id_user;
    private String status = "Chưa xử lý";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanhtoan);
        Paper.init(this);
        initView();
        initControl();

        // Lấy thông tin người dùng từ PaperDB
        User user = Utils.user_current;
        if (user != null) {
            id_user = Utils.user_current.getId();
            Log.d("Login", "User ID: " + id_user);
        } else {
            Log.d("Login", "User not found in PaperDB");
        }
    }

    private void initControl() {
        setSupportActionBar(toolbarThanhToan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarThanhToan.setNavigationOnClickListener(v -> finish());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        Float tongtien = getIntent().getFloatExtra("totalPrice", 0);

        TongTien.setText("Tong tien: " + decimalFormat.format(tongtien) + " VNĐ");
        Email.setText("Email: " + Utils.user_current.getEmail());

        // Xử lý khi người dùng nhấn nút đặt hàng
        btnDatHang.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String address_shipping = Diachi.getText().toString().trim();
        Float price = getIntent().getFloatExtra("totalPrice", 0);

        if (address_shipping.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy danh sách giỏ hàng từ nơi lưu trữ
        List<Cart> cartList = Utils.cartList;

        // Tạo đối tượng đơn hàng
        long timestamp = System.currentTimeMillis();
        String orderId = "ORD" + timestamp; // Tạo orderId duy nhất
        Order order = new Order(id_user, cartList, status, price, address_shipping, timestamp);

        // Lưu đơn hàng vào Firebase Realtime Database
        firebaseDatabase = FirebaseDatabase.getInstance("https://appplant-991ee-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("orders");
        String orderIdFirebase = databaseReference.push().getKey();

        if (orderIdFirebase != null) {
            databaseReference.child(orderIdFirebase).setValue(order)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Nếu lưu vào Firebase thành công, gửi dữ liệu tới server PHP
                            // Xóa giỏ hàng sau khi đặt hàng thành công
                            Utils.cartList = new ArrayList<>();

                            Toast.makeText(ThanhToan.this, "Đơn hàng của bạn đã được đặt, vui lòng chờ phản hồi!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ThanhToan.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Đóng activity hiện tại
                            sendOrderToServer(orderId, order);
                        } else {
                            Toast.makeText(ThanhToan.this, "Đặt hàng thất bại trên Firebase!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ThanhToan.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Không tạo được đơn hàng trên Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOrderToServer(String orderId, Order order) {
        // Chuyển cartList thành chuỗi JSON
        Gson gson = new Gson();
        String cartListJson = gson.toJson(order.getCartList());

        // Khởi tạo Retrofit và gọi API
        ApiAppPlant api = RetrofitClient.getInstance().create(ApiAppPlant.class);
        api.saveOrder(orderId, order.getId_user(), cartListJson, order.getStatus(), order.getPrice(), order.getAddress_shipping(), order.getTimestamp())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // Xử lý phản hồi từ server
                    if (response.isSuccess()) {
                        Toast.makeText(ThanhToan.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        if (response.getMessage().equals("Đơn hàng đã được lưu thành công")) {
                            // Xóa giỏ hàng sau khi đặt hàng thành công
                            Utils.cartList = new ArrayList<>();
                            Intent intent = new Intent(ThanhToan.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Đóng activity hiện tại
                        }
                    } else {
                        Toast.makeText(ThanhToan.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    // Xử lý lỗi nếu có
                    Toast.makeText(ThanhToan.this, "Lỗi khi gửi đơn hàng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void initView() {
        toolbarThanhToan = findViewById(R.id.toolbarThanhToan);
        TongTien = findViewById(R.id.txtTongtien);
        Email = findViewById(R.id.txtEmail);
        Diachi = findViewById(R.id.txtDiaChiGiaoHang);
        btnDatHang = findViewById(R.id.btnDatHang);
    }
}
