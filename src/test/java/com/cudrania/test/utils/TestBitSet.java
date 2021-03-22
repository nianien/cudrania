package com.cudrania.test.utils;


import com.cudrania.core.bits.MultiBitSet;

public class TestBitSet {
	public static void main(String[] args) {
		MultiBitSet nb = new MultiBitSet(3);
		nb.set(1, 5, 5);
		nb.set(12, 21);
		nb.set(8, 5);
		for (int i = 0; i < 16; i++) {
			System.out.println("[" + i + "]=" + nb.get(i));
		}
		nb.clear(2, 8);
		System.out.println(">>>>>>clear");
		for (int i = 0; i < 16; i++) {
			System.out.println("[" + i + "]=" + nb.get(i));
		}
		System.out.println(">>>>>>length");
		System.out.println(nb.length());
		System.out.println(">>>>>>size");
		System.out.println(nb.size());
		System.out.println(">>>>>cardinality>");
		System.out.println(nb.cardinality());
		System.out.println(">>>>>nextSetBit>");
		System.out.println(nb.nextSetBit(5));
		// nb.clear();
		// System.out.println(nb.get(1));
		// System.out.println(nb.get(5));
		// System.out.println(nb.bitset.length());
	}
}
