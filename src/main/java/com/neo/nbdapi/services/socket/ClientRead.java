package com.neo.nbdapi.services.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientRead extends Thread {
    public Socket m_Socket;
    public ByteArrayOutputStream m_buffIn;

    public ClientRead(Socket sk) {
        m_Socket = sk;
        m_buffIn = new ByteArrayOutputStream();
    }

    @Override
    public void run() {
        boolean run = true;
        InputStream inSt = null;
        try {
            inSt = m_Socket.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (run) {
            try {
                do {
                    int bytesRead = 0;
                    byte[] buffer = new byte[1000];
                    bytesRead = inSt.read(buffer);
                    m_buffIn.write(buffer, 0, bytesRead);
                    TimeUnit.MILLISECONDS.sleep(10);
                } while (inSt.available() != 0);
                Receive();
                m_buffIn.reset();
            } catch (Exception e) {
                System.out.println("Disconect");
                run = false;
            }
        }

    }

    public void Receive() {
        System.out.println("Buff size:" + m_buffIn.size());
        InputStream read = new ByteArrayInputStream(m_buffIn.toByteArray());

        //System.out.println("");
        System.out.println("m_buffIn:" + m_buffIn);
/*		try {
			for(int i=0;i<m_buffIn.size();i++){
				int a=read.read();					
				System.out.print(String.format(" %02X",a));
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        System.out.print(">");
    }
}
