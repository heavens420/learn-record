package com.zlx.deployment.spingBean;

import org.springframework.stereotype.Service;

@Service
public class CalculatorOperation {

    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }
}
