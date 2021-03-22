package com.cudrania.core.bits;

/**
 * 基本类型转字节数组
 *
 * @author skyfalling
 */
public class ByteUtils {

  private static final String hexString = "0123456789abcdef";

  /**
   * convert every one byte to 2 hexadecimal chars
   *
   * @param bytes
   * @return
   */
  public static String byte2Hex(byte[] bytes) {
    char[] buffer = new char[bytes.length * 2];
    for (int i = 0, j = 0; i < bytes.length; ++i) {
      int u = unsigned(bytes[i]);
      buffer[j++] = hexString.charAt(u >>> 4);
      buffer[j++] = hexString.charAt(u & 0xf);
    }
    return new String(buffer);
  }


  /**
   * convert every 2 hexadecimal chars to a byte
   *
   * @param hex hexadecimal chars
   * @return
   */
  public static byte[] hex2Byte(String hex) {
    String lowerCase = hex.toLowerCase();
    byte[] bytes = new byte[lowerCase.length() / 2];
    // a byte per two hex number
    for (int i = 0; i < bytes.length; i++)
      bytes[i] = (byte) (
              hexString.indexOf(lowerCase.charAt(2 * i)) << 4
                      | hexString.indexOf(lowerCase.charAt(2 * i + 1))
      );
    return bytes;
  }

  /**
   *  get a big-endian byte array with length of 2
   *
   * @param value
   * @return
   */
  public static byte[] getBytes(char value) {
    return getBytes(value, 2);
  }

  /**
   *  get a big-endian byte array with length of 2
   *
   * @param value
   * @return
   */
  public static byte[] getBytes(short value) {
    return getBytes(value, 2);
  }

  /**
   * get a big-endian byte array with length of 4
   *
   * @param value
   * @return
   */
  public static byte[] getBytes(int value) {
    return getBytes(value, 4);
  }

  /**
   * get a big-endian byte array with length of 8
   *
   * @param value
   * @return
   */
  public static byte[] getBytes(long value) {
    return getBytes(value, 8);
  }


  /**
   * get a big-endian byte array with length of size
   *
   * @param value
   * @return
   */
  private static byte[] getBytes(long value, int size) {
    byte[] result = new byte[size];
    for (int i = size-1; i >= 0; i--) {
      result[i] = (byte) (value & 0xffL);
      value >>= 8;
    }
    return result;
  }

  public static int unsigned(byte b) {
    return b < 0 ? b + 256 : b;
  }

}
