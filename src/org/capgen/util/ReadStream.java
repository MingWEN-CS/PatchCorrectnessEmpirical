package org.capgen.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadStream implements Runnable {
    String name;
    InputStream is;
    Thread thread;
    boolean flag = true;
    public String output;

    public ReadStream(String name, InputStream is) {
        this.name = name;
        this.is = is;
        output = "";
    }
    public void start () {
        thread = new Thread (this);
        thread.start ();
    }

    public void end() {
        flag = false;
    }
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);
            while (flag) {
                String s = br.readLine ();
                if (s == null) break;
                output += s + "\n";
//              	System.out.println ("[" + name + "] " + s);
            }
            is.close ();

            synchronized (this) {
                this.notify();
            }
        } catch (Exception ex) {
            System.out.println ("Problem reading stream " + name + "... :" + ex);
            ex.printStackTrace ();
        }
    }
}