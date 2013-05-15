package com.codeasylum.bank.core.store.indigenous;

import java.io.IOException;
import java.util.LinkedList;
import com.codeasylum.bank.core.ProcessException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class RecordShredder {

  public static void main (String... args)
    throws ProcessException {

    // [NOT_AVAILABLE, START_OBJECT, END_OBJECT, START_ARRAY, END_ARRAY, FIELD_NAME, VALUE_EMBEDDED_OBJECT, VALUE_STRING, VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT, VALUE_TRUE, VALUE_FALSE, VALUE_NULL]

    JsonFactory jsonFactory = new JsonFactory();
    JsonParser parser;
    JsonToken token;
    LinkedList<String> path = new LinkedList<>();

    try {
      parser = jsonFactory.createParser("{\"content\": {\n" +
        "    \"members\": [\n" +
        "        {\n" +
        "            \"id\": 708,\n" +
        "            \"targetState\": \"ACTIVE\",\n" +
        "            \"role\": \"Peon\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 21,\n" +
        "            \"targetState\": \"INVITED\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 3206,\n" +
        "            \"targetState\": \"ACTIVE\",\n" +
        "            \"role\": \"Rush Chairman\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 105,\n" +
        "            \"targetState\": \"BANNED\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"override\": false\n" +
        "}}");

      while ((token = parser.nextToken()) != null) {
        switch (token) {
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
            path.removeLast();
            System.out.println(path.toString());
            break;
          case END_ARRAY:
            path.removeLast();
            System.out.println(path.toString());
            break;
          case VALUE_STRING:
            path.removeLast();
            break;
          case VALUE_NUMBER_INT:
            path.removeLast();
            break;
          case VALUE_NUMBER_FLOAT:
            path.removeLast();
            break;
          case VALUE_TRUE:
            path.removeLast();
            break;
          case VALUE_FALSE:
            path.removeLast();
            break;
          case VALUE_NULL:
            path.removeLast();
            break;
          default:
            System.out.println(token.name());
            //           throw new UnknownSwitchCaseException(token.name());
        }
      }
      parser.close();
    }
    catch (IOException ioException) {
      throw new ProcessException(ioException);
    }
  }
}
