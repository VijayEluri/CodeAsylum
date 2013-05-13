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
package com.codeasylum.bank.core.topology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.thoughtworks.xstream.XStream;

public class Topology extends Model {

  private final Circle circle;
  private final String name;
  private final long inception;

  public Topology (String name, Tokenizer tokenizer, int segmentation) {

    this.name = name;

    inception = System.currentTimeMillis();
    circle = new Circle(tokenizer, segmentation);
  }

  public static Topology read (Path path)
    throws IOException {

    Path filePath;

    if (Files.isRegularFile(filePath = path.resolve("topology.xml"))) {
      try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset())) {

        return (Topology)new XStream().fromXML(reader);
      }
    }

    return null;
  }

  public String getName () {

    return name;
  }

  public long getInception () {

    return inception;
  }

  public Circle getCircle () {

    return circle;
  }

  public synchronized void write (Path path)
    throws IOException {

    BufferedWriter writer = Files.newBufferedWriter(path.resolve("topology.xml"), Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    new XStream().toXML(this, writer);
    writer.close();
  }
}
