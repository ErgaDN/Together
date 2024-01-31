package com.example.together;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class EditingProducts extends Fragment {
    private ImageButton btn_addproduct, btn_updateproduct;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editing_products, container, false);
        btn_addproduct = view.findViewById(R.id.btn_addproduct);
        btn_updateproduct = view.findViewById(R.id.btn_updateproduct);

        btn_addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open add product activity
                startActivity(new Intent(requireContext(), AddProduct.class));
            }
        });

        btn_updateproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open update product activity
                startActivity(new Intent(requireContext(), UpdateProduct.class));
            }
        });

        return view;

    }
}