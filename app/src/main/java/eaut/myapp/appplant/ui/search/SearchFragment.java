package eaut.myapp.appplant.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eaut.myapp.appplant.adapter.ProductAdapter;
import eaut.myapp.appplant.databinding.FragmentSearchBinding;
import eaut.myapp.appplant.model.Product;
import eaut.myapp.appplant.retrofit.ApiAppPlant;
import eaut.myapp.appplant.retrofit.RetrofitClient;
import eaut.myapp.appplant.ui.search.SearchViewModel;
import eaut.myapp.appplant.utils.Utils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment {
    private RecyclerView rcv_search;
    private EditText et_search;
    private FragmentSearchBinding binding;
    private ApiAppPlant apiAppPlan;
    private List<Product> arrsp;
    private ProductAdapter sanPhamAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Button btnSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnSearch = binding.btnSearch;
        rcv_search = binding.rcvSearch;
        et_search = binding.etSearch;

        // Thiết lập LayoutManager cho RecyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        rcv_search.setLayoutManager(layoutManager);
        rcv_search.setHasFixedSize(true);

        arrsp = new ArrayList<>();
        if (Utils.cartList == null) {
            Utils.cartList = new ArrayList<>();
        }

        // Xử lý sự kiện nhấn nút tìm kiếm
        btnSearch.setOnClickListener(v -> {
            String query = et_search.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                searchProducts(query);
            } else {
                Toast.makeText(getContext(), "Nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void searchProducts(String name_tree) {apiAppPlan = RetrofitClient.getInstance().create(ApiAppPlant.class);

        // Gọi API lấy danh sách sản phẩm và quản lý với RxJava
        compositeDisposable.add(apiAppPlan.searchTree(name_tree)
                .subscribeOn(Schedulers.io())               // Chạy trong thread nền
                .observeOn(AndroidSchedulers.mainThread())   // Hiển thị trên main thread
                .subscribe(
                        sanphamModel -> {
                            if (sanphamModel.isSuccess()) {
                                arrsp = sanphamModel.getResult();
                                sanPhamAdapter = new ProductAdapter(arrsp, getContext());
                                rcv_search.setAdapter(sanPhamAdapter);

                            }
                        },
                        throwable -> {
                            Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        }
                ));


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        compositeDisposable.clear();
    }
}