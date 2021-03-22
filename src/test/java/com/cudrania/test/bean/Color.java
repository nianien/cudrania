package com.cudrania.test.bean;

/**
 * @author skyfalling
 */
public enum Color {

    RED(255, 0, 0), GREEN(0, 255, 0), BLUE(0, 0, 255), BLACK(0, 0, 0), WHITE(255, 255, 255);

    private int r = 0;
    private int g = 0;
    private int b = 0;

    Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
