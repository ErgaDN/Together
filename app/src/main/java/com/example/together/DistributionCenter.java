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

public class DistributionCenter extends Fragment {

    MaterialCardView selectCenter;
    TextView tvAAddresses;
    boolean [] selectedAddresses;
    ArrayList<Integer> addressesList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore mStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_distribution_center, container, false);

        selectCenter = view.findViewById(R.id.select_center);
        tvAAddresses = view.findViewById(R.id.tvAddresses);
        selectedAddresses = new boolean[Constants.address.length];
        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();


        selectCenter.setOnClickListener(v ->{
            showAddressesDialog();
        });


        return view;
    }

    private void showAddressesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle("Select Addresses");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(Constants.address, selectedAddresses, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked){
                    addressesList.add(which);
                }else {
                    addressesList.remove(which);
                }
            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //create string builder
                StringBuilder stringBuilder = new StringBuilder();
                for (int i=0; i<addressesList.size(); i++){
                    stringBuilder.append(Constants.address[addressesList.get(i)]);
                    // add to DB
                    addAddresses(Constants.address[addressesList.get(i)]);
                    //check condition
                    if (i != addressesList.size()-1){
//                        when i value not equal to addressesList size
                        stringBuilder.append(",");
                    }
                    //setting selected address to textviwe
                    tvAAddresses.setText(stringBuilder.toString());
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //clearing all selecting addresses on clear all click
                for (int i=0; i< selectedAddresses.length; i++){
                    selectedAddresses[i] = false;

                    addressesList.clear();
                    tvAAddresses.setText("");
                }
            }
        });
        builder.show();
    }

    private void addAddresses(String addresses) {
        //add data to db
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> addressesData = new HashMap<>();
        addressesData.put("productId", "" + timestamp);
        addressesData.put("addresses", "" + addresses);
        addressesData.put("timestamp", "" + timestamp);
        addressesData.put("uid", "" + firebaseAuth.getUid());
        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        CollectionReference productCollectionRef = mStore.collection("agriculturals").document(userID).collection("Distribution Center");

        // Add the sample addresses document to the "Distribution Center" collection
        productCollectionRef.add(addressesData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Product added to the 'products' collection under user: " + userID);

                Intent intent = new Intent(requireContext(), Agricultural.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        }



    }
