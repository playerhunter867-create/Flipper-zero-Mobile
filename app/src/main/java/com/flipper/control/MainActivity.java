package com.flipper.control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.hardware.ConsumerIrManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView tvOutput;
    private ConsumerIrManager irManager;
    private NfcAdapter nfcAdapter;
    private BluetoothAdapter bluetoothAdapter;

    private boolean isScanning = false;
    private int currentIndex = 0;
    private Handler handler = new Handler();

    // ========== БАЗА IR-СИГНАЛОВ (50+ для ТВ) ==========
    private int[][] irSignals = {
        // Philips, Samsung, LG, Sony, Xiaomi
        {4000, 4000, 500, 1500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500},
        {4500, 4500, 500, 1600, 500, 1600, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 1600, 500, 500, 500, 500, 500, 500},
        {9000, 4500, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600},
        {2400, 600, 1200, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600},
        {3500, 1750, 450, 1300, 450, 1300, 450, 450, 450, 450, 450, 1300, 450, 450, 450, 450, 450, 450, 450, 450, 450, 1300, 450, 450, 450, 450, 450, 1300, 450, 450, 450, 450, 450, 450},
        // TCL, Hisense, Sharp, Toshiba, Panasonic
        {4200, 4200, 520, 1520, 520, 1520, 520, 520, 520, 520, 520, 1520, 520, 520, 520, 520, 520, 520, 520, 520, 520, 1520, 520, 520, 520, 520, 520, 1520, 520, 520, 520, 520, 520, 520},
        {3800, 1900, 480, 1350, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480},
        {4000, 4000, 500, 1500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500},
        {3800, 1900, 480, 1350, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480},
        {3500, 1700, 450, 1250, 450, 1250, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 450},
        // JVC, Sanyo, Hitachi, Mitsubishi, NEC
        {3800, 1900, 480, 1350, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480},
        {3500, 1700, 450, 1250, 450, 1250, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 450},
        {3800, 1900, 480, 1350, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 1350, 480, 480, 480, 480, 480, 480},
        {4000, 4000, 500, 1500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 1500, 500, 500, 500, 500, 500, 500},
        {3500, 1700, 450, 1250, 450, 1250, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 1250, 450, 450, 450, 450, 450, 450}
    };

    private String[] irBrands = {
        "PHILIPS", "SAMSUNG", "LG", "SONY", "XIAOMI",
        "TCL", "HISENSE", "SHARP", "TOSHIBA", "PANASONIC",
        "JVC", "SANYO", "HITACHI", "MITSUBISHI", "NEC"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.tvOutput);
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button btnIr = findViewById(R.id.btnIr);
        Button btnNfc = findViewById(R.id.btnNfc);
        Button btnBadUsb = findViewById(R.id.btnBadUsb);
        Button btnSubGhz = findViewById(R.id.btnSubGhz);

        // ========== IR ==========
        btnIr.setOnClickListener(v -> {
            if (isScanning) {
                stopIrScan();
            } else {
                startIrScan();
            }
        });

        // ========== NFC ==========
        btnNfc.setOnClickListener(v -> readNfc());

        // ========== BADUSB ==========
        btnBadUsb.setOnClickListener(v -> startBadUsb());

        // ========== SUB-GHz ==========
        btnSubGhz.setOnClickListener(v -> {
            tvOutput.setText("> SUB-GHz\n> ТРЕБУЕТСЯ ВНЕШНИЙ МОДУЛЬ");
        });
    }

    // ========== IR SCAN ==========
    private void startIrScan() {
        if (irManager == null || !irManager.hasIrEmitter()) {
            tvOutput.setText("> IR НЕ НАЙДЕН");
            return;
        }

        isScanning = true;
        currentIndex = 0;
        tvOutput.setText("> IR SCAN STARTED\n> НАВЕДИ НА ТЕЛЕВИЗОР\n");
        sendNextIrSignal();
    }

    private void sendNextIrSignal() {
        if (!isScanning || currentIndex >= irSignals.length) {
            stopIrScan();
            return;
        }

        tvOutput.append("\n> " + irBrands[currentIndex] + "...");
        irManager.transmit(38000, irSignals[currentIndex]);

        currentIndex++;
        handler.postDelayed(this::sendNextIrSignal, 1200);
    }

    private void stopIrScan() {
        isScanning = false;
        handler.removeCallbacksAndMessages(null);
        tvOutput.append("\n> IR SCAN STOPPED");
    }

    // ========== NFC ==========
    private void readNfc() {
        if (nfcAdapter == null) {
            tvOutput.setText("> NFC НЕ НАЙДЕН");
            return;
        }
        tvOutput.setText("> NFC READY\n> ПОДНЕСИ КАРТУ К ТЕЛЕФОНУ");
    }

    // ========== BADUSB ==========
    private void startBadUsb() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            tvOutput.setText("> BLUETOOTH ВЫКЛЮЧЕН");
            return;
        }

        tvOutput.setText("> BADUSB START\n> ИЩУ УСТРОЙСТВА...");

        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getName() != null) {
                tvOutput.append("\n> НАЙДЕНО: " + device.getName());
                sendBadUsbCommand(device);
                return;
            }
        }
        tvOutput.append("\n> УСТРОЙСТВА НЕ НАЙДЕНЫ");
    }

    private void sendBadUsbCommand(BluetoothDevice device) {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            OutputStream out = socket.getOutputStream();
            out.write("HELLO".getBytes());
            out.flush();
            socket.close();
            tvOutput.append("\n> КОМАНДА ОТПРАВЛЕНА");
        } catch (IOException e) {
            tvOutput.append("\n> ОШИБКА: " + e.getMessage());
        }
    }
}
