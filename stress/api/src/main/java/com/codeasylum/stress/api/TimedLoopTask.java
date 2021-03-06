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

import java.util.concurrent.TimeUnit;

public class TimedLoopTask extends AbstractTask implements SingularContainer {

  private Task task;
  private TimeUnit loopTimeUnit = TimeUnit.MILLISECONDS;
  private Attribute<Integer> loopTimeAttribute = new Attribute<Integer>(Integer.class, "0");
  private String key;

  public TimedLoopTask () {

  }

  public TimedLoopTask (TimedLoopTask timedLoopTask) {

    super(timedLoopTask);

    task = (timedLoopTask.getTask() == null) ? null : timedLoopTask.getTask().deepCopy();
    loopTimeAttribute = new Attribute<Integer>(Integer.class, timedLoopTask.getLoopTimeAttribute());
    loopTimeUnit = timedLoopTask.getLoopTimeUnit();
    key = timedLoopTask.getKey();
  }

  public Task getTask () {

    return task;
  }

  public void setTask (Task task) {

    this.task = task;
  }

  public Attribute<Integer> getLoopTimeAttribute () {

    return loopTimeAttribute;
  }

  public void setLoopTimeAttribute (Attribute<Integer> loopTimeAttribute) {

    this.loopTimeAttribute = loopTimeAttribute;
  }

  public TimeUnit getLoopTimeUnit () {

    return loopTimeUnit;
  }

  public void setLoopTimeUnit (TimeUnit loopTimeUnit) {

    this.loopTimeUnit = loopTimeUnit;
  }

  public String getKey () {

    return key;
  }

  public void setKey (String key) {

    this.key = key;
  }

  @Override
  public void execute (long timeDifferential, int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws Exception {

    if (isEnabled() && ouroboros.isEnabled()) {
      if ((task != null) && task.isEnabled()) {

        long startTime = System.currentTimeMillis();
        int count = 0;

        while (System.currentTimeMillis() - startTime <= loopTimeUnit.toMillis(loopTimeAttribute.get(this))) {
          if (!ouroboros.isEnabled()) {
            break;
          }
          else {
            if ((key != null) && (key.length() > 0)) {
              PropertyContext.put(key, String.valueOf(count++));
            }

            task.deepCopy().execute(timeDifferential, hostIndex, hostId, ouroboros, exchangeTransport);
          }
        }
      }
    }
  }

  @Override
  public Task deepCopy () {

    return new TimedLoopTask(this);
  }
}
