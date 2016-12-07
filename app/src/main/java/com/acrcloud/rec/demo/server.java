package com.acrcloud.rec.demo;

import android.annotation.TargetApi;
import android.os.Build;

import com.acrcloud.rec.demo.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class server extends MainActivity{

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws Exception {
        final ServerSocket server = new ServerSocket(10000);
        System.out.println("Listening for connection on port 10000 ....");
        while (true) {
            try (Socket socket = server.accept()) {
                Date today = new Date();
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today;
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            }
        }
    }
}

