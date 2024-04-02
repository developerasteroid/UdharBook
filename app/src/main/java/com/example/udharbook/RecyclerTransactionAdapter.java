package com.example.udharbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerTransactionAdapter extends  RecyclerView.Adapter<RecyclerTransactionAdapter.ViewHolder> {

    Context context;
    ArrayList<TransactionModel> arrayTransactions;
    RecyclerTransactionAdapter(Context context, ArrayList<TransactionModel> arrayTransactions){
        this.context = context;
        this.arrayTransactions = arrayTransactions;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_recycler_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtDate.setText(arrayTransactions.get(position).date);
        holder.txtPending.setText("Pending\n"+arrayTransactions.get(position).pending+"/-");
        if(arrayTransactions.get(position).method.equals("add")){
            holder.txtAmount.setText("+"+arrayTransactions.get(position).amount);
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transaction_card_add));
        } else {
            holder.txtAmount.setText("-"+arrayTransactions.get(position).amount);
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transaction_card_received));
        }

    }

    @Override
    public int getItemCount() {
        return arrayTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtPending, txtAmount;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.textViewDate);
            txtPending = itemView.findViewById(R.id.textViewPending);
            txtAmount = itemView.findViewById(R.id.textViewAmount);
            layout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
