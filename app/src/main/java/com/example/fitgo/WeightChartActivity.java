package com.example.fitgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class WeightChartActivity extends AppCompatActivity {

    private BarChart weightChart;
    private final Map<String, Float> weightData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_chart);

        weightChart = findViewById(R.id.weightChart);

        Button btnBack = findViewById(R.id.btnBackToHealthWeight);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(WeightChartActivity.this, HealthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadWeightData();
    }

    private void loadWeightData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ArrayList<String> last7Days = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -i);
            last7Days.add(sdf.format(cal.getTime()));
        }

        for (String date : last7Days) {
            db.collection("users").document(uid)
                    .collection("weight_logs").document(date)
                    .get()
                    .addOnSuccessListener(document -> {
                        float value = 0f;
                        if (document.exists()) {
                            try {
                                value = Float.parseFloat(document.getString("value"));
                            } catch (Exception ignored) {}
                        }
                        weightData.put(document.getId(), value);

                        if (weightData.size() == 7) {
                            showChart(last7Days);
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e("WeightChart", "Error al leer peso: " + e.getMessage())
                    );
        }
    }

    private void showChart(ArrayList<String> last7Days) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < last7Days.size(); i++) {
            String date = last7Days.get(i);
            float kg = weightData.getOrDefault(date, 0f);
            entries.add(new BarEntry(i, kg));

            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
                String day = new SimpleDateFormat("EEE", Locale.getDefault()).format(d);
                labels.add(day);
            } catch (Exception e) {
                labels.add("?");
            }
        }

        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        int textColor = isDarkMode ? Color.WHITE : Color.BLACK;

        BarDataSet dataSet = new BarDataSet(entries, "Peso (kg)");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(textColor);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        weightChart.setData(barData);

        XAxis xAxis = weightChart.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(textColor);
        xAxis.setLabelRotationAngle(-45f);

        YAxis yAxis = weightChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(13f);
        yAxis.setTextColor(textColor);
        weightChart.getAxisRight().setEnabled(false);

        weightChart.setDescription(null);
        weightChart.setFitBars(true);
        weightChart.setExtraBottomOffset(10f);
        weightChart.setBackgroundColor(isDarkMode ? Color.DKGRAY : Color.WHITE);
        weightChart.invalidate();
    }
}
