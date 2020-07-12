package com.example.lastminute;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PersonalRates extends Fragment {

    EditText rateInput, fromInput;
    TextView toOutput;
    private double amountToConvert;
    private double rate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_personal_rates, container, false);

        rateInput = (EditText) v.findViewById(R.id.rateInput);
        fromInput = (EditText) v.findViewById(R.id.fromInput);
        toOutput = (TextView) v.findViewById(R.id.toOutput);

        textChanged();
        return v;
    }

    private void calculateRate(double amount, double rate) {
        double result = amount * rate;
        toOutput.setText("" + Math.round(result * 100.0) / 100.0);
    }

//    private void textChanged() {
//        fromInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                try {
//                    String amount = fromInput.getText().toString();
//                    amountToConvert = Double.parseDouble(amount);
//                    calculateRate(amountToConvert);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    private TextWatcher generalTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String amount = fromInput.getText().toString();
                amountToConvert = Double.parseDouble(amount);
                String r = rateInput.getText().toString();
                rate = Double.parseDouble(r);
                calculateRate(amountToConvert, rate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void textChanged() {
        rateInput.addTextChangedListener(generalTextWatcher);
        fromInput.addTextChangedListener(generalTextWatcher);
    }
}
