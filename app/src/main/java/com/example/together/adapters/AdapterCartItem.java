package com.example.together.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.together.activities.Client;
import com.example.together.R;
import com.example.together.models.ModelCartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;
    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    Double allTotalPrice;



    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.allTotalPrice = allTotalPrice;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_cartitems.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {
        //get data
        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String getpid = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String price = modelCartItem.getPrice();
        String cost = modelCartItem.getCost();
        String quantity = modelCartItem.getQuantity();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText("₪"+cost);
        holder.itemQuantityTv.setText("* "+quantity);
        holder.itemPriceEachTv.setText("₪"+price);

        //handle remove click listener, delete item from cart
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                // Reference to the "products" collection
                CollectionReference productsRef = db.collection("clients")
                        .document(userID)
                        .collection("cart");

                // Query to find the document where productId matches the given value
                Query query = productsRef.whereEqualTo("productId", getpid);

                query.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Delete each document
                                    document.getReference().delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error getting documents", e);
                            }
                        });
                Toast.makeText(context, "Remove from cart", Toast.LENGTH_SHORT).show();
                cartItems.remove(position);

                notifyItemChanged(position);
                notifyDataSetChanged();

//                double tx = Double.parseDouble((((Client)context).sTotalTv.getText().toString().trim().replace("₪", "")));
//                double totalPrice = tx - Double.parseDouble(cost.replace("₪",""));
                ((Client)context).allTotalPrice -= Double.parseDouble(cost);
                ((Client)context).sTotalTv.setText("₪" + ((Client)context).allTotalPrice);
//                ((Client)context).submitOrder();
            }

        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size(); //return number of records
    }

    //view holder class
    class HolderCartItem extends RecyclerView.ViewHolder{


        //ui views of row_cartitems.xml
        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv, itemRemoveTv;
        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);
        }
    }
}
