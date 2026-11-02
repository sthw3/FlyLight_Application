package com.example.flylight_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvExcess, tvCharge, tvTotal, tvNotice;
    TextInputLayout tilWeight, tilRate;
    RadioGroup rgClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        tilWeight = findViewById(R.id.tilWeight);
        tilRate = findViewById(R.id.tilRate);
        rgClass = findViewById(R.id.rgClass);
        tvExcess = findViewById(R.id.tvExcess);
        tvCharge = findViewById(R.id.tvCharge);
        tvTotal = findViewById(R.id.tvTotal);
        tvNotice = findViewById(R.id.tvNotice);

        findViewById(R.id.btnCalc).setOnClickListener(v -> calculate());
    }

    private void calculate() {
        tilWeight.setError(null);
        tilRate.setError(null);

        String weightStr = tilWeight.getEditText().getText().toString().trim();
        String rateStr = tilRate.getEditText().getText().toString().trim();

        // Error messages
        if (weightStr.isEmpty()) {
            tilWeight.setError("Please enter baggage weight");
            return;
        }
        if (rateStr.isEmpty()) {
            tilRate.setError("Please enter excess rate per kg");
            return;
        }

        double weight, rate;
        try {
            weight = Double.parseDouble(weightStr);
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weight < 0) {
            tilWeight.setError("Weight cannot be negative");
            return;
        }
        if (rate < 0) {
            tilRate.setError("Rate cannot be negative");
            return;
        }

        double limit = (rgClass.getCheckedRadioButtonId() == R.id.rbEconomy) ? 20 : 40;
        double excess = Math.max(0, weight - limit);   // never negative
        double charge = excess * rate;
        double total = charge + (0.08 * charge);

        tvExcess.setText(String.format(Locale.US, "Excess weight: %.1f kg", excess));
        tvCharge.setText(String.format(Locale.US, "Excess charge: RM %.2f", charge));
        tvTotal.setText(String.format(Locale.US, "Total payable: RM %.2f", total));

        if(excess == 0){
            String cls = (limit == 20) ? "Economy" : "Business";
            tvNotice.setText("No excess baggage charge. Your baggage is within the "
                    + (int) limit + " kg " + cls + " limit.");
        } else {
            tvNotice.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "FlyLight Baggage Estimator");
            i.putExtra(Intent.EXTRA_TEXT,
                    "Share this link: https://github.com/sthw3/FlyLight_Application");
            startActivity(Intent.createChooser(i, "Share via"));
            return true;
        }
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}