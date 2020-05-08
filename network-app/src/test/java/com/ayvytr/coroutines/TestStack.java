package com.ayvytr.coroutines;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author admin
 */
public class TestStack {
    @Test
    public void t1() {
        Set<Short> stack = new HashSet<>();
        for (Short i = 0; i < 100; i++) {
            stack.add(i);
            boolean remove = stack.remove(i - 1);
            System.out.println(remove);
        }

        System.out.println(stack.size());
    }
}
