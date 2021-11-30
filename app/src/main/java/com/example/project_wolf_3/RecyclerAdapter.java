package com.example.project_wolf_3;

import android.os.health.PackageHealthStats;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.InvModel;
import com.example.project_wolf_3.model.Posts;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    private List<Posts> items;
    private List<Posts> originalItems;
    private RecyclerItemClick itemClick;
   // private RecyclerAdapter.onItemClickListener listener;

    public RecyclerAdapter(List<Posts> items, RecyclerItemClick itemClick) {
        this.items = items;
        this.itemClick = itemClick;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(items);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final Posts item = items.get(position);
        holder.roi_vol.setText(String.format("$%s", String.format("%,.2f", item.getGananciap())));
        holder.final_amount.setText(String.format("$%s", String.format("%,.2f", item.getAmount())));
        Date date_raw = item.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        holder.date.setText(String.format("Fecha: %s", dateFormat.format(date_raw)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.itemClick(item);
            }
        });

    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new RecyclerHolder(view);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }




    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private final TextView roi_vol;
        private final TextView final_amount;
        private final TextView date;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            roi_vol = itemView.findViewById(R.id.roi_volume);
            final_amount = itemView.findViewById(R.id.price_current);
            date = itemView.findViewById(R.id.date_zero);




        }
    }
    public interface RecyclerItemClick {
        void itemClick(Posts item);
    }

}
