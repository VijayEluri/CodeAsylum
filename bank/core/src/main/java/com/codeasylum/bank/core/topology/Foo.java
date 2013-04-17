package com.codeasylum.bank.core.topology;

import java.math.BigInteger;

public class Foo {

  public static void main (String... args) {

    Topology topology = new Topology(new SHA3Partitioner(), 256);

    for (int loop = 0; loop < 40; loop++) {

      System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      topology.join();
      int index = 0;

      for (Node node : topology.getNodes()) {

        BigInteger t = BigInteger.ZERO;

        System.out.println(index++ + ":" + node.getRange().getSegments().length + "----------------------------------------------");

        for (Segment segment : node.getRange().getSegments()) {
          t = t.add(BigInteger.valueOf(segment.getEnd() - segment.getStart()));
        }

        System.out.println("Total:" + t);
      }
    }
  }
}
