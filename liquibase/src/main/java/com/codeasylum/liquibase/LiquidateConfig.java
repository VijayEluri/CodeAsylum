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
package com.codeasylum.liquibase;

import org.smallmind.liquibase.spring.Goal;
import org.smallmind.liquibase.spring.Source;

public class LiquidateConfig {

  private Database database;
  private Goal goal;
  private Source source;
  private String changeLog;
  private String host;
  private String schema;
  private String user;
  private String password;
  private int port;

  public Database getDatabase () {

    return database;
  }

  public void setDatabase (Database database) {

    this.database = database;
  }

  public Goal getGoal () {

    return goal;
  }

  public void setGoal (Goal goal) {

    this.goal = goal;
  }

  public Source getSource () {

    return source;
  }

  public void setSource (Source source) {

    this.source = source;
  }

  public String getChangeLog () {

    return changeLog;
  }

  public void setChangeLog (String changeLog) {

    this.changeLog = changeLog;
  }

  public String getHost () {

    return host;
  }

  public void setHost (String host) {

    this.host = host;
  }

  public String getSchema () {

    return schema;
  }

  public void setSchema (String schema) {

    this.schema = schema;
  }

  public String getUser () {

    return user;
  }

  public void setUser (String user) {

    this.user = user;
  }

  public String getPassword () {

    return password;
  }

  public void setPassword (String password) {

    this.password = password;
  }

  public int getPort () {

    return port;
  }

  public void setPort (int port) {

    this.port = port;
  }
}
