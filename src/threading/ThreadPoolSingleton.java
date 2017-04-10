package threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nicklas Boserup
 */
public class ThreadPoolSingleton {
    
    private static ExecutorService pool = null;
    
    private ThreadPoolSingleton() {}
    
    public synchronized static ExecutorService getExecutor() {
        if(pool == null)
            pool = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors()-1));
        return pool;
    }
    
}
