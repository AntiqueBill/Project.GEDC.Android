package cn.edu.hit.project.ec.network;

import android.os.Handler;
import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class CameraSshService implements Runnable {
    private String mCommand;
    private JSch mJSch;
    private Handler mHandler;

    public CameraSshService(String command, Handler handler) {
        this.mJSch = new JSch();
        this.mCommand = command;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        try {
            Session session = mJSch.getSession("root", "192.168.0.1");
            session.setPassword("");
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(mCommand);
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
