package com.cudrania.core.functions;

/**
 * 多个参数的函数定义
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
public class Fn {


    @FunctionalInterface
    public interface Consumer3<P1, P2, P3> {
        void accept(P1 p1, P2 p2, P3 p3);
    }

    @FunctionalInterface
    public interface Consumer4<P1, P2, P3, P4> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4);
    }


    @FunctionalInterface
    public interface Consumer5<P1, P2, P3, P4, P5> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    @FunctionalInterface
    public interface Consumer6<P1, P2, P3, P4, P5, P6> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    @FunctionalInterface
    public interface Consumer7<P1, P2, P3, P4, P5, P6, P7> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    @FunctionalInterface
    public interface Consumer8<P1, P2, P3, P4, P5, P6, P7, P8> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    @FunctionalInterface
    public interface Consumer9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    @FunctionalInterface
    public interface Function3<P1, P2, P3, R> {
        R apply(P1 p1, P2 p2, P3 p3);
    }

    @FunctionalInterface
    public interface Function4<P1, P2, P3, P4, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    @FunctionalInterface
    public interface Function5<P1, P2, P3, P4, P5, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    @FunctionalInterface
    public interface Function6<P1, P2, P3, P4, P5, P6, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    @FunctionalInterface
    public interface Function7<P1, P2, P3, P4, P5, P6, P7, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    @FunctionalInterface
    public interface Function8<P1, P2, P3, P4, P5, P6, P7, P8, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    @FunctionalInterface
    public interface Function9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }
}
