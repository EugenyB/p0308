package main;

import java.util.concurrent.Callable;
import java.util.function.DoubleUnaryOperator;

public class CallableIntegralCalculator implements Callable<Double> {

    private final IntegralCalculator calculator;

    public CallableIntegralCalculator(double start, double finish, int n, DoubleUnaryOperator f) {
        calculator = new IntegralCalculator(start, finish, n, f);
    }

    @Override
    public Double call() throws Exception {
        return calculator.calculate();
    }
}
