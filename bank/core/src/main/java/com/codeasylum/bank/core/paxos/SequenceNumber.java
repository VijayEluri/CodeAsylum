package com.codeasylum.bank.core.paxos;

import java.io.Serializable;

public class SequenceNumber implements Serializable, Comparable<SequenceNumber> {

  private String identity;
  private int count;

  public SequenceNumber (String identity, int count) {

    this.identity = identity;
    this.count = count;
  }

  public String getIdentity () {

    return identity;
  }

  public int getCount () {

    return count;
  }

  @Override
  public int compareTo (SequenceNumber sequenceNumber) {

    int identityComparison;

    if ((identityComparison = identity.compareTo(sequenceNumber.getIdentity())) != 0) {

      return identityComparison;
    }

    return count - sequenceNumber.getCount();
  }

  @Override
  public int hashCode () {

    return identity.hashCode() ^ count;
  }

  @Override
  public boolean equals (Object obj) {

    return (obj instanceof SequenceNumber) && ((SequenceNumber)obj).getIdentity().equals(identity) && (((SequenceNumber)obj).getCount() == count);
  }
}
