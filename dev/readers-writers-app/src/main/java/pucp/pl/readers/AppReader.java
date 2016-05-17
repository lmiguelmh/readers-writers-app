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
    
    Semaphore readerMutex;
    Semaphore writerMutex;
    AtomicInteger readersCount;
    
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

    public void setReaderMutex(Semaphore readerMutex) {
        this.readerMutex = readerMutex;
    }

    public void setWriterMutex(Semaphore writerMutex) {
        this.writerMutex = writerMutex;
    }

    public void setReadersCount(AtomicInteger readersCount) {
        this.readersCount = readersCount;
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
        if (readerMutex == null || writerMutex == null || readersCount == null) {
            throw new IllegalStateException("initialize mx, wrt and/or ctr");
        }
        super.start();
    }

    private void process() throws InterruptedException {
        while (!stop) {
            //solution with readers preference: no reader will be keep waiting unless a writer has already obtained permissions to us the resource
            readerMutex.acquire(); //only one reader could modify writerMutex
            if (readersCount.incrementAndGet() == 1) {
                //the first reader that enters the critical section blocks all writers or waits until the writer has finished 
                writerMutex.acquire();
            }
            readerMutex.release();

            //[Critical Section]
            if (resource.getModel().size() > 0) {
                Object obj = resource.getModel().remove(0);
                log(obj + " retrieved");
            } else {
                log("resource is empty");
            }
            //[/Critical Section]
            
            readerMutex.acquire();
            if(readersCount.decrementAndGet() == 0) {
                //the last reader that leaves the critical section unblocks all writers
                writerMutex.release();
            }
            readerMutex.release();
            
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
