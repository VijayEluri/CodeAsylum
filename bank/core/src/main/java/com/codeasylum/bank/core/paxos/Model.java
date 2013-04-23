package com.codeasylum.bank.core.paxos;

import java.io.Serializable;

public abstract class Model implements Serializable {

  private SequenceNumber sequenceNumber;

  public SequenceNumber getSequenceNumber () {

    return sequenceNumber;
  }
}
