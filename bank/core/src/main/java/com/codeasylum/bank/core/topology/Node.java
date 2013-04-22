package com.codeasylum.bank.core.topology;

import java.io.Serializable;

public class Node implements Serializable {

  private final String identity;
  private final long[] tokens;

  public Node (String identity, long[] tokens) {

    this.identity = identity;
    this.tokens = tokens;
  }

  public String getIdentity () {

    return identity;
  }

  public long[] getTokens () {

    return tokens;
  }
}
