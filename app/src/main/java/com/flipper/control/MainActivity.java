package com.flipper.control;

import android.bluetooth.BluetoothAdapter;
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
    private BluetoothAdapter bluetoothAdapter;

    private boolean isScanning = false;
    private int currentIndex = 0;
    private Handler handler = new Handler();

    private int[][] irSignals = {
        {4000, 4000, 500, 1500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500},
        {4500, 4500, 500, 1600, 500, 1600, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 500},
        {9000, 4500, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600},
        {2400, 600, 1200, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600},
        {3500, 1750, 450, 1300, 450, 1300, 450, 450, 450, 450, 450, 1300, 450, 450, 450, 450, 450, 450, 450, 450, 450, 1300, 450, 450, 450, 450, 450, 1300, 450, 450, 450, 450, 450, 450}
    };

    private String[] irBrands = {"PHILIPS", "SAMSUNG", "LG", "SONY", "XIAOMI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.tvOutput);

        // Инициализация с проверкой на null
        try {
            irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        } catch (Exception e) {
            tvOutput.setText("❌ Ошибка IR: " + e.getMessage());
        }

        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        } catch (Exception e) {
            tvOutput.setText("❌ Ошибка NFC: " + e.getMessage());
        }

        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e) {
            tvOutput.setText("❌ Ошибка Bluetooth: " + e.getMessage());
        }

        Button btnIr = findViewById(R.id.btnIr);
        Button btnNfc = findViewById(R.id.btnNfc);
        Button btnBadUsb = findViewById(R.id.btnBadUsb);
        Button btnSubGhz = findViewById(R.id.btnSubGhz);

        btnIr.setOnClickListener(v -> {
            if (isScanning) {
                stopIrScan();
            } else {
                startIrScan();
            }
        });

        btnNfc.setOnClickListener(v -> {
            if (nfcAdapter == null) {
                tvOutput.setText("❌ NFC не поддерживается");
                Toast.makeText(this, "NFC не найден", Toast.LENGTH_SHORT).show();
            } else {
                tvOutput.setText("📱 NFC готов, поднесите карту");
                Toast.makeText(this, "NFC готов к чтению", Toast.LENGTH_SHORT).show();
            }
        });

        btnBadUsb.setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
                tvOutput.setText("❌ Bluetooth не поддерживается");
                Toast.makeText(this, "Bluetooth не найден", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!bluetoothAdapter.isEnabled()) {
                tvOutput.setText("❌ Включите Bluetooth");
                Toast.makeText(this, "Включите Bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }
            tvOutput.setText("⌨️ BadUSB активен");
            Toast.makeText(this, "BadUSB готов", Toast.LENGTH_SHORT).show();
        });

        btnSubGhz.setOnClickListener(v -> {
            tvOutput.setText("📡 Sub-GHz: требуется внешний модуль");
            Toast.makeText(this, "Sub-GHz не поддерживается", Toast.LENGTH_SHORT).show();
        });

        tvOutput.setText("> FLIPPER ZERO READY\n> НАЖМИ IR ДЛЯ ПОИСКА");
    }

    // ========== IR SCAN ==========
    private void startIrScan() {
        if (irManager == null || !irManager.hasIrEmitter()) {
            tvOutput.setText("❌ IR не поддерживается");
            Toast.makeText(this, "IR не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        isScanning = true;
        currentIndex = 0;
        tvOutput.setText("🔍 IR SCAN...\nНаведи на ТВ\n");
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
            tvOutput.append(" ❌ Ошибка");
            stopIrScan();
            return;
        }

        currentIndex++;
        handler.postDelayed(this::sendNextIrSignal, 1200);
    }

    private void stopIrScan() {
        isScanning = false;
        handler.removeCallbacksAndMessages(null);
        tvOutput.append("\n⏹ Остановлено");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
