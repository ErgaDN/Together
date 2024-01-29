package com.example.together;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class DistributionCenter extends Fragment {

    MaterialCardView selectCenter;
    TextView tvAAddresses;
    boolean [] selectedAddresses;
    ArrayList<Integer> addressesList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_distribution_center, container, false);

        selectCenter = view.findViewById(R.id.select_center);
        tvAAddresses = view.findViewById(R.id.tvAddresses);
        selectedAddresses = new boolean[Constants.address.length];

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
}