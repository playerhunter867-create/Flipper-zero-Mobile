package com.flipper.control;

import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TestIrActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ir);

        TextView tvStatus = findViewById(R.id.tvStatus);
        Button btnTest = findViewById(R.id.btnTest);

        ConsumerIrManager ir = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        btnTest.setOnClickListener(v -> {
            if (ir == null || !ir.hasIrEmitter()) {
                tvStatus.setText("❌ IR НЕ НАЙДЕН");
                return;
            }

            ir.transmit(38000, new int[]{1000, 1000});
            tvStatus.setText("✅ СИГНАЛ ОТПРАВЛЕН");
        });
    }
}
