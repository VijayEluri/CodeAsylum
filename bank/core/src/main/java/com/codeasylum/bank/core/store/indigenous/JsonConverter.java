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
import java.util.Iterator;
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
  private LinkedList<Field> lastClosedFieldList;
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

        if (name.indexOf('.') >= 0) {
          throw new ProcessException("Use of reserved character '.' within field name(%s)", name);
        }

        fieldList.addLast(getField(name));
        System.out.println(fieldList.toString());
        break;
      case START_OBJECT:
        if (!fieldList.isEmpty()) {
          fieldList.getLast().setGroup(true);
        }
        System.out.println(fieldList.toString());
        break;
      case START_ARRAY:
        if (!fieldList.isEmpty()) {
          fieldList.getLast().setRepeated(true);
        }
        System.out.println(fieldList.toString());
        break;
      case END_OBJECT:
        lastClosedFieldList = new LinkedList<>(fieldList);
        if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
          fieldList.removeLast();
        }
        System.out.println(fieldList.toString());
        break;
      case END_ARRAY:
        lastClosedFieldList = new LinkedList<>(fieldList);
        if (!fieldList.isEmpty()) {
          fieldList.removeLast();
        }
        System.out.println(fieldList.toString());
        break;
      case VALUE_STRING:
        try {
          return new Record<String>(new Path(fieldList), parser.getValueAsString(), getRepetitionLevel(), getDefinitionLevel());
        }
        finally {
          lastClosedFieldList = new LinkedList<>(fieldList);
          if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
            fieldList.removeLast();
          }
        }
      case VALUE_NUMBER_INT:
        try {
          return new Record<Long>(new Path(fieldList), parser.getValueAsLong(), getRepetitionLevel(), getDefinitionLevel());
        }
        finally {
          lastClosedFieldList = new LinkedList<>(fieldList);
          if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
            fieldList.removeLast();
          }
        }
      case VALUE_NUMBER_FLOAT:
        try {
          return new Record<Double>(new Path(fieldList), parser.getValueAsDouble(), getRepetitionLevel(), getDefinitionLevel());
        }
        finally {
          lastClosedFieldList = new LinkedList<>(fieldList);
          if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
            fieldList.removeLast();
          }
        }
      case VALUE_TRUE:
        try {
          return new Record<Boolean>(new Path(fieldList), true, getRepetitionLevel(), getDefinitionLevel());
        }
        finally {
          lastClosedFieldList = new LinkedList<>(fieldList);
          if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
            fieldList.removeLast();
          }
        }
      case VALUE_FALSE:
        try {
          return new Record<Boolean>(new Path(fieldList), false, getRepetitionLevel(), getDefinitionLevel());
        }
        finally {
          lastClosedFieldList = new LinkedList<>(fieldList);
          if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
            fieldList.removeLast();
          }
        }
      case VALUE_NULL:
        try {
          return new Record<Void>(new Path(fieldList), null, getRepetitionLevel(), getDefinitionLevel());
        }
        finally {
          lastClosedFieldList = new LinkedList<>(fieldList);
          if (!(fieldList.isEmpty() || fieldList.getLast().isRepeated())) {
            fieldList.removeLast();
          }
        }
      default:
        throw new UnknownSwitchCaseException(token.name());
    }

    return null;
  }

  private int getRepetitionLevel () {

    int repetitionLevel = 0;

    if (lastClosedFieldList != null) {

      Iterator<Field> currentIter = fieldList.iterator();
      Iterator<Field> lastIter = lastClosedFieldList.iterator();
      Field field;

      while (currentIter.hasNext() && lastIter.hasNext()) {
        if ((currentIter.next().equals(field = lastIter.next())) && field.isRepeated()) {
          repetitionLevel++;
        }
      }
    }

    return repetitionLevel;
  }

  private int getDefinitionLevel () {

    int definitionLevel = 0;

    for (Field field : fieldList) {
      if (field.isOptional() || field.isRepeated()) {
        definitionLevel++;
      }
    }

    return definitionLevel;
  }

  private Field getField (String name)
    throws ProcessException {

    PathKey pathKey;
    Field field;

    if ((field = fieldMap.get(pathKey = new PathKey(new Path(fieldList), name))) == null) {
      fieldMap.put(pathKey, field = new Field(counter++, name));
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
