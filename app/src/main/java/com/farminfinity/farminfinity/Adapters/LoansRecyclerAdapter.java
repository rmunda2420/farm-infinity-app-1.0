package com.farminfinity.farminfinity.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.farminfinity.farminfinity.Fragments.EditLevelOneFragment;
import com.farminfinity.farminfinity.Fragments.RecyclerViewEmiFragment;
import com.farminfinity.farminfinity.Interface.ILoadMore;
import com.farminfinity.farminfinity.Model.FarmerModel;
import com.farminfinity.farminfinity.Model.LoanModel;
import com.farminfinity.farminfinity.R;

import java.util.ArrayList;

public class LoansRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<LoanModel> processedData;
    private ArrayList<LoanModel> processedDataCopy;
    private ILoadMore loadMore;
    private RecyclerView mRecyclerView;

    private boolean isLoading = false;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    public LoansRecyclerAdapter(Context context, RecyclerView recyclerView, final ArrayList<LoanModel> processedData) {
        this.context = context;
        this.mRecyclerView = recyclerView;
        this.processedData = processedData;
        processedDataCopy = new ArrayList<>(processedData);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_loans, parent, false);
            return new LoansRecyclerAdapter.ItemViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_loading, parent, false);
            return new LoansRecyclerAdapter.LoadingViewHolder(view);
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

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoansRecyclerAdapter.ItemViewHolder) {
            LoanModel loanModel = (LoanModel) processedData.get(position);
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.loanIds.setText(loanModel.getLoan_id());
        } else {
            LoansRecyclerAdapter.LoadingViewHolder loadingViewHolder = (LoansRecyclerAdapter.LoadingViewHolder) holder;
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
            ArrayList<LoanModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(processedDataCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (LoanModel item : processedDataCopy) {
                    if (item.getLoan_id().toLowerCase().contains(filterPattern)) {
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

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView loanIds;

        private ItemViewHolder(View itemView) {
            super(itemView);

            loanIds = (TextView) itemView.findViewById(R.id.tv_ids_list_item_loans);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            LoanModel loanModel = (LoanModel) processedData.get(getAdapterPosition());
            String id = loanModel.getLoan_id();
            // Open form
            RecyclerViewEmiFragment nextFrag = new RecyclerViewEmiFragment();
            Bundle args = new Bundle();
            args.putString("LOAN_ID", id);
            nextFrag.setArguments(args);
            ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, nextFrag, "Adapter")
                    .addToBackStack(null)
                    .commit();
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
