package com.codeasylum.bank.core.topology;

import java.io.Serializable;

public interface Partitioner extends Serializable {

  long getToken (Key key);
}
