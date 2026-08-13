package com.flipper.control;

import android.hardware.ConsumerIrManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvOutput;
    private ConsumerIrManager irManager;
    private NfcAdapter nfcAdapter;
    private Handler handler = new Handler();
    private boolean isScanning = false;
    private int currentIndex = 0;

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
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Button btnIr = findViewById(R.id.btnIr);
        Button btnNfc = findViewById(R.id.btnNfc);
        Button btnBadUsb = findViewById(R.id.btnBadUsb);

        btnIr.setOnClickListener(v -> {
            if (isScanning) {
                stopScan();
            } else {
                startScan();
            }
        });

        btnNfc.setOnClickListener(v -> {
            if (nfcAdapter == null) {
                tvOutput.setText("❌ NFC не поддерживается");
                Toast.makeText(this, "NFC нет", Toast.LENGTH_SHORT).show();
            } else {
                tvOutput.setText("📱 NFC: поднесите карту");
                Toast.makeText(this, "NFC готов", Toast.LENGTH_SHORT).show();
            }
        });

        btnBadUsb.setOnClickListener(v -> {
            tvOutput.setText("⌨️ BadUSB: включите Bluetooth");
            Toast.makeText(this, "BadUSB", Toast.LENGTH_SHORT).show();
        });

        tvOutput.setText("> FLIPPER ZERO // ANDROID\n> НАЖМИ IR SCAN");
    }

    private void startScan() {
        if (irManager == null || !irManager.hasIrEmitter()) {
            tvOutput.setText("❌ IR не найден");
            Toast.makeText(this, "IR нет", Toast.LENGTH_SHORT).show();
            return;
        }

        isScanning = true;
        currentIndex = 0;
        tvOutput.setText("🔍 IR SCAN...\nНаведи на ТВ\n");
        sendNext();
    }

    private void sendNext() {
        if (!isScanning || currentIndex >= irSignals.length) {
            stopScan();
            return;
        }

        tvOutput.append("\n📤 " + irBrands[currentIndex] + "...");
        try {
            irManager.transmit(38000, irSignals[currentIndex]);
        } catch (Exception e) {
            tvOutput.append(" ❌");
            stopScan();
            return;
        }

        currentIndex++;
        handler.postDelayed(this::sendNext, 1500);
    }

    private void stopScan() {
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
