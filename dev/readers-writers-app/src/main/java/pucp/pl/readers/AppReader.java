/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pucp.pl.readers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author adun
 */
public class AppReader extends Thread {

    DefaultListModel model;
    AppResource resource;
    
    Semaphore mx;
    Semaphore wrt;
    AtomicInteger ctr;
    
    boolean stop;
    int sleepTime;

    AppReader(String name, AppResource resource, DefaultListModel readersListModel) {
        super(name);
        this.model = readersListModel;
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
        super.start();
    }

    private void process() throws InterruptedException {
        while (!stop) {
            mx.acquire();
            if (ctr.incrementAndGet() == 1) {
                wrt.acquire();
            }
            mx.release();

            //[Critical Section]
            if (resource.getModel().size() > 0) {
                Object obj = resource.getModel().remove(0);
                log(obj + " retrieved");
            } else {
                log("resource is empty");
            }
            //[Critical Section]
            
            mx.acquire();
            if(ctr.decrementAndGet() == 0) {
                wrt.release();
            }
            mx.release();
            
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
