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

public class JsonDisassembler extends Disassembler {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();
  private final JsonParser parser;
  private final HashMap<PathKey, Field> fieldMap = new HashMap<>();
  private LinkedList<Field> fieldList = new LinkedList<>();
  private LinkedList<Field> arrayFieldList = new LinkedList<>();
  private Record<?> nextRecord;
  private JsonToken lastToken;

  public JsonDisassembler (String json)
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

    while ((token = parser.nextToken()) != null) {
      try {
        switch (token) {
          case NOT_AVAILABLE:
            throw new ProcessException("Encountered illegal token(%s)", token.name());
          case VALUE_EMBEDDED_OBJECT:
            throw new ProcessException("Encountered illegal token(%s)", token.name());
          case FIELD_NAME:

            String name = parser.getCurrentName();

            if (name.indexOf('.') >= 0) {
              throw new ProcessException("Use of reserved character '.' within field name(%s)", name);
            }

            fieldList.add(getField(name));
            break;
          case START_OBJECT:
            if (fieldList.isEmpty()) {
              fieldList.add(Field.root());
            }
            else {
              fieldList.getLast().setGroup(true);
            }
            break;
          case START_ARRAY:
            if (fieldList.isEmpty() || (lastToken.equals(JsonToken.START_ARRAY))) {
              fieldList.add(getField("array").setRepeated(true));
            }
            else {
              fieldList.getLast().setRepeated(true);
            }
            break;
          case END_OBJECT:
            closeField();
            break;
          case END_ARRAY:
            if ((!arrayFieldList.isEmpty()) && (arrayFieldList.getLast().equals(fieldList.removeLast()))) {
              arrayFieldList.removeLast();
            }
            break;
          case VALUE_STRING:
            return new Record<String>(new Path(fieldList), parser.getValueAsString(), getRepetitionLevel(), getDefinitionLevel());
          case VALUE_NUMBER_INT:
            return new Record<Long>(new Path(fieldList), parser.getValueAsLong(), getRepetitionLevel(), getDefinitionLevel());
          case VALUE_NUMBER_FLOAT:
            return new Record<Double>(new Path(fieldList), parser.getValueAsDouble(), getRepetitionLevel(), getDefinitionLevel());
          case VALUE_TRUE:
            return new Record<Boolean>(new Path(fieldList), true, getRepetitionLevel(), getDefinitionLevel());
          case VALUE_FALSE:
            return new Record<Boolean>(new Path(fieldList), false, getRepetitionLevel(), getDefinitionLevel());
          case VALUE_NULL:
            return new Record<Void>(new Path(fieldList), null, getRepetitionLevel(), getDefinitionLevel());
          default:
            throw new UnknownSwitchCaseException(token.name());
        }
      }
      finally {
        lastToken = token;
        if (token.isScalarValue()) {
          closeField();
        }
      }
    }

    return null;
  }

  private void closeField () {

    if (!fieldList.getLast().isRepeated()) {
      fieldList.removeLast();
    }
    else if (arrayFieldList.isEmpty() || (!arrayFieldList.getLast().equals(fieldList.getLast()))) {
      arrayFieldList.add(fieldList.getLast());
    }
  }

  private int getRepetitionLevel () {

    int repetitionLevel = 0;

    if (!arrayFieldList.isEmpty()) {
      for (Field field : fieldList) {
        if (field.isRepeated()) {
          repetitionLevel++;
        }
        if (field.equals(arrayFieldList.getLast())) {
          break;
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
      fieldMap.put(pathKey, field = new Field(nextId(), name).setOptional(true));
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
