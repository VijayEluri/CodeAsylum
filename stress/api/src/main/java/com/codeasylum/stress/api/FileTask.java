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
package com.codeasylum.stress.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FileTask extends AbstractTask {

  private static final HashMap<String, LineSampler> SAMPLER_MAP = new HashMap<String, LineSampler>();

  private Attribute<String> fileAttribute = new Attribute<String>(String.class);
  private String key;
  private boolean homogenized = false;

  public FileTask () {

  }

  private FileTask (FileTask fileTask) {

    super(fileTask);

    fileAttribute = new Attribute<String>(String.class, fileTask.getFileAttribute());
    key = fileTask.getKey();
    homogenized = fileTask.isHomogenized();
  }

  public Attribute<String> getFileAttribute () {

    return fileAttribute;
  }

  public void setFileAttribute (Attribute<String> fileAttribute) {

    this.fileAttribute = fileAttribute;
  }

  public String getKey () {

    return key;
  }

  public void setKey (String key) {

    this.key = key;
  }

  public boolean isHomogenized () {

    return homogenized;
  }

  public void setHomogenized (boolean homogenized) {

    this.homogenized = homogenized;
  }

  @Override
  public void execute (int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws IOException, ScriptInterpolationException {

    if (isEnabled() && ouroboros.isEnabled()) {

      String fileName;

      if ((fileName = fileAttribute.get(this)) == null) {
        throw new TaskExecutionException("The %s(%s) has not been configured with a file", FileTask.class.getSimpleName(), getName());
      }
      if ((key == null) || (key.length() == 0)) {
        throw new TaskExecutionException("The %s(%s) has not been configured with a property key (the data read will not be available)", FileTask.class.getSimpleName(), getName());
      }

      LineSampler lineSampler;

      synchronized (lineSampler = getLineSampler(fileName)) {
        PropertyContext.put(key, lineSampler.getLine());
      }
    }
  }

  private LineSampler getLineSampler (String fileName)
    throws IOException {

    LineSampler lineSampler;

    synchronized (SAMPLER_MAP) {
      if ((lineSampler = SAMPLER_MAP.get(fileName)) == null) {
        SAMPLER_MAP.put(fileName, lineSampler = new LineSampler(fileName, homogenized));
      }
    }

    return lineSampler;
  }

  @Override
  public Task deepCopy () {

    return new FileTask(this);
  }

  private class LineSampler {

    private BufferedReader reader;
    private String fileName;
    private boolean homogenized;

    private LineSampler (String fileName, boolean homogenized)
      throws IOException {

      this.fileName = fileName;
      this.homogenized = homogenized;
    }

    public String getLine ()
      throws IOException {

      if (homogenized) {

        reader = new BufferedReader(new FileReader(fileName));
        StringBuilder contentBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        int charsRead;

        while ((charsRead = reader.read(buffer)) >= 0) {
          contentBuilder.append(buffer, 0, charsRead);
        }
        reader.close();

        return contentBuilder.toString();
      }
      else {

        String singleLine;

        do {
          if (reader == null) {
            reader = new BufferedReader(new FileReader(fileName));
          }

          if ((singleLine = reader.readLine()) == null) {
            reader.close();
            reader = null;
          }
        } while (singleLine == null);

        return singleLine;
      }
    }
  }
}
