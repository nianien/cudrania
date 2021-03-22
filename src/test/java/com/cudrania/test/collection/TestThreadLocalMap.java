package com.cudrania.test.collection;

import com.cudrania.core.collection.map.ThreadLocalMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author skyfalling
 */
public class TestThreadLocalMap {

    private static ThreadLocalMap localMap = new ThreadLocalMap();

    @Test
    public void main() {
        List<Thread> list = new ArrayList();
        for (int i = 0; i < 3; i++) {

            list.add(new Thread("Thread-" + i) {
                @Override
                public void run() {
                    int j = 0;
                    while (j++ < 10) {
                        AtomicLong atomicLong = (AtomicLong) localMap.get("times");
                        if (atomicLong == null) {
                            localMap.put("times", new AtomicLong());
                        }
                        atomicLong = ((AtomicLong) localMap.get("times"));
                        atomicLong.getAndIncrement();
                        System.out.println("[" + this.getName() + "]times=" + atomicLong);
                        try {
                            Thread.sleep(new Random().nextInt(10) * 100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        for (Thread t : list) {
            t.start();
        }
        for (Thread t : list) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
