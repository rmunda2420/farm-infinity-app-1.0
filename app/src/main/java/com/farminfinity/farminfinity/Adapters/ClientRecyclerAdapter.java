package com.farminfinity.farminfinity.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.PopupMenu;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.farminfinity.farminfinity.Fragments.EditLevelOneFragment;
import com.farminfinity.farminfinity.Fragments.RecyclerViewLoanIntFragment;
import com.farminfinity.farminfinity.Interface.ILoadMore;
import com.farminfinity.farminfinity.Model.FarmerModel;
import com.farminfinity.farminfinity.R;

import java.util.ArrayList;

public class ClientRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<FarmerModel> processedData;
    private ArrayList<FarmerModel> processedDataCopy;
    private ILoadMore loadMore;
    private RecyclerView mRecyclerView;

    private boolean isLoading = false;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    public ClientRecyclerAdapter(Context context, RecyclerView recyclerView, final ArrayList<FarmerModel> processedData) {
        this.context = context;
        this.mRecyclerView = recyclerView;
        this.processedData = processedData;
        processedDataCopy = new ArrayList<>(processedData);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && processedData.size() >= 50) {
                    if (loadMore != null) {
                        loadMore.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_client, parent, false);
            return new ClientRecyclerAdapter.ItemViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_loading, parent, false);
            return new ClientRecyclerAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (processedData.get(position) == null) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void setLoadMore(ILoadMore loadMore) {

        this.loadMore = loadMore;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            FarmerModel farmerModel = (FarmerModel) processedData.get(position);
            String fName = "";
            String mName = "";
            String lName = "";
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
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
            switch (farmerModel.getStatus()) {
                case 1:
                    viewHolder.status.setText("Lead");
                    break;
                case 2:
                    viewHolder.status.setText("submitted");
                    break;
                case 3:
                    viewHolder.status.setText("Under Process");
                    break;
                case 4:
                    viewHolder.status.setText("Approved");
                    break;
                case 5:
                    viewHolder.status.setText("Rejected");
                    break;
                case 6:
                    viewHolder.status.setText("Disbursed");
                    break;
                default:
                    viewHolder.status.setText("");
                    break;
            }
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return processedData.size();
    }

    @Override
    public Filter getFilter() {
        return recyclerViewFilter;
    }

    private Filter recyclerViewFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FarmerModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(processedDataCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (FarmerModel item : processedDataCopy) {
                    if (item.getF_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            processedData.clear();
            processedData.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, phone, address, loanAmtSought, status;
        ImageView thumbnail, optionMenu;
        ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        TextDrawable mDrawableBuilder;


        private ItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tv_client_name_list_item_client);
            phone = (TextView) itemView.findViewById(R.id.tv_client_phone_list_item_client);
            address = (TextView) itemView.findViewById(R.id.tv_client_address_list_item_client);
            loanAmtSought = (TextView) itemView.findViewById(R.id.tv_client_loan_amt_sought_list_item_client);
            status = (TextView) itemView.findViewById(R.id.tv_client_status_list_item_client);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail_list_item_client);
            optionMenu = (ImageView) itemView.findViewById(R.id.iv_option_menu_list_item_client);

            optionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, optionMenu);
                    popupMenu.inflate(R.menu.popup_menu_recycler_farmer);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_loan:
                                    FarmerModel farmerModel = (FarmerModel) processedData.get(getAdapterPosition());
                                    String id = farmerModel.getId();
                                    // Open form
                                    RecyclerViewLoanIntFragment nextFrag = new RecyclerViewLoanIntFragment();
                                    Bundle args = new Bundle();
                                    args.putString("FID", id);
                                    nextFrag.setArguments(args);
                                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, nextFrag, "Adapter")
                                            .addToBackStack(null)
                                            .commit();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FarmerModel farmerModel = (FarmerModel) processedData.get(getAdapterPosition());
            String id = farmerModel.getId();
            // Open form
            EditLevelOneFragment nextFrag = new EditLevelOneFragment();
            Bundle args = new Bundle();
            args.putString("FID", id);
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

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        private LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                Drawable drawableProgress = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
                DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(drawableProgress));

            } else {
                progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
