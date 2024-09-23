package com.farminfinity.farminfinity.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farminfinity.farminfinity.R;


public class EmptyRecyclerViewAdapter extends RecyclerView.Adapter<EmptyRecyclerViewAdapter.ViewHolder> {
    private String mMessage;

    public EmptyRecyclerViewAdapter(){

    }

    public EmptyRecyclerViewAdapter(String message){
        mMessage = message;
    }
    @NonNull
    @Override
    public EmptyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_empty, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if(mMessage != null){
            viewHolder.mMessageView.setText(mMessage);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyRecyclerViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mMessageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMessageView = (TextView) view.findViewById(R.id.empty_item_message);
        }
    }
}
