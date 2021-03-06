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
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with the CodeAsylum Code Project. If not, see
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import com.codeasylum.bank.core.Key;

public class Circle implements Serializable {

  private final LinkedList<Node> nodes = new LinkedList<>();
  private final Tokenizer tokenizer;
  private final int segmentation;
  private transient TreeMap<Long, Node> circumference;

  public Circle (Tokenizer tokenizer, int segmentation) {

    this.tokenizer = tokenizer;
    this.segmentation = segmentation;

    loadCircumference();
  }

  private void loadCircumference () {

    circumference = new TreeMap<>();
    for (Node node : nodes) {
      for (long token : node.getTokens()) {
        circumference.put(token, node);
      }
    }
  }

  public synchronized Node join () {

    do {

      String identity = UUID.randomUUID().toString();
      boolean collision = false;

      for (Node node : nodes) {
        if (identity.equals(node.getIdentity())) {
          collision = true;
          break;
        }
      }

      if (!collision) {

        Node node;
        long[] tokens = new long[segmentation];

        for (int segment = 0; segment < segmentation; segment++) {
          if (circumference.containsKey(tokens[segment] = tokenizer.toLong(new IdentityKey(identity, segment)))) {
            collision = true;
            break;
          }
        }

        if (!collision) {
          nodes.add(node = new Node(identity, tokens));
          for (long token : tokens) {
            circumference.put(token, node);
          }

          return node;
        }
      }
    } while (true);
  }

  // TODO: Will be useful later
  public synchronized void foo () {

    HashMap<Node, BigInteger> spaceMap = new HashMap<>();
    Map.Entry<Long, Node> currentEntry = circumference.lastEntry();
    BigInteger sum = BigInteger.ZERO;
    long mark = Long.MAX_VALUE;

    while (currentEntry != null) {

      BigInteger total;

      if ((total = spaceMap.get(currentEntry.getValue())) == null) {
        total = BigInteger.ZERO;
      }

      spaceMap.put(currentEntry.getValue(), total.add(BigInteger.valueOf(mark).subtract(BigInteger.valueOf(currentEntry.getKey()))));
      currentEntry = circumference.lowerEntry(mark = currentEntry.getKey());
    }

    if (!circumference.isEmpty()) {
      spaceMap.put(circumference.lastEntry().getValue(), spaceMap.get(circumference.lastEntry().getValue()).add(BigInteger.ONE).add(BigInteger.valueOf(mark).subtract(BigInteger.valueOf(Long.MIN_VALUE))));
    }

    for (Map.Entry<Node, BigInteger> spaceEntry : spaceMap.entrySet()) {
      sum = sum.add(spaceEntry.getValue());
    }
    for (Map.Entry<Node, BigInteger> spaceEntry : spaceMap.entrySet()) {
      System.out.println(spaceEntry.getKey().getIdentity() + ":" + spaceEntry.getValue() + ":" + new BigDecimal(spaceEntry.getValue()).divide(new BigDecimal(sum)));
    }
    System.out.println("N=" + nodes.size() + " Total=" + sum);
  }

  public synchronized boolean remove (String identity)
    throws TopologyException {

    Iterator<Node> nodeIter = nodes.iterator();

    while (nodeIter.hasNext()) {
      if (nodeIter.next().getIdentity().equals(identity)) {

        Iterator<Map.Entry<Long, Node>> entryIter = circumference.entrySet().iterator();

        nodeIter.remove();
        while (entryIter.hasNext()) {
          if (entryIter.next().getValue().getIdentity().equals(identity)) {
            entryIter.remove();
          }
        }

        return true;
      }
    }

    return false;
  }

  public synchronized int size () {

    return nodes.size();
  }

  public synchronized Node get (Key key) {

    Map.Entry<Long, Node> tokenEntry;

    if ((tokenEntry = circumference.floorEntry(tokenizer.toLong(key))) == null) {
      if ((tokenEntry = circumference.lastEntry()) == null) {

        return null;
      }
    }

    return tokenEntry.getValue();
  }

  private void writeObject (ObjectOutputStream out)
    throws IOException {

    out.defaultWriteObject();
  }

  private void readObject (ObjectInputStream in)
    throws IOException, ClassNotFoundException {

    in.defaultReadObject();
    loadCircumference();
  }

  private class IdentityKey implements Key {

    private String identity;
    private int segment;

    public IdentityKey (String identity, int segment) {

      this.identity = identity;
      this.segment = segment;
    }

    @Override
    public byte[] asBytes () {

      return (identity + segment).getBytes();
    }
  }
}
