package com.farminfinity.farminfinity.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.farminfinity.farminfinity.Fragments.EditLevelOneFragment;
import com.farminfinity.farminfinity.Model.FarmerModel;
import com.farminfinity.farminfinity.R;

import java.util.ArrayList;

public class SearchByNameRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<FarmerModel> processedData;
    private RecyclerView mRecyclerView;

    public SearchByNameRecyclerAdapter(Context context, RecyclerView recyclerView, ArrayList<FarmerModel> processedData) {
        this.context = context;
        this.processedData = processedData;
        this.mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_client, parent, false);
        return new SearchByNameRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            FarmerModel farmerModel = (FarmerModel) processedData.get(position);
            String fName = "";
            String mName = "";
            String lName = "";
            final SearchByNameRecyclerAdapter.ItemViewHolder viewHolder = (SearchByNameRecyclerAdapter.ItemViewHolder) holder;
            viewHolder.setFarmerThumbnail(farmerModel.getF_name());

            if (farmerModel.getF_name() != null)
                fName = farmerModel.getF_name();
            if (farmerModel.getM_name() != null)
                mName = farmerModel.getM_name();
            if (farmerModel.getL_name() != null)
                lName = farmerModel.getL_name();
            String fullName = fName.concat(" ").concat(mName).concat(" ").concat(lName);

            viewHolder.name.setText(fullName);
            viewHolder.phone.setText(farmerModel.getPh_no());
            viewHolder.address.setText(farmerModel.getVillage());
            viewHolder.loanAmtSought.setText(farmerModel.getLoan_amt_sought());
        }
    }

    @Override
    public int getItemCount() {
        return processedData.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name, phone, address, loanAmtSought;
        private ImageView thumbnail;
        private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        private TextDrawable mDrawableBuilder;

        private ItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tv_client_name_list_item_client);
            phone = (TextView) itemView.findViewById(R.id.tv_client_phone_list_item_client);
            address = (TextView) itemView.findViewById(R.id.tv_client_address_list_item_client);
            loanAmtSought = (TextView) itemView.findViewById(R.id.tv_client_loan_amt_sought_list_item_client);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail_list_item_client);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = mRecyclerView.indexOfChild(v);
            FarmerModel farmerModel = (FarmerModel) processedData.get(position);
            String id = farmerModel.getId();
            // Open form
            //AppCompatActivity activity = (AppCompatActivity) v.getContext();
            EditLevelOneFragment nextFrag = new EditLevelOneFragment();
            Bundle args = new Bundle();
            args.putString("FID", id);
            args.putString("MODE", "edit");
            nextFrag.setArguments(args);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, nextFrag, "Adapter")
                    .addToBackStack(null)
                    .commit();
        }

        private void setFarmerThumbnail(String title) {
            String letter = "A";

            if (title != null && !title.isEmpty()) {
                letter = title.substring(0, 1);
            }

            int color = mColorGenerator.getRandomColor();

            mDrawableBuilder = TextDrawable.builder()
                    .buildRound(letter, color);
            thumbnail.setImageDrawable(mDrawableBuilder);
        }
    }
}
