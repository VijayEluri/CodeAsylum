/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011, 10212, 2013 David Berkman
 * 
 * This file is part of the CodeAsylum Code Project.
 * 
 * The CodeAsylum Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The CodeAsylum Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The CodeAsylum Code Project. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under the GNU Affero GPL version 3 section 7
 * ------------------------------------------------------------------
 * If you modify this Program, or any covered work, by linking or
 * combining it with other code, such other code is not for that reason
 * alone subject to any of the requirements of the GNU Affero GPL
 * version 3.
 */
package com.codeasylum.bank.core.topology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.codeasylum.bank.core.Key;
import org.smallmind.nutsnbolts.util.Bytes;

public class SHA3Tokenizer implements Tokenizer {

  private transient SHA3 sha3;

  public SHA3Tokenizer () {

    loadPartitionerImplementations();
  }

  private void loadPartitionerImplementations () {

    sha3 = new SHA3(1536, 64, 64);
  }

  @Override
  public synchronized long toLong (Key key) {

    try {
      return Bytes.getLong(sha3.digest(key.asBytes()));
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
