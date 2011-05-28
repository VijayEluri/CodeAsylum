/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
 * 
 * This file is part of the SmallMind Code Project.
 * 
 * The SmallMind Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The SmallMind Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The SmallMind Code Project. If not, see
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

public class LoopTask extends AbstractTask implements SingularContainer {

  private Task task;
  private Attribute<Integer> sizeAttribute = new Attribute<Integer>(Integer.class, "0");
  private String key;

  public LoopTask () {

  }

  private LoopTask (LoopTask loopTask) {

    super(loopTask);

    task = (loopTask.getTask() == null) ? null : loopTask.getTask().deepCopy();
    sizeAttribute = new Attribute<Integer>(Integer.class, loopTask.getSizeAttribute());
    key = loopTask.getKey();
  }

  public Task getTask () {

    return task;
  }

  public void setTask (Task task) {

    this.task = task;
  }

  public Attribute<Integer> getSizeAttribute () {

    return sizeAttribute;
  }

  public void setSizeAttribute (Attribute<Integer> sizeAttribute) {

    this.sizeAttribute = sizeAttribute;
  }

  public String getKey () {

    return key;
  }

  public void setKey (String key) {

    this.key = key;
  }

  @Override
  public void execute (String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws Exception {

    if (isEnabled() && ouroboros.isEnabled()) {
      if ((task != null) && task.isEnabled()) {

        int size;

        if ((size = sizeAttribute.get(this)) == 0) {
          throw new TaskExecutionException("The %s(%s) has been configured to iterate zero times (i.e. not at all)", LoopTask.class.getSimpleName(), getName());
        }
        else {
          for (int count = 0; count < size; count++) {
            if (!ouroboros.isEnabled()) {
              break;
            }
            else {
              if ((key != null) && (key.length() > 0)) {
                PropertyContext.put(key, String.valueOf(count));
              }

              task.deepCopy().execute(hostId, ouroboros, exchangeTransport);
            }
          }
        }
      }
    }
  }

  @Override
  public Task deepCopy () {

    return new LoopTask(this);
  }
}
