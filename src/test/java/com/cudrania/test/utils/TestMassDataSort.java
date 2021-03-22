package com.cudrania.test.utils;

import java.io.File;
import java.util.Comparator;

import org.junit.Test;

import com.cudrania.core.loader.ResourceLoader;
import com.cudrania.algorithm.MassSorter;

public class TestMassDataSort {
	@Test
	public void test() throws Exception {

		MassSorter mds = new MassSorter(10000,
				new Comparator<String>() {

					@Override
					public int compare(String str1, String str2) {
						if (str1.trim().isEmpty()) {
							return 1;
						}
						if (str2.trim().isEmpty()) {
							return -1;
						}
						return Integer.parseInt(str1.trim())
								- Integer.parseInt(str2.trim());
					}
				});

		File f1 = ResourceLoader.getFile("all200000.txt");
		File f2 = ResourceLoader.getFile("all200000.txt");
		mds.sort(f1, f2);

	}
}
