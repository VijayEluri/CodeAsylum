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
        path.removeLast();
        System.out.println(path.toString());
        break;
      case END_ARRAY:
        path.removeLast();
        System.out.println(path.toString());
        break;
      case VALUE_STRING:
        try {

        }
        finally {
          path.removeLast();
        }

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
        throw new UnknownSwitchCaseException(token.name());
    }

    return null;
  }

}
