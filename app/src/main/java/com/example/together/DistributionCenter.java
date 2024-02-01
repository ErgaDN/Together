package com.example.together;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.Inflater;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DistributionCenter extends Fragment {

  private EditText searchProductsEt;
  private ImageButton filterProductBtn;
  private TextView filteredProductsTv;
  private RecyclerView productsRv;
  private ArrayList<ModelProduct> productList;
  private AdapterProductSeller adapterProductSeller;
  private FirebaseAuth firebaseAuth;
//  ///TODO:-----50:00-----
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_distribution_center, container, false);
    searchProductsEt = view.findViewById(R.id.searchProductsEt);
    filterProductBtn = view.findViewById(R.id.filterProductBtn);
    filteredProductsTv = view.findViewById(R.id.filteredProductsTv);
    productsRv = view.findViewById(R.id.productsRv);

    loadAllProducts();

    //search
    searchProductsEt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
          adapterProductSeller.getFilter().filter(s);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    filterProductBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Category:")
                .setItems(Constants.productCategories_1, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    //get selected item
                    String selected = Constants.productCategories_1[which];
                    filteredProductsTv.setText(selected);
                    if (selected.equals("הכל")) {
                      /////TODO: 55:17------------
                      loadAllProducts();
                    }
                    else {
//                      loadFilteredProducts(selected);
                    }
                  }
                })
                .show();
      }
    });

    return view;

  }

//  private void loadFilteredProducts(String selected) {
//    productList = new ArrayList<>();
//
//    //get all product
//    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
//    //TODO: change the "User"
//    reference.child(firebaseAuth.getUid()).child("products")
//            .addValueEventListener(new ValueEventListener() {
//              @Override
//              public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//                //before getting rest list
//                productList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                  String productCategory = "" + ds.child("productCategory").getValue();
//
//                  //if selected category matches product category that add in list
//                  if (selected.equals(productCategory)) {
//                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
//                    productList.add(modelProduct);
//                  }
//
//                }
//                //setup adapter
//                adapterProductSeller = new AdapterProductSeller(getContext(), productList);
//                //set adapter
//                productsRv.setAdapter(adapterProductSeller);
//              }
//
//              @Override
//              public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//              }
//            });
//  }


  private void loadAllProducts() {
    productList = new ArrayList<>();

    //get all product
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("seller");
    reference.child(firebaseAuth.getUid()).child("products")
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
                //before getting rest list
                productList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                  ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                  productList.add(modelProduct);
                }
                //setup adapter
                adapterProductSeller = new AdapterProductSeller(getContext(), productList);
                //set adapter
                productsRv.setAdapter(adapterProductSeller);
              }

              @Override
              public void onCancelled(@Nonnull DatabaseError databaseError) {

              }
            });
  }

}
