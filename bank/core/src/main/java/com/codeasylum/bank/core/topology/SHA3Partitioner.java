package com.codeasylum.bank.core.topology;

import org.smallmind.nutsnbolts.util.Bytes;

public class SHA3Partitioner implements Partitioner {

  private SHA3 sha3;

  public SHA3Partitioner () {

    sha3 = new SHA3(1536, 64, 64);
  }

  @Override
  public long getToken (Key key) {

    return Bytes.getLong(sha3.digest(key.getBytes()));
  }
}
