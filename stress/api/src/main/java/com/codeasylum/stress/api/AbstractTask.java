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

import java.util.UUID;

public abstract class AbstractTask implements Task {

  private final UUID uuid = UUID.randomUUID();

  private String name;
  private boolean enabled = true;

  public AbstractTask () {

  }

  public AbstractTask (Task task) {

    name = task.getName();
    enabled = task.isEnabled();
  }

  @Override
  public UUID getUUID () {

    return uuid;
  }

  @Override
  public String getName () {

    return name;
  }

  @Override
  public void setName (String name) {

    this.name = name;
  }

  public boolean isEnabled () {

    return enabled;
  }

  public void setEnabled (boolean enabled) {

    this.enabled = enabled;
  }
}
