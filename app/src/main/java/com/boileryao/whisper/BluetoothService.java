package com.boileryao.whisper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Bluetooth Service, socket related
 */

class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static boolean isServer;
    private static boolean isAvailable;
    private static boolean isTurnedOff;

    private static BluetoothService INSTANCE;
    private final UUID uuid;
    private Activity activity;
    private BluetoothAdapter adapter;

    private BluetoothSocket socket;
    private BluetoothServerSocket serverSocket;
    private Gson gsonGenerater;

    /*
    * Constructor Singleton*/
    private BluetoothService() {
//        uuid = UUID.fromString("b5536967-a052-4285-bba3-f2bc21623504");
        uuid = UUID.fromString("b5536967-a052-4285-bba3-f2bc21623504");
        adapter = BluetoothAdapter.getDefaultAdapter();
        gsonGenerater = new GsonBuilder().create();
    }

    static BluetoothService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BluetoothService();
        }
        return INSTANCE;
    }

    void openBluetooth() {
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        adapter.cancelDiscovery();
    }

    List<BluetoothDevice> getPairedDevices() {
        List<BluetoothDevice> pairedDevices = new LinkedList<>();
        for (BluetoothDevice device : adapter.getBondedDevices()) {
            pairedDevices.add(device);
        }
        return pairedDevices;
    }

    /*
    * 连接策略：
    * Open Client, wait 1s, open Server.
    * once device client connected, close its server
    * 即，最先尝试连接的那个是Server*/
    boolean connect(BluetoothDevice device) {
        isAvailable = false;
        isTurnedOff = false;
        ClientConnectThread clientConnectThread;
        Log.d(TAG, "Build Brand " + Build.BRAND);
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            clientConnectThread = new ClientConnectThread(socket);
            clientConnectThread.start();
        } catch (IOException ignored) {

        }
        try {
            serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("xx_bluetooth_server", uuid);
            new ServerAcceptThread(serverSocket).start();
        } catch (IOException e2) {
            return false;
        }
        return true;
    }

    boolean isAvailable() {
        return isAvailable;
    }

    boolean isTurnedOff() {
        return isTurnedOff;
    }

    void send(QMessage msg) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeUTF(gsonGenerater.toJson(msg));
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "Exception on send");
        } catch (Exception ignored) {}
    }

    QMessage receive() {
        String content = null;
        try {
            if (socket != null && socket.isConnected()) {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                content = in.readUTF();
            } else {
                Thread.sleep(200);
                Log.e(TAG, "can not receive" + (socket == null));
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception on receive");
        } catch (InterruptedException ignored) {}
        return gsonGenerater.fromJson(content, QMessage.class);
    }

    void stop(boolean offBluetooth) {
        isAvailable = false;
        isTurnedOff = true;
        try {
            serverSocket.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }

        if (offBluetooth) {
            adapter.disable();
        }
    }

    String getLocalAddress() {
        return adapter.getAddress();
    }

    private void echoState() {
        Log.d(TAG, "echoState: Server is null: " + (serverSocket == null));
        if (socket != null) {
            Log.d(TAG, "echoState: Socket is connected: " + socket.isConnected());
            Log.d(TAG, "echoState: Remote: " + socket.getRemoteDevice());
        }
        Log.d(TAG, "echoState: is server: " + isServer);
    }

    private class ServerAcceptThread extends Thread {
        BluetoothServerSocket server;

        ServerAcceptThread(BluetoothServerSocket server) {
            this.server = server;
        }

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(500);
                Log.i(TAG, Build.VERSION.CODENAME);
                socket = server.accept();
                Log.d(TAG, "Act as server……");
                isAvailable = true;
                isServer = true;
                echoState();
            } catch (IOException e) {
                Log.e(TAG, "Exception on accept");
            } catch (InterruptedException ignored) {

            }
        }
    }

    private class ClientConnectThread extends Thread {
        BluetoothSocket socket;

        ClientConnectThread(BluetoothSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            try {
                while (!socket.isConnected()) {
                    socket.connect();
                    Log.d(TAG, "Act as client……");
                    serverSocket.close();
                }
                Log.d(TAG, socket.getRemoteDevice().getName());
                isAvailable = true;
                isServer = false;
                echoState();
            } catch (IOException e) {
                Log.e(TAG, "Exception on connect");
            }
        }
    }
}