package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private double total;
    private int finished;

    private Lock lock;
    private Condition condition;

    public static void main(String[] args) {
        new Main().run3();
    }

    private void run3() {
        double a = 0;
        double b = Math.PI;
        int n = 1_000_000_000;
        int nThreads = 100;
        long startTime = System.currentTimeMillis();
        double delta = (b-a)/nThreads;
        total = 0;
        finished = 0;
        ThreadIntegralCalculator2[] threads = new ThreadIntegralCalculator2[nThreads];
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new ThreadIntegralCalculator2(a + i * delta, a + (i+1) * delta, n/nThreads, Math::sin);
            threads[i].start();
        }

        try {
            for (ThreadIntegralCalculator2 thread : threads) {
                thread.join();
                total += thread.getResult();
            }
            long finishTime = System.currentTimeMillis();
            System.out.println("result = " + total);
            System.out.println(finishTime-startTime);
        } catch (InterruptedException e) {
            System.err.println("Exception in thread");
        }


    }

    private void run() {
        double a = 0;
        double b = Math.PI;
        int n = 1_000_000_000;
        int nThreads = 10000;
        double delta = (b-a)/nThreads;
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(100);
        List<Future<Double>> futures = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            CallableIntegralCalculator calculator = new CallableIntegralCalculator(a + i * delta, a + (i+1) * delta, n/nThreads, Math::sin);
            Future<Double> future = es.submit(calculator);
            futures.add(future);
        }
        es.shutdown();
        total = 0;
        try {
            for (Future<Double> future : futures) {
                total += future.get();
            }
            long finishTime = System.currentTimeMillis();
            System.out.println("result = " + total);
            System.out.println(finishTime - startTime);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Exception in thread");
        }


    }

    private void run1() {
        // y = sin(x) [0, PI] 2
        double a = 0;
        double b = Math.PI;
        int n = 1_000_000_000;
        int nThreads = 1000;
        long startTime = System.currentTimeMillis();
        double delta = (b-a)/nThreads;
        total = 0;
        finished = 0;
        for (int i = 0; i < nThreads; i++) {
            new Thread(
                    new ThreadIntegralCalculator(a + i * delta, a + (i+1) * delta, n/nThreads, Math::sin, this)
            ).start();
        }

        try {
            synchronized (this) {
                while (finished < nThreads) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            System.err.println("interrupted");
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("total = " + total);
        System.out.println(finishTime - startTime);
    }

    private void run2() {
        // y = sin(x) [0, PI] 2
        double a = 0;
        double b = Math.PI;
        int n = 1_000_000_000;
        int nThreads = 100;
        long startTime = System.currentTimeMillis();
        double delta = (b-a)/nThreads;
        total = 0;
        finished = 0;
        for (int i = 0; i < nThreads; i++) {
            new Thread(
                    new ThreadIntegralCalculator(a + i * delta, a + (i+1) * delta, n/nThreads, Math::sin, this)
            ).start();
        }
        lock = new ReentrantLock();
        condition = lock.newCondition();
        try {
            lock.lock();
            while (finished < nThreads) {
                condition.await();
            }
        } catch (InterruptedException e) {
            System.err.println("Exception in thread");
        } finally {
            lock.unlock();
        }

        long finishTime = System.currentTimeMillis();
        System.out.println("total = " + total);
        System.out.println(finishTime - startTime);
    }

    //public synchronized void sendResult(double v) {
    public void sendResult(double v) {
        try {
            lock.lock();
            total += v;
            finished++;
            AtomicInteger a = new AtomicInteger();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}