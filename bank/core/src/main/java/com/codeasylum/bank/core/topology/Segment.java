package com.codeasylum.bank.core.topology;

import java.math.BigInteger;

public class Segment {

  private final long start;
  private final long end;
  private final int generation;

  public Segment (long start, long end) {

    this.start = start;
    this.end = end;

    generation = 0;
  }

  private Segment (long start, long end, int generation) {

    this.start = start;
    this.end = end;

    this.generation = generation + 1;
  }

  public long getStart () {

    return start;
  }

  public long getEnd () {

    return end;
  }

  public int getGeneration () {

    return generation;
  }

  public Segment[] split () {

    long breadth = BigInteger.valueOf(end).subtract(BigInteger.valueOf(start)).divide(BigInteger.valueOf(2)).longValue();

    return new Segment[] {new Segment(start, start + breadth, generation), new Segment(start + breadth, end, generation)};
  }

  @Override
  public String toString () {

    return new StringBuilder("Segment[start=").append(start).append(", end=").append(end).append(", generation=").append(generation).append(", breadth=").append(end - start).append(']').toString();
  }
}
