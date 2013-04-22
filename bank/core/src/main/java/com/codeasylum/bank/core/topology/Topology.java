package com.codeasylum.bank.core.topology;

import com.codeasylum.bank.core.paxos.Model;

public class Topology extends Model {

  private final Circle circle;
  private final String name;
  private final long inception;

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
