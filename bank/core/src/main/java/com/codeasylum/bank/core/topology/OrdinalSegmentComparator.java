package com.codeasylum.bank.core.topology;

import java.util.Comparator;

public class OrdinalSegmentComparator implements Comparator<Segment> {

  private static final OrdinalSegmentComparator INSTANCE = new OrdinalSegmentComparator();

  public static OrdinalSegmentComparator instance () {

    return INSTANCE;
  }

  @Override
  public int compare (Segment segment1, Segment segment2) {

    int ordinalComparison;

    if ((ordinalComparison = segment1.getOrdinal() - segment2.getOrdinal()) != 0) {

      return ordinalComparison;
    }

    return (segment1 == segment2) ? 0 : 1;
  }
}
