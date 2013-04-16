package com.codeasylum.bank.core.topology;

public interface Partitioner {

  long getToken (Key key);
}
