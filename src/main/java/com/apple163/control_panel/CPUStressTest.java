package com.apple163.control_panel;
public class CPUStressTest {
    public static void main(String[] args) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                while (true) {
                    Math.sin(Math.random());
                }
            }).start();
        }
    }
}