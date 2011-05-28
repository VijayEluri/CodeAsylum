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

import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DelayTask extends AbstractTask {

  private static final Random RANDOM = new SecureRandom();

  private TimeUnit delayTimeUnit = TimeUnit.MILLISECONDS;
  private Attribute<Integer> staticTimeAttribute = new Attribute<Integer>(Integer.class, "0");
  private Attribute<Integer> stochasticTimeAttribute = new Attribute<Integer>(Integer.class, "0");

  public DelayTask () {

  }

  private DelayTask (DelayTask delayTask) {

    super(delayTask);

    staticTimeAttribute = new Attribute<Integer>(Integer.class, delayTask.getStaticTimeAttribute());
    stochasticTimeAttribute = new Attribute<Integer>(Integer.class, delayTask.getStochasticTimeAttribute());
    delayTimeUnit = delayTask.getDelayTimeUnit();
  }

  public Attribute<Integer> getStaticTimeAttribute () {

    return staticTimeAttribute;
  }

  public void setStaticTimeAttribute (Attribute<Integer> staticTimeAttribute) {

    this.staticTimeAttribute = staticTimeAttribute;
  }

  public Attribute<Integer> getStochasticTimeAttribute () {

    return stochasticTimeAttribute;
  }

  public void setStochasticTimeAttribute (Attribute<Integer> stochasticTimeAttribute) {

    this.stochasticTimeAttribute = stochasticTimeAttribute;
  }

  public TimeUnit getDelayTimeUnit () {

    return delayTimeUnit;
  }

  public void setDelayTimeUnit (TimeUnit delayTimeUnit) {

    this.delayTimeUnit = delayTimeUnit;
  }

  @Override
  public void execute (String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws RemoteException, InterruptedException, ScriptInterpolationException {

    if (isEnabled() && ouroboros.isEnabled()) {

      long totalTime = delayTimeUnit.toMillis(staticTimeAttribute.get(this));
      int stochasticTime;

      if ((stochasticTime = stochasticTimeAttribute.get(this)) > 0) {
        totalTime += delayTimeUnit.toMillis(RANDOM.nextInt(stochasticTime));
      }

      long startTime = System.currentTimeMillis();
      long sleepTime = totalTime;

      do {
        Thread.sleep(Math.min(250, Math.abs(sleepTime)));
      } while (ouroboros.isEnabled() && ((sleepTime = System.currentTimeMillis() - (startTime + totalTime)) < 0));
    }
  }

  @Override
  public Task deepCopy () {

    return new DelayTask(this);
  }
}
