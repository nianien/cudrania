package com.cudrania.test.proxy;

public class Task implements ITask {

    @Override
    public void doIt() {
        System.out.println("[" + this + "]do task");

    }

}
