package com.codeasylum.bank.core.topology;

import java.util.LinkedList;
import java.util.TreeSet;

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
      TreeSet<Node> nodeSet = new TreeSet<>(GenerationalNodeComparator.instance());

      for (Node node : nodeList) {
        nodeSet.add(node);
      }

      while (stolenSegmentList.size() < numberOfSegments) {

        Node victimNode = nodeSet.pollFirst();

        stolenSegmentList.add(victimNode.getRange().splitOldestSegment());
        nodeSet.add(victimNode);
      }

      stolenSegments = new Segment[stolenSegmentList.size()];
      stolenSegmentList.toArray(stolenSegments);

      nodeList.add(new Node(new Range(stolenSegments)));
    }
  }
}
