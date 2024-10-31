package com.zlx.deployment.exposeInterfaceImpl;

import com.zlx.deployment.exposeInterface.Calculator;
import org.springframework.stereotype.Service;

//@Service("abc")
public class CalculatorImpl implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b + 100;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

}
