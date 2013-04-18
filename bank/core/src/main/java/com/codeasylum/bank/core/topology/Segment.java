package com.codeasylum.bank.core.topology;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class Segment {

  private final long start;
  private final long end;
  private final int stage;
  private final int ordinal;

  public Segment (long start, long end) {

    this(start, end, -1);
  }

  private Segment (long start, long end, int stage) {

    this.start = start;
    this.end = end;

    this.stage = stage + 1;
    ordinal = ThreadLocalRandom.current().nextInt();
  }

  public long getStart () {

    return start;
  }

  public long getEnd () {

    return end;
  }

  public int getStage () {

    return stage;
  }

  public int getOrdinal () {

    return ordinal;
  }

  public Segment[] split () {

    long breadth = BigInteger.valueOf(end).subtract(BigInteger.valueOf(start)).divide(BigInteger.valueOf(2)).longValue();

    return new Segment[] {new Segment(start, start + breadth, stage), new Segment(start + breadth, end, stage)};
  }

  @Override
  public String toString () {

    return new StringBuilder("Segment[start=").append(start).append(", end=").append(end).append(", stage=").append(stage).append(", breadth=").append(end - start).append(']').toString();
  }
}
