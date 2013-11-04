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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestExecutorEvent;
import com.codeasylum.stress.api.TestExecutorListener;
import info.monitorenter.gui.chart.IRangePolicy;
import info.monitorenter.util.Range;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class PerSecondTimeRangePolicy implements IRangePolicy, TestExecutorListener {

  private final WeakEventListenerList<PropertyChangeListener> listenerList = new WeakEventListenerList<PropertyChangeListener>();
  private final AtomicReference<Range> rangeRef = new AtomicReference<Range>();

  private Range currentRange;
  private RangeTimer rangeTimer;
  private TimeUnit rangeTimeUnit;
  private long rangeTimeDuration;

  public PerSecondTimeRangePolicy (TestExecutor testExecutor, long rangeTimeDuration, TimeUnit rangeTimeUnit) {

    this.rangeTimeDuration = rangeTimeDuration;
    this.rangeTimeUnit = rangeTimeUnit;

    adjustRange(System.currentTimeMillis() / 1000);
    currentRange = rangeRef.get();

    testExecutor.addTestExecutorListener(this);
    new Thread((rangeTimer = new RangeTimer())).start();
  }

  public synchronized void setDuration (long rangeTimeDuration) {

    this.rangeTimeDuration = rangeTimeDuration;
    adjustRange(System.currentTimeMillis() / 1000);
  }

  private synchronized void adjustRange (long secondsSinceEpoch) {

    long now;

    rangeRef.set(new Range((now = secondsSinceEpoch * 1000) - rangeTimeUnit.toMillis(rangeTimeDuration), now));
    firePropertyChange(new PropertyChangeEvent(this, IRangePolicy.PROPERTY_RANGE, currentRange, currentRange = rangeRef.get()));
  }

  @Override
  public double getMax (double v, double v1) {

    return rangeRef.get().getMax();
  }

  @Override
  public double getMin (double v, double v1) {

    return rangeRef.get().getMin();
  }

  @Override
  public Range getRange () {

    long now;

    return new Range((now = System.currentTimeMillis()) - rangeTimeUnit.toMillis(rangeTimeDuration), now);
  }

  @Override
  public void setRange (Range range) {

    throw new UnsupportedOperationException();
  }

  private synchronized void firePropertyChange (PropertyChangeEvent propertyChangeEvent) {

    for (PropertyChangeListener propertyChangeListener : listenerList) {
      propertyChangeListener.propertyChange(propertyChangeEvent);
    }
  }

  @Override
  public PropertyChangeListener[] getPropertyChangeListeners (String property) {

    throw new UnsupportedOperationException();
  }

  @Override
  public void addPropertyChangeListener (String property, PropertyChangeListener propertyChangeListener) {

    if (property.equals(IRangePolicy.PROPERTY_RANGE)) {
      listenerList.addListener(propertyChangeListener);
    }
  }

  @Override
  public void removePropertyChangeListener (PropertyChangeListener propertyChangeListener, String property) {

    removePropertyChangeListener(property, propertyChangeListener);
  }

  @Override
  public void removePropertyChangeListener (String property, PropertyChangeListener propertyChangeListener) {

    if (property.equals(IRangePolicy.PROPERTY_RANGE)) {
      listenerList.removeListener(propertyChangeListener);
    }
  }

  @Override
  public void executionStarted (TestExecutorEvent testExecutorEvent) {

    rangeTimer.setEngaged(true);
  }

  @Override
  public void executionStopped (TestExecutorEvent testExecutorEvent) {

    rangeTimer.setEngaged(false);
  }

  private class RangeTimer implements Runnable {

    private CountDownLatch terminationLatch = new CountDownLatch(1);
    private CountDownLatch finishLatch = new CountDownLatch(1);
    private long secondsSinceEpoch;
    private boolean engaged = false;

    public void finish ()
      throws InterruptedException {

      terminationLatch.countDown();
      finishLatch.await();

      secondsSinceEpoch = System.currentTimeMillis() / 1000;
    }

    public void setEngaged (boolean engaged) {

      this.engaged = engaged;
    }

    @Override
    public void run () {

      try {
        while (!terminationLatch.await(300, TimeUnit.MILLISECONDS)) {
          if (engaged) {

            long nowSeconds;

            if ((nowSeconds = System.currentTimeMillis() / 1000) > secondsSinceEpoch) {
              adjustRange(secondsSinceEpoch = nowSeconds);
            }
          }
        }
      }
      catch (InterruptedException interruptedException) {
        terminationLatch.countDown();
      }
      finally {
        finishLatch.countDown();
      }
    }

    @Override
    protected void finalize ()
      throws InterruptedException {

      finish();
    }
  }
}
