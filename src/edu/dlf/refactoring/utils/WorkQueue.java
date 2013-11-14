package edu.dlf.refactoring.utils;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.ServiceLocator;

public class WorkQueue
{
    private final PoolWorker[] threads;
    private final LinkedList<Runnable> queue;
    private final Logger logger = ServiceLocator.ResolveType(Logger.class);

    public WorkQueue(int nThreads)
    {
        queue = new LinkedList<Runnable>();
        threads = new PoolWorker[nThreads];
        for (int i=0; i<nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }
    
    public void execute(Runnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    private class PoolWorker extends Thread {
        public void run() {
            Runnable r;
            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException e)
                        {
                        	logger.fatal(e);
                        }
                    }
                    r = (Runnable) queue.removeFirst();
                }
                try {
                    r.run();
                    logger.info("Queue length:" + queue.size());
                }
                catch (RuntimeException e) {
                   logger.fatal(e);
                }
            }
        }
    }
}