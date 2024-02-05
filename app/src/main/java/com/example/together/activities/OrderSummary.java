package com.example.together.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.together.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OrderSummary extends Fragment {
//    Button button;
    private TextView filteredOrdersTv;
    private ImageButton filteredOrdersBtn;
    private RecyclerView ordersRv;
    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_summary, container, false);
        filteredOrdersTv = view.findViewById(R.id.filteredOrdersTv);
        filteredOrdersBtn = view.findViewById(R.id.filteredOrdersBtn);
        ordersRv = view.findViewById(R.id.ordersRv);

        loadAllOrders();

        return view;
    }

    private void loadAllOrders() {
        //init array list
        orderShopArrayList =  new ArrayList<>();

        //load orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clients");
        //TODO: 27:36
    }
}