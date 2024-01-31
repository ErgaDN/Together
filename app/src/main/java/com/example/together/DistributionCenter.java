package com.example.together;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.Inflater;

import javax.annotation.Nullable;

public class DistributionCenter extends Fragment {

  private EditText searchProductsEt;
  private ImageButton filterProductBtn;
  private TextView filteredProductsTv;
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_distribution_center, container, false);
    searchProductsEt = view.findViewById(R.id.searchProductsEt);
    filterProductBtn = view.findViewById(R.id.filterProductBtn);
    filteredProductsTv = view.findViewById(R.id.filteredProductsTv);

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
        startActivity(new Intent(requireContext(), ChooseUpdateProduct.class));
      }
    });

    return view;

  }

}
