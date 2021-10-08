package com.github.marschall.comparableversion;

import java.util.Arrays;

/**
 * A version made up of integer components separated by {@code '.'}.
 * <p>
 * Trailing 0 elements will be ignored when comparing but retained for {@link #toString()}.
 */
public final class ComparableVersion implements Comparable<ComparableVersion> {

  private static final int MAX_VALUE = Byte.toUnsignedInt((byte) -1);

  private final byte[] components;
  private final String original;

  /**
   * Constructs a new {@link ComparableVersion}
   * 
   * @param version the version string made up of positive integer components separated by {@code '.'},
   *                not {@code null},
   *                will be retained for {@link #toString()}
   */
  public ComparableVersion(String version) {
    this.original = version;
    this.components = parse(version);
  }

  private static byte[] parse(String version) {
    String[] numbers = version.split("\\.");
    byte[] components = new byte[numbers.length];
    int lastNonZero = -1;
    for (int i = 0; i < components.length; i++) {
      int intValue = Integer.parseInt(numbers[i]);
      if (intValue < 0) {
        throw new IllegalArgumentException("negative numbers not supported");
      }
      if (intValue > MAX_VALUE) {
        throw new IllegalArgumentException("version component too large");
      }
      components[i] = (byte) intValue;
      if (intValue != 0) {
        lastNonZero = i;
      }
    }
    if (lastNonZero == -1) {
      return new byte[0];
    } else if (lastNonZero != components.length -1) {
      byte[] canonical = new byte[lastNonZero + 1];
      System.arraycopy(components, 0, canonical, 0, lastNonZero + 1);
      return canonical;
    } else {
      return components;
    }
  }

  @Override
  public int compareTo(ComparableVersion o) {
    if (this.components.length == o.components.length) {
      return Arrays.compareUnsigned(this.components, o.components);
    } else {
      int prefixLength = Math.min(this.components.length, o.components.length);
      int prefixCompare = Arrays.compareUnsigned(this.components, 0, prefixLength, o.components, 0, prefixLength);
      if (prefixCompare != 0) {
        return prefixCompare;
      } else {
        return Integer.compare(this.components.length, o.components.length);
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ComparableVersion)) {
      return false;
    }
    ComparableVersion other = (ComparableVersion) obj;
    return Arrays.equals(this.components, other.components);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.components);
  }

  @Override
  public String toString() {
    return this.original;
  }

}
