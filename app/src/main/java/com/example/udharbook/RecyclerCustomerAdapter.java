package com.example.udharbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerCustomerAdapter extends RecyclerView.Adapter<RecyclerCustomerAdapter.ViewHolder> {

    Context context;
    ArrayList<CustomerModel> arrayCustomers;

    public void callBack(int userID){};

    RecyclerCustomerAdapter(Context context, ArrayList<CustomerModel> arrayCustomers){
        this.context = context;
        this.arrayCustomers = arrayCustomers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_recycler_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(arrayCustomers.get(position).name);
        holder.txtMobile.setText(arrayCustomers.get(position).mobileNo);
        holder.txtPending.setText(arrayCustomers.get(position).pending+"/-");
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack(arrayCustomers.get(position).userID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayCustomers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtMobile, txtPending;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textView14);
            txtMobile = itemView.findViewById(R.id.textView15);
            txtPending = itemView.findViewById(R.id.textView16);
            layout = itemView.findViewById(R.id.linearLayoutCustomer);
        }
    }
}
