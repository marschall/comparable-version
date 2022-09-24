package com.github.marschall.comparableversion;

/**
 * A version made up of up to 3 integer components separated by {@code '.'} and an optional
 * final one preceeded by {@code '-'}.
 * <p>
 * Trailing 0 elements will be ignored when comparing but retained for {@link #toString()}.
 */
public final class ComparableVersion implements Comparable<ComparableVersion> {

  private static final int MAX_VALUE = Byte.toUnsignedInt((byte) -1);

  private final byte major;
  private final byte minor;
  private final byte micro;
  private final byte patch;
  private final String original;

  /**
   * Constructs a new {@link ComparableVersion}.
   *
   * @param version the version string made up of up to 3 positive integer components separated by {@code '.'}
   *                and an optional final one preceeded by {@code '-'},
   *                not {@code null},
   *                will be retained for {@link #toString()}
   */
  public ComparableVersion(String version) {
    this.original = version;

    int end = version.indexOf('.');
    if (end == -1) {
      this.major = parseByte(version, 0, version.length());
      this.minor = 0;
      this.micro = 0;
      this.patch = 0;
    } else {
      this.major = parseByte(version, 0, end);
      int start = end + 1;
      end = version.indexOf('.', start);
      if (end == -1) {
        this.minor = parseByte(version, start, version.length());
        this.micro = 0;
        this.patch = 0;
      } else {
        this.minor = parseByte(version, start, end);
        start = end + 1;
        end = version.indexOf('.', start);
        if (end != -1) {
          throw new IllegalArgumentException("only three dots supported");
        }
        end = version.indexOf('-', start);
        if (end == -1) {
          this.micro = parseByte(version, start, version.length());
          this.patch = 0;
        } else {
          this.micro = parseByte(version, start, end);
          this.patch = parseByte(version, end + 1, version.length());
        }
      }
    }
  }

  private static byte parseByte(String s, int beginIndex, int endIndex) {
    return toByte(Integer.parseInt(s, beginIndex, endIndex, 10));
  }

  private static byte toByte(int i) {
    if (i < 0) {
      throw new IllegalArgumentException("negative numbers not supported");
    }
    if (i > MAX_VALUE) {
      throw new IllegalArgumentException("version component too large");
    }
    return (byte) i;
  }

  @Override
  public int compareTo(ComparableVersion o) {
    int result = Byte.compareUnsigned(this.major, o.major);
    if (result != 0) {
      return result;
    }
    result = Byte.compareUnsigned(this.minor, o.minor);
    if (result != 0) {
      return result;
    }
    result = Byte.compareUnsigned(this.micro, o.micro);
    if (result != 0) {
      return result;
    }
    return Byte.compareUnsigned(this.patch, o.patch);
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
    return (this.major == other.major)
            && (this.minor == other.minor)
            && (this.micro == other.micro)
            && (this.patch == other.patch);
  }

  @Override
  public int hashCode() {
    return ((((((31 + this.major) * 31) + this.minor) * 31) + this.micro) * 31) + this.patch;
  }

  @Override
  public String toString() {
    return this.original;
  }

}
