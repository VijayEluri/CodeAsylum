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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.ATrace2D;

public abstract class PerSecondTimedTrace extends ATrace2D {

  private final ConcurrentSkipListMap<Long, ITracePoint2D> pointMap = new ConcurrentSkipListMap<Long, ITracePoint2D>();
  private final ReentrantReadWriteLock pointLock = new ReentrantReadWriteLock();
  private final AtomicInteger size = new AtomicInteger(0);

  private TimeUnit rangeTimeUnit;
  private long rangeTimeDuration;

  public PerSecondTimedTrace (long rangeTimeDuration, TimeUnit rangeTimeUnit) {

    this.rangeTimeDuration = rangeTimeDuration;
    this.rangeTimeUnit = rangeTimeUnit;
  }

  public synchronized void setDuration (long rangeTimeDuration) {

    this.rangeTimeDuration = rangeTimeDuration;
  }

  public abstract ITracePoint2D calculatePoint (ITracePoint2D currentPoint, ITracePoint2D newPoint);

  @Override
  public int getMaxSize () {

    return (int)rangeTimeUnit.toSeconds(rangeTimeDuration);
  }

  @Override
  public int getSize () {

    return size.get();
  }

  @Override
  public boolean isEmpty () {

    return size.get() > 0;
  }

  @Override
  protected boolean addPointInternal (ITracePoint2D iTracePoint2D) {

    pointLock.readLock().lock();
    try {

      long pointSeconds;
      long floorSeconds;

      if ((pointSeconds = (long)iTracePoint2D.getX() / 1000) >= (floorSeconds = (System.currentTimeMillis() / 1000) - (int)rangeTimeUnit.toSeconds(rangeTimeDuration))) {
        synchronized (pointMap) {

          ITracePoint2D calculatedPoint;

          if ((calculatedPoint = calculatePoint(pointMap.get(pointSeconds), iTracePoint2D)) != null) {
            pointMap.put(pointSeconds, calculatedPoint);
            size.incrementAndGet();
            firePointAdded(calculatedPoint);
            calculatedPoint.setListener(this);
          }

          for (Map.Entry<Long, ITracePoint2D> removedEntry : pointMap.subMap(0L, floorSeconds).entrySet()) {
            pointMap.remove(removedEntry.getKey());
            size.decrementAndGet();
            firePointRemoved(removedEntry.getValue());
          }
        }
      }

      return false;
    }
    finally {
      pointLock.readLock().unlock();
    }
  }

  public void clear () {

    removeAllPointsInternal();
  }

  @Override
  protected void removeAllPointsInternal () {

    pointLock.writeLock().lock();
    try {
      pointMap.clear();
      size.set(0);
    }
    finally {
      pointLock.writeLock().unlock();
    }
  }

  @Override
  protected ITracePoint2D removePointInternal (ITracePoint2D iTracePoint2D) {

    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<ITracePoint2D> iterator () {

    pointLock.readLock().lock();
    try {
      return pointMap.values().iterator();
    }
    finally {
      pointLock.readLock().unlock();
    }
  }
}
