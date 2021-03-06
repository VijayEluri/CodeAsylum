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
package com.codeasylum.stress.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PropertyContext {

  private static final InheritableThreadLocal<HashMap<String, Object>> ATTRIBUTE_MAP_LOCAL = new InheritableThreadLocal<HashMap<String, Object>>() {

    @Override
    protected HashMap<String, Object> initialValue () {

      HashMap<String, Object> propertyMap;

      propertyMap = new HashMap<String, Object>();
      for (Map.Entry<Object, Object> systemPropertyEntry : System.getProperties().entrySet()) {
        propertyMap.put(systemPropertyEntry.getKey().toString(), systemPropertyEntry.getValue().toString());
      }

      return propertyMap;
    }

    @Override
    protected HashMap<String, Object> childValue (HashMap<String, Object> parentValue) {

      return new HashMap<String, Object>(parentValue);
    }
  };

  public static Map<String, Object> getMap () {

    return ATTRIBUTE_MAP_LOCAL.get();
  }

  public static boolean valueEquals (String key, String matchingValue) {

    String value;

    if ((value = ATTRIBUTE_MAP_LOCAL.get().get(key).toString()) == null) {

      return "null".equalsIgnoreCase(matchingValue) || "false".equalsIgnoreCase(matchingValue);
    }

    return "?".equalsIgnoreCase(matchingValue) || value.equals(matchingValue);
  }

  public static boolean containsKey (String key) {

    return ATTRIBUTE_MAP_LOCAL.get().containsKey(key);
  }

  public static void put (String key, String value) {

    ATTRIBUTE_MAP_LOCAL.get().put(key, value);
  }

  public static String get (String key) {

    return ATTRIBUTE_MAP_LOCAL.get().get(key).toString();
  }

  public static void remove (String key) {

    ATTRIBUTE_MAP_LOCAL.get().remove(key);
  }

  public static void removeKeysStartingWith (String keyFragment) {

    Iterator<String> keyIter = ATTRIBUTE_MAP_LOCAL.get().keySet().iterator();
    String key;

    while (keyIter.hasNext()) {
      if (((key = keyIter.next()).startsWith(keyFragment)) && ((key.length() == keyFragment.length()) || (key.charAt(keyFragment.length()) == '.'))) {
        keyIter.remove();
      }
    }
  }
}
