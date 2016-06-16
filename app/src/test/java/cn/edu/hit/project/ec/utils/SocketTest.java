package cn.edu.hit.project.ec.utils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTest {

    @Test
    public void testSocket() throws Exception {

        String message = String.format("%d:%s:%s\r\n", 1, "wifi", "password");
        try {
            Socket socket = new Socket("192.168.4.1", 8082);
            try {
                socket.setSoTimeout(10000);
                OutputStream output = socket.getOutputStream();
                output.write(message.getBytes());
                output.flush();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    try {
                        String line = input.readLine();
                        if (line != null && line.equals("ok")) {
                            break;
                        }
                    } catch (Exception e) {
                        socket.close();
                    }
                }
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
