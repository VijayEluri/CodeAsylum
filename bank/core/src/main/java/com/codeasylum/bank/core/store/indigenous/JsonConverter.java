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
import java.util.LinkedList;
import com.codeasylum.bank.core.ProcessException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;

public class JsonConverter extends Converter {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();
  private final JsonParser parser;
  private final LinkedList<String> path = new LinkedList<>();
  private Column<?> nextColumn;

  public JsonConverter (String json)
    throws IOException, ProcessException {

    try {
      parser = JSON_FACTORY.createParser(json);
    }
    catch (IOException ioException) {
      throw new ProcessException(ioException);
    }

    nextColumn = findNext();
  }

  @Override
  public boolean hasNext () {

    return nextColumn != null;
  }

  @Override
  public Column<?> next () {

    Column<?> column = nextColumn;

    try {
      nextColumn = findNext();
    }
    catch (Exception exception) {
      throw new RuntimeException(exception);
    }

    return column;
  }

  private Column<?> findNext ()
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

        path.addLast(name);
        System.out.println(path.toString());
        break;
      case START_OBJECT:
        if ((!path.isEmpty()) && (path.getLast().equals("array"))) {
          path.addLast("object");
          System.out.println(path.toString());
        }
        break;
      case START_ARRAY:
        path.addLast("array");
        System.out.println(path.toString());
        break;
      case END_OBJECT:
        if (!path.isEmpty()) {
          path.removeLast();
        }
        System.out.println(path.toString());
        break;
      case END_ARRAY:
        path.removeLast();
        if ((!path.isEmpty()) && (!path.getLast().equals("array"))) {
          path.removeLast();
        }
        System.out.println(path.toString());
        break;
      case VALUE_STRING:
        try {
          return new Column<String>(concatenatePath(), parser.getValueAsString());
        }
        finally {
          path.removeLast();
        }
      case VALUE_NUMBER_INT:
        try {
          return new Column<Long>(concatenatePath(), parser.getValueAsLong());
        }
        finally {
          path.removeLast();
        }
      case VALUE_NUMBER_FLOAT:
        try {
          return new Column<Double>(concatenatePath(), parser.getValueAsDouble());
        }
        finally {
          path.removeLast();
        }
      case VALUE_TRUE:
        try {
          return new Column<Boolean>(concatenatePath(), true);
        }
        finally {
          path.removeLast();
        }
      case VALUE_FALSE:
        try {
          return new Column<Boolean>(concatenatePath(), false);
        }
        finally {
          path.removeLast();
        }
      case VALUE_NULL:
        try {
          return new Column<Void>(concatenatePath(), null);
        }
        finally {
          path.removeLast();
        }
      default:
        throw new UnknownSwitchCaseException(token.name());
    }

    return null;
  }

  private String concatenatePath () {

    StringBuilder pathBuilder = new StringBuilder();
    boolean first = true;

    for (String pathComponent : path) {
      if (!first) {
        pathBuilder.append('.');
      }
      first = false;

      pathBuilder.append(pathComponent);
    }

    return pathBuilder.toString();
  }
}
