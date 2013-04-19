package com.codeasylum.bank.core.topology;

public class Foo {

  public static void main (String... args)
    throws Exception {

    Topology topology = new Topology("test", new SHA3Partitioner(), 256);

    for (int loop = 0; loop < 20; loop++) {

      System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      topology.getCircle().join(new Node());
      topology.getCircle().foo();
    }
  }
}
