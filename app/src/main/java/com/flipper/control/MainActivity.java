package com.flipper.control;

import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvOutput;
    private ConsumerIrManager irManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.tvOutput);
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        // Находим наши кнопки
        Button btnMiBox = findViewById(R.id.btnMiBox);
        Button btnPhilips = findViewById(R.id.btnPhilips);

        // Кнопка для Mi Box (уже работает!)
        btnMiBox.setOnClickListener(v -> {
            if (irManager == null || !irManager.hasIrEmitter()) {
                tvOutput.setText("❌ IR не найден");
                return;
            }
            // Сигнал для Xiaomi Mi Box
            int[] pattern = new int[]{3200, 1650, 420, 1250, 420, 1250, 420, 420, 420, 420, 420, 1250, 420, 420, 420, 420, 420, 420, 420, 420, 420, 1250, 420, 420, 420, 420, 420, 1250, 420, 420, 420, 420, 420, 420};
            irManager.transmit(38000, pattern);
            tvOutput.setText("✅ Сигнал на Mi Box отправлен!");
        });

        // Кнопка для Philips (попытка)
        btnPhilips.setOnClickListener(v -> {
            if (irManager == null || !irManager.hasIrEmitter()) {
                tvOutput.setText("❌ IR не найден");
                return;
            }
            // Пробуем другой популярный код Philips (RC-5)
            int[] pattern = new int[]{4000, 4000, 500, 1500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500};
            irManager.transmit(38000, pattern);
            tvOutput.setText("✅ Сигнал на Philips отправлен! Если не включился — попробуй ещё раз.");
        });
    }
}
