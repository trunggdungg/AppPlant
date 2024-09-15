package eaut.myapp.appplant.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import eaut.myapp.appplant.R;
import eaut.myapp.appplant.adapter.OrderAdapter;
import eaut.myapp.appplant.model.Cart;
import eaut.myapp.appplant.model.Order;
import eaut.myapp.appplant.utils.Utils;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Cart> cartList = new ArrayList<>(); // Danh sách giỏ hàng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.cartRecyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set adapter
        orderAdapter = new OrderAdapter(getContext(), orderList);
        recyclerView.setAdapter(orderAdapter);
        // Log when fragment is created
        Log.d("HistoryFragment", "HistoryFragment is created");
        // Load orders and carts for the current user
        loadOrders();
        loadCarts();

        return view;
    }

    private void loadOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.orderByChild("id_user").equalTo(Utils.user_current.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void loadCarts() {
        System.out.println("cartList: " + cartList);
        DatabaseReference cartsRef = FirebaseDatabase.getInstance().getReference("cartList");
        cartsRef.orderByChild("id_user").equalTo(Utils.user_current.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                cartList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart cart = snapshot.getValue(Cart.class);
                    if (cart != null) {
                        cartList.add(cart);
                        Log.d("CartInfo", "Cart ID: " + cart.getId_tree());
                        Log.d("CartInfo", "Product Name: " + cart.getName_tree());
                        Log.d("CartInfo", "Quantity: " + cart.getQuantity());
                        Log.d("CartInfo", "Price: " + cart.getPrice_tree());
                        System.out.println("CartInfo" + cartList);
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("CartInfo", "Database Error: " + databaseError.getMessage());
            }
        });
    }
}

