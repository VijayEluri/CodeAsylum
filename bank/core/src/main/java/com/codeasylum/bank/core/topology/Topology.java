package com.codeasylum.bank.core.topology;

public class Topology {

  private final long inception;
  private Circle circle;
  private String name;

  public Topology (String name, Partitioner partitioner, int segmentation) {

    this.name = name;

    inception = System.currentTimeMillis();
    circle = new Circle(partitioner, segmentation);
  }

  public String getName () {

    return name;
  }

  public long getInception () {

    return inception;
  }

  public Circle getCircle () {

    return circle;
  }
}
