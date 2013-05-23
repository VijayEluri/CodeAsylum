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

import java.util.List;

public class Path {

  private final String key;
  private final int repetitionLevel;
  private final int definitionLevel;

  public Path (List<Field> fieldList, RepetitionTracker repetitionTracker) {

    StringBuilder keyBuilder = new StringBuilder();
    boolean first = true;
    boolean mightRepeat = !repetitionTracker.isEmpty();
    int repeated = 0;
    int defined = 0;

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

      if (!first) {
        keyBuilder.append('.');
      }
      first = false;
      keyBuilder.append(field.getName());
    }

    key = keyBuilder.toString();
    repetitionLevel = repeated;
    definitionLevel = defined;
  }

  public String getKey () {

    return key;
  }

  public int getRepetitionLevel () {

    return repetitionLevel;
  }

  public int getDefinitionLevel () {

    return definitionLevel;
  }
}
