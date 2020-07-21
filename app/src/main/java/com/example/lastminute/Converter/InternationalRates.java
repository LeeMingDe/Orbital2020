package com.example.lastminute.Converter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lastminute.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class InternationalRates extends Fragment {

    private TextView sgdOutput, usdOutput, eurOutput;
    private EditText currencyInput;
    private Spinner currencySpinner;
    private int spinnerIndex;
    private double amountToConvert;
    private String[] currencyList = new String[]{"SGD", "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON", "RUB", "SEK", "THB", "TRY", "USD", "ZAR"};
    private String[] result = new String[33];
    private TextView[] outputs = new TextView[33];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_international_rates, container, false);

        outputs[0] = v.findViewById(R.id.sgdOutput);
        outputs[1] = v.findViewById(R.id.audOutput);
        outputs[2] = v.findViewById(R.id.bgnOutput);
        outputs[3] = v.findViewById(R.id.brlOutput);
        outputs[4] = v.findViewById(R.id.cadOutput);
        outputs[5] = v.findViewById(R.id.chfOutput);
        outputs[6] = v.findViewById(R.id.cnyOutput);
        outputs[7] = v.findViewById(R.id.czkOutput);
        outputs[8] = v.findViewById(R.id.dkkOutput);
        outputs[9] = v.findViewById(R.id.eurOutput);
        outputs[10] = v.findViewById(R.id.gbpOutput);
        outputs[11] = v.findViewById(R.id.hkdOutput);
        outputs[12] = v.findViewById(R.id.hrkOutput);
        outputs[13] = v.findViewById(R.id.hufOutput);
        outputs[14] = v.findViewById(R.id.idrOutput);
        outputs[15] = v.findViewById(R.id.ilsOutput);
        outputs[16] = v.findViewById(R.id.inrOutput);
        outputs[17] = v.findViewById(R.id.iskOutput);
        outputs[18] = v.findViewById(R.id.jpyOutput);
        outputs[19] = v.findViewById(R.id.krwOutput);
        outputs[20] = v.findViewById(R.id.mxnOutput);
        outputs[21] = v.findViewById(R.id.myrOutput);
        outputs[22] = v.findViewById(R.id.nokOutput);
        outputs[23] = v.findViewById(R.id.nzdOutput);
        outputs[24] = v.findViewById(R.id.phpOutput);
        outputs[25] = v.findViewById(R.id.plnOutput);
        outputs[26] = v.findViewById(R.id.ronOutput);
        outputs[27] = v.findViewById(R.id.rubOutput);
        outputs[28] = v.findViewById(R.id.sekOutput);
        outputs[29] = v.findViewById(R.id.thbOutput);
        outputs[30] = v.findViewById(R.id.tryOutput);
        outputs[31] = v.findViewById(R.id.usdOutput);
        outputs[32] = v.findViewById(R.id.zarOutput);

        currencyInput = v.findViewById(R.id.currencyInput);
        currencySpinner = v.findViewById(R.id.currencySpinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.currencyInput, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(arrayAdapter);

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIndex = position;
                new Calculator().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        textChanged();
        return v;
    }

    @SuppressLint("StaticFieldLeak")
    public class Calculator extends AsyncTask<String, String, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String[] strings) {
            int index = spinnerIndex;
            for (int i = 0; i < 33; i++) {
                if (i == index) {
                    outputs[i].setText("" + Math.round(amountToConvert * 100.0) / 100.0);
                } else {
                    double value = Double.parseDouble(strings[i]) * amountToConvert;
                    outputs[i].setText("" + Math.round(value * 100.0) / 100.0);
                }
            }
            textChanged();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            int index = spinnerIndex;
            String base = currencyList[index];
            String[] urlList = new String[33];
            for (int i = 0; i < 33; i++) {
                if (i != index) {
                    try {
                        urlList[i] = getJSON("https://api.ratesapi.io/api/latest?base=" + base + "&symbols=" + currencyList[i]);
                        JSONObject currencyToObj;
                        currencyToObj = new JSONObject(urlList[i]);
                        JSONObject rates = currencyToObj.getJSONObject("rates");
                        result[i] = rates.getString(currencyList[i]);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        public String getJSON(String uri) throws ClientProtocolException, IOException {
            StringBuilder stringBuilder = new StringBuilder();
            URL url = new URL(uri);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream input = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String temp;

                while ((temp = reader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
            } finally {
                // regardless success or failure, we will disconnect
                urlConnection.disconnect();
            }
            return stringBuilder.toString();
        }

    }

    private void textChanged() {
        currencyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String amount = currencyInput.getText().toString();
                try {
                    amountToConvert = Double.parseDouble(amount);
                } catch (NumberFormatException e) {
                    amountToConvert = 0.0;
                }
                int index = spinnerIndex;
                for (int i = 0; i < 33; i++) {
                    if (i == index) {
                        outputs[i].setText("" + Math.round(amountToConvert * 100.0) / 100.0);
                    } else {
                        double value = Double.parseDouble(result[i]) * amountToConvert;
                        outputs[i].setText("" + Math.round(value * 100.0) / 100.0);
                    }
                }
            }
        });
    }
}
