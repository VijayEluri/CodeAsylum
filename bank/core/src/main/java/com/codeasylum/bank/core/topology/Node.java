package com.codeasylum.bank.core.topology;

import java.util.UUID;

public class Node {

  private final Range range;
  private final String identity;

  public Node (Range range) {

    this.range = range;

    identity = UUID.randomUUID().toString();
  }

  public String getIdentity () {

    return identity;
  }

  public Range getRange () {

    return range;
  }
}
