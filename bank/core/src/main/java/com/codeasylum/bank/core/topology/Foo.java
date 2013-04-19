package com.codeasylum.bank.core.topology;

import java.util.LinkedList;

public class Foo {

  public static void main (String... args)
    throws Exception {

    Topology topology = new Topology("test", new SHA3Partitioner(), 256);
    LinkedList<Node> nodeList = new LinkedList<>();

    for (int loop = 0; loop < 20; loop++) {

      Node node;

      System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      topology.getCircle().join(node = new Node());
      nodeList.add(node);
      topology.getCircle().foo();
    }

    for (Node node : nodeList) {
      System.out.println("################################################################");
      topology.getCircle().remove(node.getIdentity());
      topology.getCircle().foo();
    }
  }
}
