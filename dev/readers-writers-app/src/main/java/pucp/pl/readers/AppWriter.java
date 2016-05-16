/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pucp.pl.readers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author adun
 */
public class AppWriter extends Thread {

    DefaultListModel model;
    AppResource resource;

    Semaphore mx;
    Semaphore wrt;
    AtomicInteger ctr;

    boolean stop;
    int sleepTime;

    AppWriter(String name, AppResource resource, DefaultListModel writersListModel) {
        super(name);
        this.model = writersListModel;
        this.resource = resource;
        this.stop = false;
        this.sleepTime = 1000;
        log("added");
    }

    public void setMx(Semaphore mx) {
        this.mx = mx;
    }

    public void setWrt(Semaphore wrt) {
        this.wrt = wrt;
    }

    public void setCtr(AtomicInteger ctr) {
        this.ctr = ctr;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    void log(String message) {
        this.model.addElement(getName() + ":" + message);
    }

    @Override
    public synchronized void start() {
        if (mx == null || wrt == null || ctr == null) {
            throw new IllegalStateException("initialize mx, wrt and/or ctr");
        }
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    private void process() throws InterruptedException {
        while (!stop) {
            wrt.acquire();

            //[Critical Section]
            Object obj = ThreadLocalRandom.current().nextInt(1, 100 + 1);
            resource.getModel().addElement(obj);
            log(obj + " added");
            //[Critical Section]

            wrt.release();

            Thread.sleep(sleepTime);
        }
        log("stopped");
    }

    @Override
    public void run() {
        try {
            process();
        } catch (InterruptedException ex) {
            log(ex.getMessage());
        }
    }
}
