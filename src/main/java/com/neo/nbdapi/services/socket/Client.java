package com.neo.nbdapi.services.socket;

import java.io.*;
import java.net.Socket;

public class Client {
    Socket m_Socket;
    int m_port;

    public void Open(String ip, int port) {
        m_port = port;
        try {
            m_Socket = new Socket(ip, port);
            DataOutputStream outToServer = new DataOutputStream(m_Socket.getOutputStream());
            ClientRead cl = new ClientRead(m_Socket);
            cl.start();

            String sentence = "";
            ByteArrayOutputStream ouar = new ByteArrayOutputStream();
            ouar.write(sentence.getBytes(), 0, sentence.length());
            outToServer.write(ouar.toByteArray());

            m_Socket.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Client cl = new Client();
        cl.Open("192.168.1.20", 10001);
    }
}
