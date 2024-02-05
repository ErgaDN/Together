package com.example.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;
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

        loadAllOrders();

        return view;
    }

    private void loadAllOrders() {
        //init array list
        orderShopArrayList =  new ArrayList<>();

        //load orders
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("seller").document(userID);
        Log.d("Debug", "doc ref: " + docRef);

        docRef.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                orderShopArrayList.clear();
                if (task.isSuccessful()) {
                    Log.d("Debug", "is succses " );
                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
                        Log.d("Debug", "in the for ");
                        // Use toObject to convert the document snapshot to a ModelProduct object
                        ModelOrderShop modelOrderShop = productDocument.toObject(ModelOrderShop.class);
                        orderShopArrayList.add(modelOrderShop);

                    }
//                    //setup adapter
                    adapterOrderShop = new AdapterOrderShop(getContext(), orderShopArrayList);
                    Log.d("Debug", "after adapter new, contex:  "+ getContext());
//                    //set adapter
                    ordersRv.setAdapter(adapterOrderShop);
                    Log.d("Debug", "after ordersRv");

                }
            }
        });

    }

}