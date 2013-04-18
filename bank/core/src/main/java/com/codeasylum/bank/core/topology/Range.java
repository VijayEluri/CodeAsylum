package com.codeasylum.bank.core.topology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Range {

  private final Segment[] segments;
  private transient TreeMap<Integer, TreeSet<Segment>> generationalMap;

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

    reconstructGenerationalMap();
  }

  public Range (Segment[] segments) {

    this.segments = segments;

    reconstructGenerationalMap();
  }

  private void reconstructGenerationalMap () {

    generationalMap = new TreeMap<>();

    for (Segment segment : segments) {

      TreeSet<Segment> segmentSet;

      if ((segmentSet = generationalMap.get(segment.getStage())) == null) {
        generationalMap.put(segment.getStage(), segmentSet = new TreeSet<>(OrdinalSegmentComparator.instance()));
      }
      segmentSet.add(segment);
    }
  }

  public synchronized Segment[] getSegments () {

    return segments;
  }

  public synchronized Segment splitOldestSegment () {

    Segment oldestSegment = generationalMap.firstEntry().getValue().pollFirst();
    Segment[] splitSegments = oldestSegment.split();

    if (generationalMap.firstEntry().getValue().isEmpty()) {
      generationalMap.pollFirstEntry();
    }

    for (int index = 0; index < segments.length; index++) {
      if (segments[index] == oldestSegment) {

        TreeSet<Segment> segmentSet;

        segments[index] = splitSegments[0];

        if ((segmentSet = generationalMap.get(segments[index].getStage())) == null) {
          generationalMap.put(segments[index].getStage(), segmentSet = new TreeSet<>(OrdinalSegmentComparator.instance()));
        }
        segmentSet.add(segments[index]);

        return splitSegments[1];
      }
    }

    throw new IllegalStateException("Generational map is out of sync with the current segments");
  }

  public synchronized Generation getOldestGeneration () {

    if (generationalMap.isEmpty()) {

      return null;
    }

    Map.Entry<Integer, TreeSet<Segment>> oldestEntry = generationalMap.firstEntry();

    return new Generation(oldestEntry.getKey(), oldestEntry.getValue());
  }

  private void writeObject (ObjectOutputStream objectOutputStream)
    throws IOException {

    objectOutputStream.defaultWriteObject();
  }

  private void readObject (ObjectInputStream objectInputStream)
    throws IOException, ClassNotFoundException {

    objectInputStream.defaultReadObject();
    reconstructGenerationalMap();
  }
}
