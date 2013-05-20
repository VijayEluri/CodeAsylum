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

public class RecordShredder {

  private final Converter converter;

  public RecordShredder (Converter converter) {

    this.converter = converter;
  }

  public static void main (String... args)
    throws Exception {

    String foo = "{\"content\": {\n" +
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
      "}}";

    String bar = "{\n" +
      "    \"docId\": 10,\n" +
      "    \"links\": {\n" +
      "        \"forward\": [\n" +
      "            20,\n" +
      "            40,\n" +
      "            60\n" +
      "        ]\n" +
      "    },\n" +
      "    \"name\": [\n" +
      "        {\n" +
      "            \"language\": [\n" +
      "                {\n" +
      "                    \"code\": \"en-us\",\n" +
      "                    \"country\": \"us\"\n" +
      "                },\n" +
      "                {\n" +
      "                    \"code\": \"en\"\n" +
      "                }\n" +
      "            ],\n" +
      "            \"url\": \"http://A\"\n" +
      "        },\n" +
      "        {\n" +
      "            \"url\": \"http://B\"\n" +
      "        },\n" +
      "        {\n" +
      "            \"language\": [\n" +
      "                {\n" +
      "                    \"code\": \"en-gb\",\n" +
      "                    \"country\": \"gb\"\n" +
      "                }\n" +
      "            ]\n" +
      "        }\n" +
      "    ]\n" +
      "}";

    RecordShredder r = new RecordShredder(new JsonConverter(bar));

    r.shred();
  }

  public void shred () {

    while (converter.hasNext()) {
      System.out.println(converter.next());
    }
  }
}
