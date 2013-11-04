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
package com.codeasylum.bank.core.store.indigenous;

import com.codeasylum.bank.core.ProcessException;

public class Field {

  private final String name;
  private final int id;
  private Boolean group;
  private Boolean repeated;
  private Boolean optional;

  public Field (int id, String name) {

    this.id = id;
    this.name = name;
  }

  public Field (int id, String name, boolean group, boolean optional, boolean repeated) {

    this(id, name);

    this.group = group;
    this.optional = optional;
    this.repeated = repeated;
  }

  public int getId () {

    return id;
  }

  public String getName () {

    return name;
  }

  public boolean isGroup () {

    return (group == null) ? false : group;
  }

  public Field setGroup (boolean group)
    throws ProcessException {

    if ((this.group != null) && (this.group != group)) {
      throw new ProcessException("Field(%s) has already been marked as a group(%b)", name, this.group);
    }

    this.group = group;

    return this;
  }

  public boolean isRepeated () {

    return (repeated == null) ? false : repeated;
  }

  public Field setRepeated (boolean repeated)
    throws ProcessException {

    if ((this.repeated != null) && (this.repeated != repeated)) {
      throw new ProcessException("Field(%s) has already been marked repeated(%b)", name, this.repeated);
    }

    this.repeated = repeated;

    return this;
  }

  public boolean isOptional () {

    return (optional == null) ? false : optional;
  }

  public Field setOptional (boolean optional)
    throws ProcessException {

    if ((this.optional != null) && (this.optional != optional)) {
      throw new ProcessException("Field(%s) has already been marked optional(%b)", name, this.optional);
    }

    this.optional = optional;

    return this;
  }

  @Override
  public String toString () {

    return new StringBuilder(Field.class.getSimpleName()).append("[id=").append(id).append(", name=").append(name).append(", group=").append(group).append(", repeated=").append(repeated).append(", optional=").append(optional).append("]").toString();
  }

  @Override
  public int hashCode () {

    return id;
  }

  @Override
  public boolean equals (Object obj) {

    return (obj != null) && (obj instanceof Field) && (((Field)obj).getId() == getId());
  }
}
