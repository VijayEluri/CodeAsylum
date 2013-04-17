package com.codeasylum.bank.core.topology;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Range {

  private static final Comparator<Segment> GENERATION_COMPARATOR = new Comparator<Segment>() {

    @Override
    public int compare (Segment segment1, Segment segment2) {

      return segment1.getGeneration() - segment2.getGeneration();
    }
  };
  private final Segment[] segments;

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

  public Range (Segment[] segments) {

    this.segments = segments;
  }

  public Segment[] getSegments () {

    return segments;
  }

  public Segment[] split (int stolenSegmentCount) {

    Segment[] stolenSegments = new Segment[stolenSegmentCount];
    LinkedList<Segment> segmentList = new LinkedList<>(Arrays.asList(segments));

    Collections.sort(segmentList, GENERATION_COMPARATOR);
    for (int count = 0; count < stolenSegmentCount; count++) {

      Segment[] splitSegments;
      Segment youngSegment = segmentList.removeFirst();
      int index = 0;

      for (Segment currentSegment : segments) {
        if (currentSegment == youngSegment) {
          break;
        }

        index++;
      }

      splitSegments = youngSegment.split();
      segments[index] = splitSegments[0];
      stolenSegments[count] = splitSegments[1];
    }

    return stolenSegments;
  }
}
