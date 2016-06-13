package cn.edu.hit.project.ec.network;

import android.net.wifi.ScanResult;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
                socket.getOutputStream().write(message.getBytes());
                socket.shutdownOutput();
                InputStream input = socket.getInputStream();
                int len;
                byte[] b = new byte[1024];
                StringBuilder sb = new StringBuilder();
                while ((len = input.read(b)) != -1) {
                    sb.append(new String(b, 0, len));
                }
                if (sb.toString().equals("ok")) {
                    mHandler.obtainMessage(0).sendToTarget();
                } else {
                    mHandler.obtainMessage(1).sendToTarget();
                }
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            mHandler.obtainMessage(-1).sendToTarget();
        }
    }
}
