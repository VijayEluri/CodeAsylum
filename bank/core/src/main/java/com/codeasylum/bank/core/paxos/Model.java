package com.codeasylum.bank.core.paxos;

import java.io.Serializable;

public abstract class Model implements Serializable {

  private Entry entry;

  public Entry getEntry () {

    return entry;
  }
}
