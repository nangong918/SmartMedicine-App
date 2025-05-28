package com.czy.baseUtilsLib.reflect;


public class B {
    public C<Integer> doubleValue(Integer input) {
        return new C<>(input * 2);
    }

    public C<String> prependZero(String input) {
        return new C<>("0" + input);
    }

    public C<Double> incrementValue(Double input) {
        return new C<>(input + 1.0);
    }
}
