package com.codeasylum.bank.core.topology;

import java.math.BigInteger;

public class Range {

  private final Segment[] segments;

  public Range () {

    this(256);
  }

  public Range (int size) {

    if (size < 2) {
      throw new IllegalArgumentException("A Range must be composed of at least 2 segments");
    }

    long breadth = BigInteger.valueOf(Long.MAX_VALUE).subtract(BigInteger.valueOf(Long.MIN_VALUE)).divide(BigInteger.valueOf(size)).add(BigInteger.ONE).longValue();
    long start = Long.MIN_VALUE;

    segments = new Segment[size];
    for (int index = 0; index < size; index++) {
      segments[index] = new Segment(start, (index == size - 1) ? Long.MAX_VALUE : (start += breadth));
    }
  }
}
