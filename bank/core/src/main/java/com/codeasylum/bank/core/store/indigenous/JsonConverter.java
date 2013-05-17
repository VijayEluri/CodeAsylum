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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import com.codeasylum.bank.core.ProcessException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;

public class JsonConverter extends Converter {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();
  private final JsonParser parser;
  private final HashMap<PathKey, Field> fieldMap = new HashMap<>();
  private LinkedList<Field> fieldList = new LinkedList<>();
  private Record<?> nextRecord;
  private int counter = 0;

  public JsonConverter (String json)
    throws IOException, ProcessException {

    try {
      parser = JSON_FACTORY.createParser(json);
    }
    catch (IOException ioException) {
      throw new ProcessException(ioException);
    }

    nextRecord = findNext();
  }

  @Override
  public boolean hasNext () {

    return nextRecord != null;
  }

  @Override
  public Record<?> next () {

    Record<?> record = nextRecord;

    try {
      nextRecord = findNext();
    }
    catch (Exception exception) {
      throw new RuntimeException(exception);
    }

    return record;
  }

  private Record<?> findNext ()
    throws IOException, ProcessException {

    JsonToken token;

    while ((token = parser.nextToken()) != null) switch (token) {
      case NOT_AVAILABLE:
        throw new ProcessException("Encountered illegal token(%s)", token.name());
      case VALUE_EMBEDDED_OBJECT:
        throw new ProcessException("Encountered illegal token(%s)", token.name());
      case FIELD_NAME:

        String name = parser.getCurrentName();

        if (name.equals("array")) {
          throw new ProcessException("Use of reserved word 'array' as a field name");
        }
        if (name.equals("object")) {
          throw new ProcessException("Use of reserved word 'object' as a field name");
        }
        if (name.indexOf('.') >= 0) {
          throw new ProcessException("Use of reserved character '.' within field name(%s)", name);
        }

        fieldList.addLast(getField(name));
        System.out.println(fieldList.toString());
        break;
      case START_OBJECT:
        if ((!fieldList.isEmpty()) && (fieldList.getLast().getName().equals("array"))) {
          fieldList.addLast(getField("object"));
          System.out.println(fieldList.toString());
        }
        break;
      case START_ARRAY:
        fieldList.addLast(getField("array"));
        System.out.println(fieldList.toString());
        break;
      case END_OBJECT:
        if (!fieldList.isEmpty()) {
          fieldList.removeLast();
        }
        System.out.println(fieldList.toString());
        break;
      case END_ARRAY:
        fieldList.removeLast();
        if ((!fieldList.isEmpty()) && (!fieldList.getLast().getName().equals("array"))) {
          fieldList.removeLast();
        }
        System.out.println(fieldList.toString());
        break;
      case VALUE_STRING:
        try {
          return new Record<String>(new Path(fieldList), parser.getValueAsString());
        }
        finally {
          fieldList.removeLast();
        }
      case VALUE_NUMBER_INT:
        try {
          return new Record<Long>(new Path(fieldList), parser.getValueAsLong());
        }
        finally {
          fieldList.removeLast();
        }
      case VALUE_NUMBER_FLOAT:
        try {
          return new Record<Double>(new Path(fieldList), parser.getValueAsDouble());
        }
        finally {
          fieldList.removeLast();
        }
      case VALUE_TRUE:
        try {
          return new Record<Boolean>(new Path(fieldList), true);
        }
        finally {
          fieldList.removeLast();
        }
      case VALUE_FALSE:
        try {
          return new Record<Boolean>(new Path(fieldList), false);
        }
        finally {
          fieldList.removeLast();
        }
      case VALUE_NULL:
        try {
          return new Record<Void>(new Path(fieldList), null);
        }
        finally {
          fieldList.removeLast();
        }
      default:
        throw new UnknownSwitchCaseException(token.name());
    }

    return null;
  }

  private Field getField (String name) {

    PathKey pathKey;
    Field field;

    if ((field = fieldMap.get(pathKey = new PathKey(new Path(fieldList), name))) == null) {
      fieldMap.put(pathKey, field = new Field(counter++, name, true, true));
    }

    return field;
  }

  private class PathKey {

    private Path path;
    private String name;

    private PathKey (Path path, String name) {

      this.path = path;
      this.name = name;
    }

    private Path getPath () {

      return path;
    }

    private String getName () {

      return name;
    }

    @Override
    public int hashCode () {

      return path.hashCode() ^ name.hashCode();
    }

    @Override
    public boolean equals (Object obj) {

      return (obj instanceof PathKey) && ((PathKey)obj).getPath().equals(getPath()) && ((PathKey)obj).getName().equals(getName());
    }
  }
}
