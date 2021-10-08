package com.github.marschall.comparableversion;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.jupiter.api.Test;

class CompatibilityTests {

  @Test
  void test() {
    ComparableVersion zeroDotNine = new ComparableVersion("0.9");
    new ComparableVersion("1.0");
    new ComparableVersion("1.0.0");
    new ComparableVersion("1.0.1");
  }

}
