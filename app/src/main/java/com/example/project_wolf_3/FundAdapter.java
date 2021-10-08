package com.example.project_wolf_3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.FundModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FundAdapter extends FirestoreRecyclerAdapter<FundModel, FundAdapter.TransactionViewHolder> {

    private onItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FundAdapter(@NonNull FirestoreRecyclerOptions<FundModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull FundModel model) {
        //holder.roi_vol.setText(String.format("$%s", String.format("%,.2f", model.getRoi_vol())));
        holder.initial_amount.setText(String.format("$%s", String.format("%,.2f", model.getInitial_amount())));
        holder.final_amount.setText(String.format("$%s", String.format("%,.2f", model.getFinal_amount())));
        Double value = 54013.3600 + model.getRoi_vol();

        holder.roi_vol.setText(String.valueOf("$ " +value));
       holder.roi_per.setText(String.format("%s%%", String.format("%,.2f", model.getRoi_per()*100)));
        if(model.getDate() != null) {
            Date date_raw = model.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
            holder.date.setText(String.format("Fecha: %s", dateFormat.format(date_raw)));
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new TransactionViewHolder(view);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {


        private final TextView roi_vol;
        private final TextView roi_per;
        private final TextView initial_amount;
        private final TextView final_amount;
        private final TextView date;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            roi_vol = itemView.findViewById(R.id.roi_volume);
            roi_per = itemView.findViewById(R.id.roi_percentage);
            initial_amount = itemView.findViewById(R.id.price_zero);
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

    public void setOnItemClickListener(FundAdapter.onItemClickListener listener) {
        this.listener = listener;
    }

}
