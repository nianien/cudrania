package com.cudrania.test.utils;

import com.cudrania.core.bits.ByteUtils;
import org.junit.jupiter.api.Test;

/**
 * @author skyfalling.
 */
public class TestByteUtils {

    @Test
    public void test() {
        String hex = ByteUtils.byte2Hex("中国".getBytes());
        System.out.println(hex);
        assert hex.equals("e4b8ade59bbd");
        assert new String(ByteUtils.hex2Byte(hex.toUpperCase())).equals("中国");
    }
}
