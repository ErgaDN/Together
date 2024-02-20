package com.example.together.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.together.R;
import com.example.together.adapters.AdapterOrderClient;
import com.example.together.models.ModelOrderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryOrdersClient extends AppCompatActivity {
    private RecyclerView orderRv;
    private ArrayList<ModelOrderClient> orderList;

    private AdapterOrderClient adapterOrderClient;

    FirebaseFirestore db;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders_client);

        orderRv = findViewById(R.id.orderRv);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadOrders();

    }

    //go over the orders collection and get the product collection
    private void loadOrders() {
        // Initialize order list
        orderList = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        CollectionReference ordersRef = db.collection("clients").document(userID).collection("orders");

        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("Debug", "after 'onComplete' ");
                if (task.isSuccessful()) {
                    Log.d("Debug", "after 'task.isSuccessful()' ");
                    for (QueryDocumentSnapshot orderDocument : task.getResult()) {
                        Log.d("Debug", " in the for! ");
                        String orderId = orderDocument.getId();

                        CollectionReference productRef = ordersRef.document(orderId).collection("products");
                        ModelOrderClient modelOrderClient = orderDocument.toObject(ModelOrderClient.class);
                                        orderList.add(modelOrderClient);

                    }
                    Log.d("Debug", " after the for ");
                    adapterOrderClient = new AdapterOrderClient(HistoryOrdersClient.this, orderList);
                    Log.d("Debug", " after 'adapterOrderClient = new AdapterOrderClient(HistoryOrdersClient.this, orderList);' ");
                    orderRv.setAdapter(adapterOrderClient);
                    Log.d("Debug", " after 'orderRv.setAdapter(adapterOrderClient) ");

                }
            }
        });

    }

}