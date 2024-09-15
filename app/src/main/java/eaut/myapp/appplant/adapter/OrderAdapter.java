package eaut.myapp.appplant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import eaut.myapp.appplant.R;
import eaut.myapp.appplant.model.Cart;
import eaut.myapp.appplant.model.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_oder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orderList.get(position);

        if (order != null) {
            if (holder.totalPrice != null) {
                holder.totalPrice.setText("Tổng: " + order.getPrice());
            }
            if (holder.addressTextView != null) {
                holder.addressTextView.setText("Địa chỉ: " + order.getAddress_shipping());
            }
            if (holder.statusTextView != null) {
                holder.statusTextView.setText("Trạng thái: " + order.getStatus());
            }

            // Set up the CartAdapter for this order
            if (holder.cartRecyclerView != null) {
                CartAdapter cartAdapter = new CartAdapter(order.getCartList());
                holder.cartRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                holder.cartRecyclerView.setAdapter(cartAdapter);
            }

            // Set up expand/collapse functionality
            if (holder.expandButton != null && holder.expandableLayout != null) {
                holder.expandButton.setOnClickListener(v -> {
                    if (holder.expandableLayout.getVisibility() == View.VISIBLE) {
                        holder.expandableLayout.setVisibility(View.GONE);
                        holder.expandButton.setImageResource(android.R.drawable.arrow_down_float);
                    } else {
                        holder.expandableLayout.setVisibility(View.VISIBLE);
                        holder.expandButton.setImageResource(android.R.drawable.arrow_up_float);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView totalPrice, statusTextView, addressTextView;
        RecyclerView cartRecyclerView;
        ImageButton expandButton;
        LinearLayout expandableLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            totalPrice = itemView.findViewById(R.id.totalPriceTextHistory);
            statusTextView = itemView.findViewById(R.id.txttrangthai);
            addressTextView = itemView.findViewById(R.id.txtDiaChi);
            cartRecyclerView = itemView.findViewById(R.id.cartRecyclerView);
            expandButton = itemView.findViewById(R.id.expandButton);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
        private List<Cart> cartList;

        public CartAdapter(List<Cart> cartList) {
            this.cartList = cartList;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            Cart cart = cartList.get(position);
            if (cart != null) {
                if (holder.nameProduct != null) {
                    holder.nameProduct.setText(cart.getName_tree());
                }
                if (holder.quantityTextHistory != null) {
                    holder.quantityTextHistory.setText("Số lượng: " + cart.getQuantity());
                }
                if (holder.priceTextView != null) {
                    holder.priceTextView.setText("Giá: " + cart.getPrice_tree());
                }

                // Load image using Glide
                if (holder.imgProduct != null) {
                    Glide.with(context)
                            .load(cart.getImage_tree())
                            .placeholder(R.drawable.ic_cart_24)
                            .into(holder.imgProduct);
                }
            }
        }

        @Override
        public int getItemCount() {
            return cartList != null ? cartList.size() : 0;
        }

        class CartViewHolder extends RecyclerView.ViewHolder {
            ImageView imgProduct;
            TextView nameProduct, quantityTextHistory, priceTextView;

            CartViewHolder(@NonNull View itemView) {
                super(itemView);
                imgProduct = itemView.findViewById(R.id.productImageHistory);
                nameProduct = itemView.findViewById(R.id.productNameHistory);
                quantityTextHistory = itemView.findViewById(R.id.quantityTextHistory);
                priceTextView = itemView.findViewById(R.id.productPriceHistory);
            }
        }
    }
}