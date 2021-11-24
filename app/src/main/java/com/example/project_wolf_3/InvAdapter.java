package com.example.project_wolf_3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.Posts;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class InvAdapter extends FirestoreRecyclerAdapter<Posts, InvAdapter.TransactionViewHolder> {

    private onItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public InvAdapter(@NonNull FirestoreRecyclerOptions<Posts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull Posts model) {
        holder.roi_vol.setText(String.format("$%s", String.format("%,.2f", model.getPrice())));
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new TransactionViewHolder(view);
    }
    class TransactionViewHolder extends RecyclerView.ViewHolder {


        private final TextView roi_vol;
        private final TextView final_amount;
        private final TextView date;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            roi_vol = itemView.findViewById(R.id.roi_volume);
            final_amount = itemView.findViewById(R.id.price_current);
            date = itemView.findViewById(R.id.date_zero);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });

        }

    }

    public interface onItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(InvAdapter.onItemClickListener listener) {
        this.listener = listener;
    }


}
