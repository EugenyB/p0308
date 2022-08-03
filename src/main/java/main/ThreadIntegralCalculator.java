package main;

import java.util.function.DoubleUnaryOperator;

public class ThreadIntegralCalculator implements Runnable {

    private IntegralCalculator calculator;

    private Main main;

    public ThreadIntegralCalculator(double start, double finish, int n, DoubleUnaryOperator f, Main main) {
        calculator = new IntegralCalculator(start, finish, n, f);
        this.main = main;
    }

    @Override
    public void run() {
        double v = calculator.calculate();
        main.sendResult(v);
    }
}