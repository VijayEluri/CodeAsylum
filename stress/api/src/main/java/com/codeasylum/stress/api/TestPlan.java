/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011, 10212, 2013 David Berkman
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
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with the CodeAsylum Code Project. If not, see
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

import java.io.Serializable;
import java.util.HashMap;

public class TestPlan implements Serializable {

  private transient boolean changed = false;

  private RootTask rootTask;
  private HashMap<String, String> serverMap;
  private String outputPath;
  private int exchangeBufferSize = 1000;
  private int debugBufferSize = 100;
  private int monitorDuration = 5;

  public TestPlan () {

    rootTask = new RootTask();

    serverMap = new HashMap<String, String>();
    serverMap.put("localhost", "localhost");
  }

  public RootTask getRootTask () {

    return rootTask;
  }

  public void setRootTask (RootTask rootTask) {

    this.rootTask = rootTask;
  }

  public HashMap<String, String> getServerMap () {

    return serverMap;
  }

  public void setServerMap (HashMap<String, String> serverMap) {

    this.serverMap = serverMap;
  }

  public String getOutputPath () {

    return outputPath;
  }

  public void setOutputPath (String outputPath) {

    this.outputPath = outputPath;
    setChanged(true);
  }

  public int getExchangeBufferSize () {

    return exchangeBufferSize;
  }

  public void setExchangeBufferSize (int exchangeBufferSize) {

    this.exchangeBufferSize = exchangeBufferSize;
    setChanged(true);
  }

  public int getDebugBufferSize () {

    return debugBufferSize;
  }

  public void setDebugBufferSize (int debugBufferSize) {

    this.debugBufferSize = debugBufferSize;
    setChanged(true);
  }

  public int getMonitorDuration () {

    return monitorDuration;
  }

  public void setMonitorDuration (int monitorDuration) {

    this.monitorDuration = monitorDuration;
    setChanged(true);
  }

  public boolean isChanged () {

    return changed;
  }

  public void setChanged (boolean changed) {

    this.changed = changed;
  }
}
