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

import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractPluralContainer extends AbstractTask implements PluralContainer {

  public LinkedList<Task> taskList = new LinkedList<Task>();

  public AbstractPluralContainer () {

  }

  public AbstractPluralContainer (AbstractPluralContainer taskMaster) {

    super(taskMaster);

    for (Task task : taskMaster) {
      taskList.add(task.deepCopy());
    }
  }

  public Task getChild (int index) {

    return taskList.get(index);
  }

  public int indexOf (Task task) {

    return taskList.indexOf(task);
  }

  public void add (Task task) {

    taskList.add(task);
  }

  public void add (int index, Task task) {

    taskList.add(index, task);
  }

  public void remove (Task task) {

    taskList.remove(task);
  }

  public int size () {

    return taskList.size();
  }

  @Override
  public Iterator<Task> iterator () {

    return taskList.iterator();
  }
}
