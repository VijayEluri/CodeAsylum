package com.codeasylum.bank.core.topology;

import java.util.UUID;

public class Node {

  private final String identity;

  public Node () {

    identity = UUID.randomUUID().toString();
  }

  public String getIdentity () {

    return identity;
  }
}
