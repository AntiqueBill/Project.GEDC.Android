package cn.edu.hit.project.ec.network;

import android.net.wifi.ScanResult;
import android.os.Handler;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

import cn.edu.hit.project.ec.App;
import cn.edu.hit.project.ec.utils.StringUtils;
import cn.edu.hit.project.ec.utils.WifiUtils;

import static cn.edu.hit.project.ec.utils.StringUtils.escapeString;

public class CameraSshMatcher implements Runnable {
    private JSch mJSch;
    private int mSensorId;
    private String mPassword;
    private Handler mHandler;
    private ScanResult mWifi;

    public CameraSshMatcher(int sensorId, ScanResult wifi, String password, Handler handler) {
        this.mJSch = new JSch();
        this.mWifi = wifi;
        this.mHandler = handler;
        this.mSensorId = sensorId;
        this.mPassword = password;
    }

    @Override
    public void run() {
        String command = "sed -i \"s:^STREAM_SERVER=.*:STREAM_SERVER=" + escapeString(App.STREAM_SERVER) + ":g\" /etc/init.d/streaming.sh && " +
                "sed -i \"s:^STREAM_ID=.*:STREAM_ID=" + mSensorId + ":g\" /etc/init.d/streaming.sh && " +
                "sed -ire \"s:(^ssid\\s*=\\s*)(.*):\\1" + escapeString(mWifi.SSID) + ":g\" /etc/jffs2/camera.ini && " +
                "sed -ire \"s:(^security\\s*=\\s*)(.*):\\1" + escapeString(WifiUtils.getWifiSecurityString(mWifi)) + ":g\" /etc/jffs2/camera.ini && " +
                "sed -ire \"s:(^password\\s*=\\s*)(.*):\\1" + escapeString(mPassword) +  ":g\" /etc/jffs2/camera.ini && " +
                "sed -ire \"s:(^running\\s*=\\s*)(.*):\\1station:g\" /etc/jffs2/camera.ini && " +
                "/sbin/reboot";
        try {
            Session session = mJSch.getSession("root", "192.168.0.1");
            session.setPassword("");
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.setTimeout(10000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(command);
            channel.connect(1000);
            // this kludge seemed to be required.
            Thread.sleep(500);
            channel.disconnect();
            if (channel.getExitStatus() == -1) {
                mHandler.obtainMessage(0).sendToTarget();
            } else {
                mHandler.obtainMessage(1).sendToTarget();
            }
        } catch (JSchException | InterruptedException e) {
            mHandler.obtainMessage(-1).sendToTarget();
        }
    }
}
