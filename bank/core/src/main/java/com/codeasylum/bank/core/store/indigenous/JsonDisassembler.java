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
import com.codeasylum.bank.core.ProcessException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;

public class JsonDisassembler extends Disassembler {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();
  private final Schema schema = new Schema();
  private final RepetitionTracker repetitionTracker = new RepetitionTracker();
  private final JsonParser parser;
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
  public Schema getSchema () {

    return schema;
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

            schema.addChildField(getField(name));
            break;
          case START_OBJECT:
            schema.setCurrentFieldAsGroup();
            break;
          case START_ARRAY:
            if (schema.isCurrentlyRoot() || (lastToken.equals(JsonToken.START_ARRAY))) {
              schema.addChildField(getField("array").setRepeated(true));
            }
            else {
              schema.setCurrentFieldAsRepeated();
            }
            break;
          case END_OBJECT:
            closeField();
            break;
          case END_ARRAY:
            repetitionTracker.endIfLast(schema.close());
            break;
          case VALUE_STRING:
            return new Record<String>(schema.getCurrentPath(repetitionTracker), parser.getValueAsString());
          case VALUE_NUMBER_INT:
            return new Record<Long>(schema.getCurrentPath(repetitionTracker), parser.getValueAsLong());
          case VALUE_NUMBER_FLOAT:
            return new Record<Double>(schema.getCurrentPath(repetitionTracker), parser.getValueAsDouble());
          case VALUE_TRUE:
            return new Record<Boolean>(schema.getCurrentPath(repetitionTracker), true);
          case VALUE_FALSE:
            return new Record<Boolean>(schema.getCurrentPath(repetitionTracker), false);
          case VALUE_NULL:
            return new Record<Void>(schema.getCurrentPath(repetitionTracker), null);
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

    if (!schema.closeIfNotRepeated()) {
      repetitionTracker.addIfNotLast(schema.getCurrentField());
    }
  }

  private Field getField (String name)
    throws ProcessException {

    Field field;

    if ((field = schema.getChildFieldWithName(name)) == null) {
      field = new Field(nextId(), name).setOptional(true);
    }

    return field;
  }
}
