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
package com.codeasylum.stress.api.visualization;

import java.util.concurrent.TimeUnit;
import org.smallmind.instrument.Clocks;
import org.smallmind.instrument.Meter;
import org.smallmind.instrument.MeterImpl;
import org.smallmind.instrument.Speedometer;
import org.smallmind.instrument.SpeedometerImpl;

public class MonitorStatistics {

  private Speedometer responseSpeedometer = new SpeedometerImpl(1, TimeUnit.SECONDS, Clocks.EPOCH.getClock());
  private Meter failureMeter = new MeterImpl(1, TimeUnit.SECONDS, Clocks.EPOCH.getClock());

  public synchronized void reset () {

    failureMeter.clear();
    responseSpeedometer.clear();
  }

  public synchronized void incFailureCount () {

    failureMeter.mark();
  }

  public synchronized void addExchange (long responseTime) {

    responseSpeedometer.update(responseTime);
  }

  public synchronized double getAverageResponseTime () {

    return responseSpeedometer.getOneMinuteAvgVelocity();
  }

  public synchronized double getAverageRequestCount () {

    return responseSpeedometer.getOneMinuteAvgRate();
  }

  public synchronized long getTotalFailureCount () {

    return failureMeter.getCount();
  }

  public synchronized double getAverageFailureCount () {

    return failureMeter.getAverageRate();
  }

  public synchronized double getFailurePercentage () {

    long failureCount;

    return ((failureCount = failureMeter.getCount()) * 100D) / (responseSpeedometer.getCount() + failureCount);
  }
}
