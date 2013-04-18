package com.codeasylum.bank.core.topology;

import java.util.Collections;
import java.util.SortedSet;

public class Generation {

  private SortedSet<Segment> segmentSet;
  private int stage;

  public Generation (int stage, SortedSet<Segment> segmentSet) {

    this.stage = stage;
    this.segmentSet = Collections.unmodifiableSortedSet(segmentSet);
  }

  public int getStage () {

    return stage;
  }

  public SortedSet<Segment> getSegmentSet () {

    return segmentSet;
  }

  public int size () {

    return segmentSet.size();
  }
}
