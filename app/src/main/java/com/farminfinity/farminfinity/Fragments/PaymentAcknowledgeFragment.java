package com.farminfinity.farminfinity.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.farminfinity.farminfinity.R;

public class PaymentAcknowledgeFragment extends Fragment {
    private TextView tvAckText;
    private Button btnBack;
    public PaymentAcknowledgeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_acknowledge, container, false);
        tvAckText = view.findViewById(R.id.tv_status_fragment_payment_ack);
        btnBack = view.findViewById(R.id.btn_back_fragment_payment_ack);
        tvAckText.setText(getArguments().getString("MSG"));
        tvAckText.setTextColor(Color.parseColor(getArguments().getString("COLOR")));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });
        return view;
    }
}