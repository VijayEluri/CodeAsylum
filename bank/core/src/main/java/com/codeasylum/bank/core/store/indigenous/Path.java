/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011 David Berkman
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Path implements Iterable<Field> {

  private final Field[] fields;
  private final int repetitionLevel;
  private final int definitionLevel;

  public Path (List<Field> fieldList, RepetitionTracker repetitionTracker) {

    boolean mightRepeat = !repetitionTracker.isEmpty();
    int repeated = 0;
    int defined = 0;
    int index = 0;

    fields = new Field[fieldList.size()];

    for (Field field : fieldList) {
      if (field.isOptional() || field.isRepeated()) {
        defined++;
      }
      if (mightRepeat) {
        if (field.isRepeated()) {
          repeated++;
        }
        if (field.equals(repetitionTracker.getLast())) {
          mightRepeat = false;
        }
      }
      fields[index++] = field;
    }

    repetitionLevel = repeated;
    definitionLevel = defined;
  }

  public int size () {

    return fields.length;
  }

  public Field[] getFields () {

    return fields;
  }

  public int getRepetitionLevel () {

    return repetitionLevel;
  }

  public int getDefinitionLevel () {

    return definitionLevel;
  }

  @Override
  public Iterator<Field> iterator () {

    return new FieldIterator();
  }

  @Override
  public String toString () {

    StringBuilder pathBuilder = new StringBuilder(Path.class.getSimpleName()).append('[');
    boolean first = true;

    for (Field field : fields) {
      if (!first) {
        pathBuilder.append(',');
      }
      first = false;

      pathBuilder.append(field);
    }

    return pathBuilder.append(']').toString();
  }

  @Override
  public int hashCode () {

    return Arrays.hashCode(fields);
  }

  @Override
  public boolean equals (Object obj) {

    return (obj instanceof Path) && Arrays.equals(getFields(), ((Path)obj).getFields());
  }

  public class FieldIterator implements Iterator<Field> {

    int index = 0;

    @Override
    public boolean hasNext () {

      return index < fields.length;
    }

    @Override
    public Field next () {

      return fields[index++];
    }

    @Override
    public void remove () {

      throw new UnsupportedOperationException();
    }
  }
}
