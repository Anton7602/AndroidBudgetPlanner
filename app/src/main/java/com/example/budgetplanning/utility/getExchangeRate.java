package com.example.budgetplanning.utility;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getExchangeRate extends AsyncTask<Void, Void, Void> {
    private static final String currentExchangeRateURL="https://www.cbr-xml-daily.ru/daily_json.js";
    private static final String archiveExchangeRateURL="https://www.cbr-xml-daily.ru/archive/YYYY/MM/DD/daily_json.js";
    private boolean exchangeRateReceived = false;
    public String output = "";
    private int year;
    private int month;
    private int day;
    private double USD;
    private double EUR;

    public double getUSD() {
        return USD;
    }

    public double getEUR() {
        return EUR;
    }

    public boolean isExchangeRateReceived() {
        return exchangeRateReceived;
    }

    public getExchangeRate() {

    }

    public getExchangeRate(int setYear, int setMonth, int setDay) {
        this.year=setYear;
        this.month=setMonth;
        this.day=setDay;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(currentExchangeRateURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line =reader.readLine();
            while (line!=null && !line.contains("INR")) {
                line = reader.readLine();
                if (line.contains("USD")) {
                    while (!line.contains("Value")) {
                        line = reader.readLine();
                    }
                    USD = Double.parseDouble(line.substring(line.indexOf(":")+1,line.indexOf(",")));
                }
                if (line.contains("EUR")) {
                    while (!line.contains("Value")) {
                        line = reader.readLine();
                    }
                    EUR = Double.parseDouble(line.substring(line.indexOf(":")+1,line.indexOf(",")));
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exchangeRateReceived = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
