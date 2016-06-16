package cn.edu.hit.project.ec.network;

import android.net.wifi.ScanResult;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static cn.edu.hit.project.ec.utils.StringUtils.escapeString;

public class BandTcpMatcher implements Runnable {
    private String mIp;
    private int mSensorId;
    private String mPassword;
    private Handler mHandler;
    private ScanResult mWifi;

    public BandTcpMatcher(String ip, int sensorId, ScanResult wifi, String password, Handler handler) {
        this.mIp = ip;
        this.mWifi = wifi;
        this.mHandler = handler;
        this.mSensorId = sensorId;
        this.mPassword = password;
    }

    @Override
    public void run() {
        String message = String.format("%d:%s:%s\r\n", mSensorId, escapeString(mWifi.SSID), escapeString(mPassword));
        try {
            Socket socket = new Socket(mIp, 8082);
            try {
                socket.setSoTimeout(10000);
                OutputStream output = socket.getOutputStream();
                output.write(message.getBytes());
                output.flush();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    try {
                        String line = input.readLine();
                        if (line != null) {
                            if (line.equals("ok")) {
                                mHandler.obtainMessage(0).sendToTarget();
                            } else {
                                mHandler.obtainMessage(1).sendToTarget();
                            }
                            break;
                        }
                    } catch (SocketTimeoutException e){
                        mHandler.obtainMessage(1).sendToTarget();
                        break;
                    }
                }
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            mHandler.obtainMessage(-1).sendToTarget();
        }
    }
}
