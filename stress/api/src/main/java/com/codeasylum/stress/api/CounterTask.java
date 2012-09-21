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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CounterTask extends AbstractTask {

  private static final ConcurrentHashMap<String, AtomicLong> COUNTER_MAP = new ConcurrentHashMap<String, AtomicLong>();

  private Attribute<Long> startAttribute = new Attribute<Long>(Long.class, "0");
  private String key;

  public CounterTask () {

  }

  private CounterTask (CounterTask counterTask) {

    super(counterTask);

    startAttribute = new Attribute<Long>(Long.class, counterTask.getStartAttribute());
    key = counterTask.getKey();
  }

  public Attribute<Long> getStartAttribute () {

    return startAttribute;
  }

  public void setStartAttribute (Attribute<Long> startAttribute) {

    this.startAttribute = startAttribute;
  }

  public String getKey () {

    return key;
  }

  public void setKey (String key) {

    this.key = key;
  }

  @Override
  public void execute (long timeDifferential, int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws RemoteException, ScriptInterpolationException {

    if (isEnabled() && ouroboros.isEnabled()) {

      AtomicLong counter;

      if ((key == null) || (key.length() == 0)) {
        throw new TaskExecutionException("The %s(%s) has not been configured with a property key (the data read will not be available)", CounterTask.class.getSimpleName(), getName());
      }

      if ((counter = COUNTER_MAP.get(key)) == null) {

        AtomicLong prevCounter;

        if ((prevCounter = COUNTER_MAP.putIfAbsent(key, counter = new AtomicLong(startAttribute.get(this)))) != null) {
          counter = prevCounter;
        }
      }

      PropertyContext.put(key, String.valueOf(counter.getAndIncrement()));
    }
  }

  @Override
  public Task deepCopy () {

    return new CounterTask(this);
  }
}
