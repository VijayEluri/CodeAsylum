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
package com.codeasylum.bank.core.store.indigenous;

import java.util.HashSet;
import java.util.LinkedList;
import com.codeasylum.bank.core.ProcessException;

public class Schema {

  private Node current;

  public Schema () {

  }

  public boolean isCurrentlyRoot () {

    return current == null;
  }

  public Field getCurrentField () {

    if (current == null) {

      return null;
    }

    return current.getField();
  }

  public Field close () {

    if (current == null) {

      return null;
    }

    Field field = current.getField();

    current = current.getParent();

    return field;
  }

  public boolean closeIfNotRepeated () {

    if (current == null) {

      return true;
    }

    if (!current.getField().isRepeated()) {
      current = current.getParent();

      return true;
    }

    return false;
  }

  public void setCurrentFieldAsGroup ()
    throws ProcessException {

    if (current != null) {
      current.getField().setGroup(true);
    }
  }

  public void setCurrentFieldAsRepeated ()
    throws ProcessException {

    if (current != null) {
      current.getField().setRepeated(true);
    }
  }

  public Path getCurrentPath () {

    LinkedList<Field> fields = new LinkedList<>();

    if (current != null) {

      Node node = current;

      do {
        fields.addFirst(node.getField());
      } while ((node = node.getParent()) != null);
    }

    return new Path(fields);
  }

  public Field addChildField (Field child) {

    if (current == null) {
      current = new Node(child);
    }
    else {
      current = current.addChildField(current, child);
    }

    return current.getField();
  }

  public Field getChildFieldWithName (String name) {

    if (current == null) {

      return null;
    }

    return current.getChildFieldWithName(name);
  }

  private class Node {

    private Field field;
    private Node parent;
    private HashSet<Node> children = new HashSet<>();

    public Node (Field field) {

      this.field = field;
    }

    public Node (Node parent, Field field) {

      this(field);

      this.parent = parent;
    }

    public Node getParent () {

      return parent;
    }

    public Field getField () {

      return field;
    }

    public Node addChildField (Node parent, Field child) {

      Node node;

      children.add(node = new Node(parent, child));

      return node;
    }

    public Field getChildFieldWithName (String name) {

      for (Node child : children) {
        if (child.getField().getName().equals(name)) {

          return child.getField();
        }
      }

      return null;
    }

    @Override
    public int hashCode () {

      return field.hashCode();
    }

    @Override
    public boolean equals (Object obj) {

      return (obj instanceof Node) && ((Node)obj).getField().equals(field);
    }
  }
}
