package com.example.mobilestore.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Product;

import com.example.mobilestore.Adapters.ProductUserAdapter;
import com.example.mobilestore.R;
import com.example.mobilestore.databinding.FragmentProductListBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProductListFragment extends Fragment {

    private FragmentProductListBinding binding;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    private ProductUserAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerProducts;
        setRecyclerView();
        adapter.startListening();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.searchAction);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Поиск по названию");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchRecyclerView(s);
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter.stopListening();
    }

    private void setRecyclerView() {
        Query query = firebaseFirestore.collection("Products")
                .orderBy("productName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter = new ProductUserAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void searchRecyclerView(String word) {
        Query query = firebaseFirestore.collection("Products")
                .orderBy("productName", Query.Direction.ASCENDING)
                .startAt(firstUpperCase(word))
                .endAt(firstUpperCase(word) + "\uf8ff");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter.updateOptions(options);
        recyclerView.setAdapter(adapter);
    }

    public String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}