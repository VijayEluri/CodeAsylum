package com.codeasylum.bank.core.topology;

public class Foo {

  public static void main (String... args)
    throws Exception {

    Topology topology = new Topology("test", new SHA3Partitioner(), 2);

    for (int loop = 0; loop < 20; loop++) {

      System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      topology.join(new Node());
    }
  }
}
