package com.flipper.control;

import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvOutput;
    private ConsumerIrManager irManager;
    private boolean isScanning = false;
    private int currentIndex = 0;
    private Handler handler = new Handler();

    private int[][] irSignals = {
        {4000, 4000, 500, 1500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500},
        {4500, 4500, 500, 1600, 500, 1600, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 500},
        {9000, 4500, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600}
    };

    private String[] irBrands = {"PHILIPS", "SAMSUNG", "LG"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.tvOutput);
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        Button btnIr = findViewById(R.id.btnIr);
        Button btnNfc = findViewById(R.id.btnNfc);
        Button btnBadUsb = findViewById(R.id.btnBadUsb);

        if (irManager == null || !irManager.hasIrEmitter()) {
            tvOutput.setText("❌ IR не найден");
        } else {
            tvOutput.setText("> FLIPPER ZERO READY");
        }

        btnIr.setOnClickListener(v -> {
            if (isScanning) {
                stopIrScan();
            } else {
                startIrScan();
            }
        });

        btnNfc.setOnClickListener(v -> {
            tvOutput.setText("📱 NFC: поднесите карту");
        });

        btnBadUsb.setOnClickListener(v -> {
            tvOutput.setText("⌨️ BadUSB: включите Bluetooth");
        });
    }

    private void startIrScan() {
        if (irManager == null || !irManager.hasIrEmitter()) {
            tvOutput.setText("❌ IR не найден");
            return;
        }

        isScanning = true;
        currentIndex = 0;
        tvOutput.setText("🔍 IR SCAN...\n");
        sendNextIrSignal();
    }

    private void sendNextIrSignal() {
        if (!isScanning || currentIndex >= irSignals.length) {
            stopIrScan();
            return;
        }

        tvOutput.append("\n📤 " + irBrands[currentIndex] + "...");
        try {
            irManager.transmit(38000, irSignals[currentIndex]);
        } catch (Exception e) {
            tvOutput.append(" ❌");
            stopIrScan();
            return;
        }

        currentIndex++;
        handler.postDelayed(this::sendNextIrSignal, 1500);
    }

    private void stopIrScan() {
        isScanning = false;
        handler.removeCallbacksAndMessages(null);
        tvOutput.append("\n⏹ СТОП");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
            }
