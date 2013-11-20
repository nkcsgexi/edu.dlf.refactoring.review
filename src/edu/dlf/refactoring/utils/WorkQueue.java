package edu.dlf.refactoring.utils;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.Equal;
import fj.data.List;
import fj.data.List.Buffer;

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
    
    public void execute(WorkQueueItem r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }
    
    public boolean isInQueueNow() {
    	long current = Thread.currentThread().getId();
    	return getAllWorkerID().find(Equal.longEqual.eq(current)).isSome();
    }
    
    private List<Long> getAllWorkerID(){
	   Buffer<Long> buffer = Buffer.empty();
	   for(Thread t : threads) {
		   buffer.snoc(t.getId());
	   }
	   return buffer.toList();
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