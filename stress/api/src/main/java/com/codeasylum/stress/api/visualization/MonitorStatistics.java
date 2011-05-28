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
package com.codeasylum.stress.api.visualization;

public class MonitorStatistics {

  private double responseTime;
  private long startTime;
  private int successCount;
  private int failureCount;

  public synchronized void reset () {

    successCount = 0;
    failureCount = 0;
    responseTime = 0;
    startTime = System.currentTimeMillis();
  }

  public synchronized void incFailureCount () {

    failureCount++;
  }

  public synchronized void addExchange (long responseTime) {

    successCount++;
    this.responseTime += responseTime;
  }

  public synchronized double getAverageResponseTime () {

    if (successCount == 0) {

      return 0D;
    }

    return responseTime / successCount;
  }

  public synchronized double getAverageRequestCount () {

    long elapsedTime;

    if ((elapsedTime = System.currentTimeMillis() - startTime) == 0) {

      return 0D;
    }

    return (successCount + failureCount) * 1000D / elapsedTime;
  }

  public synchronized int getTotalFailureCount () {

    return failureCount;
  }

  public synchronized double getAverageFailureCount () {

    long elapsedTime;

    if ((elapsedTime = System.currentTimeMillis() - startTime) == 0) {

      return 0D;
    }

    return failureCount * 1000D / elapsedTime;
  }

  public synchronized double getFailurePercentage () {

    return (failureCount * 100D) / (successCount + failureCount);
  }
}
