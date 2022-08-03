package main;

import java.util.function.DoubleUnaryOperator;

public class ThreadIntegralCalculator2 extends Thread {

    private IntegralCalculator calculator;

    private double result;

    public ThreadIntegralCalculator2(double start, double finish, int n, DoubleUnaryOperator f) {
        calculator = new IntegralCalculator(start, finish, n, f);
    }

    @Override
    public void run() {
        result = calculator.calculate();
    }

    public double getResult() {
        return result;
    }
}