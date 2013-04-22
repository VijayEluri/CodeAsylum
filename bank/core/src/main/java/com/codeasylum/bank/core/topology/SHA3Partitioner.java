package com.codeasylum.bank.core.topology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.smallmind.nutsnbolts.util.Bytes;

public class SHA3Partitioner implements Partitioner {

  private transient SHA3 sha3;

  public SHA3Partitioner () {

    loadPartitionerImplementations();
  }

  private void loadPartitionerImplementations () {

    sha3 = new SHA3(1536, 64, 64);
  }

  @Override
  public synchronized long getToken (Key key) {

    try {
      return Bytes.getLong(sha3.digest(key.getBytes()));
    }
    finally {
      sha3.reset();
    }
  }

  private void writeObject (ObjectOutputStream out)
    throws IOException {

    out.defaultWriteObject();
  }

  private void readObject (ObjectInputStream in)
    throws IOException, ClassNotFoundException {

    in.defaultReadObject();
    loadPartitionerImplementations();
  }
}
