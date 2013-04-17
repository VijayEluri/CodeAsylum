package com.codeasylum.bank.core.topology;

import java.util.Arrays;
import java.util.LinkedList;

public class Topology {

  private final int numberOfSegments;
  private final LinkedList<Node> nodeList;
  private final Partitioner partitioner;
  private final long inception;

  public Topology (Partitioner partitioner, int numberOfSegments) {

    this.partitioner = partitioner;
    this.numberOfSegments = numberOfSegments;

    nodeList = new LinkedList<>();

    inception = System.currentTimeMillis();
  }

  public synchronized Node[] getNodes () {

    Node[] nodes = new Node[nodeList.size()];

    nodeList.toArray(nodes);

    return nodes;
  }

  public synchronized void join () {

    if (nodeList.size() == 0) {
      nodeList.add(new Node(new Range(numberOfSegments)));
    }
    else {

      Segment[] stolenSegments;
      LinkedList<Segment> stolenSegmentList = new LinkedList<>();
      int stolenSegmentCount = numberOfSegments / nodeList.size();
      int oddSegmentCount = numberOfSegments % nodeList.size();

      for (Node node : nodeList) {
        stolenSegmentList.addAll(Arrays.asList(node.split(stolenSegmentCount + ((oddSegmentCount-- > 0) ? 1 : 0))));
      }

      stolenSegments = new Segment[stolenSegmentList.size()];
      stolenSegmentList.toArray(stolenSegments);

      nodeList.add(new Node(new Range(stolenSegments)));
    }
  }
}
