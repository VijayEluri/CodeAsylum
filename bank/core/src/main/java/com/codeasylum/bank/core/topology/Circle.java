package com.codeasylum.bank.core.topology;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Circle {

  private final LinkedList<Node> nodes = new LinkedList<>();
  private final TreeMap<Long, Node> circumference = new TreeMap<>();
  private final Partitioner partitioner;
  private final int segmentation;

  public Circle (Partitioner partitioner, int segmentation) {

    this.partitioner = partitioner;
    this.segmentation = segmentation;
  }

  public synchronized void join (Node node)
    throws TopologyCollisionException {

    LinkedList<Long> tokenList = new LinkedList<>();

    for (int segment = 0; segment < segmentation; segment++) {

      long token;

      if (circumference.containsKey(token = partitioner.getToken(new NodeKey(node, segment)))) {
        throw new TopologyCollisionException("Node(%s) causes a hash collision on attempting a join operation", node.getIdentity());
      }

      tokenList.add(token);
    }

    nodes.add(node);
    for (long token : tokenList) {
      circumference.put(token, node);
    }
  }

  public synchronized void remove (String identity)
    throws TopologyException {

    Iterator<Node> nodeIter;
    Iterator<Map.Entry<Long, Node>> entryIter;
    boolean matched = false;

    nodeIter = nodes.iterator();
    while (nodeIter.hasNext()) {
      if (nodeIter.next().getIdentity().equals(identity)) {
        matched = true;
        nodeIter.remove();
        break;
      }
    }

    if (!matched) {
      throw new TopologyException("Unable top locate Node(%s)", identity);
    }

    entryIter = circumference.entrySet().iterator();
    while (entryIter.hasNext()) {
      if (entryIter.next().getValue().getIdentity().equals(identity)) {
        entryIter.remove();
      }
    }
  }

  public synchronized Node get (Key key) {

    Map.Entry<Long, Node> tokenEntry;
    long token = partitioner.getToken(key);

    if ((tokenEntry = circumference.floorEntry(token)) == null) {
      if ((tokenEntry = circumference.lastEntry()) == null) {

        return null;
      }
    }

    return tokenEntry.getValue();
  }

  public class NodeKey implements Key {

    private Node node;
    private int segment;

    public NodeKey (Node node, int segment) {

      this.node = node;
      this.segment = segment;
    }

    @Override
    public byte[] getBytes () {

      return (node.getIdentity() + segment).getBytes();
    }
  }
}
