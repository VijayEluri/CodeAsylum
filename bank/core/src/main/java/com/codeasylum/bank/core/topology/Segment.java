package com.codeasylum.bank.core.topology;

public class Segment {

  private final long start;
  private final long end;
  private int generation;

  public Segment (long start, long end) {

    this.start = start;
    this.end = end;

    generation = 0;
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
}
