package com.codeasylum.bank.core.store.indigenous;

public class RecordShredder {

  private final Converter converter;

  public RecordShredder (Converter converter) {

    this.converter = converter;
  }

  public static void main (String... args)
    throws Exception {

    RecordShredder r = new RecordShredder(new JsonConverter("{\"content\": {\n" +
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
      "}}"));
  }

  public void shred () {

    while (converter.hasNext()) {
      System.out.println(converter.next());
    }
  }
}
