package com.example.together.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.together.Constants;
import com.example.together.adapters.AdapterOrderSeller;
import com.example.together.R;
import com.example.together.models.ModelOrderSeller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderSummary extends Fragment {
//    Button button;
    private TextView filteredOrdersTv;
    private ImageButton filteredOrdersBtn;
    private RecyclerView ordersRv;
    private ArrayList<ModelOrderSeller> orderSellerArrayList;
    private AdapterOrderSeller adapterOrderSeller;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_summary, container, false);
        filteredOrdersTv = view.findViewById(R.id.filteredOrdersTv);
        filteredOrdersBtn = view.findViewById(R.id.filteredOrdersBtn);
        ordersRv = view.findViewById(R.id.ordersRv);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        filteredOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //options to display in dialog
                String[] options = {"הכל", "בתהליך", "הושלמה", "בוטלה"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("בחר סטטוס:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //handle item clicks
                                if (which == 0) {
                                    //All clicked
                                    filteredOrdersTv.setText("כל ההזמנות");
                                    adapterOrderSeller.getFilter().filter(""); //show all orders
                                } else {
                                    String optionClicked = options[which];
                                    filteredOrdersTv.setText("סטטוס הזמנות: " + optionClicked);
                                    adapterOrderSeller.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });

        loadAllOrders();

        return view;
    }

    private void loadAllOrders() {
        //init array list
        orderSellerArrayList =  new ArrayList<>();

        //load orders
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("seller").document(userID);
        Log.d("Debug", "doc ref: " + docRef);

        docRef.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                orderSellerArrayList.clear();
                if (task.isSuccessful()) {
                    Log.d("Debug", "is succses " );
                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
                        Log.d("Debug", "in the for ");
                        // Use toObject to convert the document snapshot to a ModelProduct object
                        ModelOrderSeller modelOrderSeller = productDocument.toObject(ModelOrderSeller.class);
                        orderSellerArrayList.add(modelOrderSeller);

                    }
//                    //setup adapter
                    adapterOrderSeller = new AdapterOrderSeller(getContext(), orderSellerArrayList);
                    Log.d("Debug", "after adapter new, contex:  "+ getContext());
//                    //set adapter
                    ordersRv.setAdapter(adapterOrderSeller);
                    Log.d("Debug", "after ordersRv");

                }
            }
        });

    }

}