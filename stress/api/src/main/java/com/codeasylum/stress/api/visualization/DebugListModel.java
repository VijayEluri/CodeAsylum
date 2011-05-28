/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
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
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import com.codeasylum.stress.api.DebugEvent;
import com.codeasylum.stress.api.DebugListener;
import com.codeasylum.stress.api.ExchangeTransport;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class DebugListModel implements ListModel, DebugListener {

  private final WeakEventListenerList<ListDataListener> listenerList = new WeakEventListenerList<ListDataListener>();
  private final LinkedList<DebugWrapper> outputList = new LinkedList<DebugWrapper>();
  private final AtomicInteger serialNumber = new AtomicInteger(0);

  private TestPlan testPlan;

  public DebugListModel (TestPlan testPlan, ExchangeTransport exchangeTransport)
    throws RemoteException {

    this.testPlan = testPlan;

    exchangeTransport.addDebugListener(this);
  }

  public void clear () {

    synchronized (outputList) {

      int size = outputList.size();

      serialNumber.set(0);
      outputList.clear();
      fireIntervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, size - 1));
    }
  }

  @Override
  public void receive (DebugEvent debugEvent) {

    synchronized (outputList) {

      int size = outputList.size();

      while (size >= testPlan.getDebugBufferSize()) {
        outputList.removeLast();
        fireIntervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, size - 1, size - 1));
        size--;
      }

      outputList.addFirst(new DebugWrapper(serialNumber.incrementAndGet(), debugEvent.getDebug()));
      fireIntervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, 0));
    }
  }

  @Override
  public int getSize () {

    synchronized (outputList) {

      return outputList.size();
    }
  }

  @Override
  public Object getElementAt (int index) {

    synchronized (outputList) {

      return outputList.get(index);
    }
  }

  public void fireIntervalAdded (ListDataEvent listDataEvent) {

    for (ListDataListener listDataListener : listenerList) {
      listDataListener.intervalAdded(listDataEvent);
    }
  }

  public void fireIntervalRemoved (ListDataEvent listDataEvent) {

    for (ListDataListener listDataListener : listenerList) {
      listDataListener.intervalRemoved(listDataEvent);
    }
  }

  @Override
  public void addListDataListener (ListDataListener listDataListener) {

    listenerList.addListener(listDataListener);
  }

  @Override
  public void removeListDataListener (ListDataListener listDataListener) {

    listenerList.removeListener(listDataListener);
  }
}
