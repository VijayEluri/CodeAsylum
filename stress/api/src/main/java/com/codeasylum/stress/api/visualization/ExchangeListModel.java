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
package com.codeasylum.stress.api.visualization;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import com.codeasylum.stress.api.ExchangeEvent;
import com.codeasylum.stress.api.ExchangeListener;
import com.codeasylum.stress.api.ExchangeTransport;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class ExchangeListModel implements ListModel, ExchangeListener {

  private final WeakEventListenerList<ListDataListener> listDataListenerList = new WeakEventListenerList<ListDataListener>();
  private final LinkedList<ExchangeWrapper> exchangeList = new LinkedList<ExchangeWrapper>();
  private final EventCompressor eventCompressor;
  private final AtomicInteger serialNumber = new AtomicInteger(0);

  private TestPlan testPlan;

  public ExchangeListModel (TestPlan testPlan, ExchangeTransport exchangeTransport)
    throws RemoteException {

    this.testPlan = testPlan;

    exchangeTransport.addExchangeListener(this);

    new Thread(eventCompressor = new EventCompressor()).start();
  }

  @Override
  public void receive (ExchangeEvent exchangeEvent) {

    synchronized (exchangeList) {
      exchangeList.addFirst(new ExchangeWrapper(serialNumber.incrementAndGet(), exchangeEvent.getExchange()));
      if (exchangeList.size() > testPlan.getExchangeBufferSize()) {
        exchangeList.removeLast();
      }

      eventCompressor.inc();
    }
  }

  public void clear () {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run () {

        synchronized (exchangeList) {
          if (!exchangeList.isEmpty()) {

            int size = exchangeList.size();

            serialNumber.set(0);
            exchangeList.clear();
            eventCompressor.clear();
            fireIntervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, size - 1));
          }
        }
      }
    });
  }

  @Override
  public int getSize () {

    synchronized (exchangeList) {
      return exchangeList.size();
    }
  }

  @Override
  public Object getElementAt (int index) {

    synchronized (exchangeList) {
      return exchangeList.get(index);
    }
  }

  private void fireIntervalAdded (ListDataEvent listDataEvent) {

    for (ListDataListener listDataListener : listDataListenerList) {
      listDataListener.intervalAdded(listDataEvent);
    }
  }

  private void fireIntervalRemoved (ListDataEvent listDataEvent) {

    for (ListDataListener listDataListener : listDataListenerList) {
      listDataListener.intervalRemoved(listDataEvent);
    }
  }

  @Override
  public synchronized void addListDataListener (ListDataListener listDataListener) {

    listDataListenerList.addListener(listDataListener);
  }

  @Override
  public synchronized void removeListDataListener (ListDataListener listDataListener) {

    listDataListenerList.removeListener(listDataListener);
  }

  private class EventWindow {

    private int added = 0;
    private int size = 0;

    public void reset () {

      added = 0;
      size = exchangeList.size();
    }

    public void clear () {

      added = 0;
      size = 0;
    }

    public int getAdded () {

      return added;
    }

    public int getSize () {

      return size;
    }

    public void inc () {

      added++;
      size++;
    }
  }

  private class EventCompressor implements Runnable {

    private CountDownLatch exitLatch;
    private CountDownLatch pulseLatch;
    private EventWindow eventWindow;

    public EventCompressor () {

      pulseLatch = new CountDownLatch(1);
      exitLatch = new CountDownLatch(1);

      eventWindow = new EventWindow();
    }

    public void finish ()
      throws InterruptedException {

      pulseLatch.countDown();
      exitLatch.await();
    }

    public void inc () {

      eventWindow.inc();
    }

    public void clear () {

      eventWindow.clear();
    }

    @Override
    public void run () {

      try {
        while (!pulseLatch.await(300, TimeUnit.MILLISECONDS)) {
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run () {

              synchronized (exchangeList) {
                if (eventWindow.getAdded() > 0) {
                  fireIntervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, eventWindow.getAdded() - 1));
                  if (eventWindow.getSize() > testPlan.getExchangeBufferSize()) {
                    fireIntervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, testPlan.getExchangeBufferSize(), eventWindow.getSize() - 1));
                  }

                  eventWindow.reset();
                }
              }
            }
          });
        }
      }
      catch (InterruptedException interruptedException) {
      }
      finally {
        exitLatch.countDown();
      }
    }

    @Override
    protected void finalize ()
      throws Throwable {

      finish();
    }
  }
}
