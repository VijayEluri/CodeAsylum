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

import java.util.NoSuchElementException;
import java.util.TreeSet;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class MonitorLegendListModel implements ListModel {

  private final WeakEventListenerList<ListDataListener> listenerList = new WeakEventListenerList<ListDataListener>();
  private final TreeSet<MonitorLegend> legendSet = new TreeSet<MonitorLegend>();

  public synchronized MonitorLegend addLegend (MonitorLegend legend) {

    if (legendSet.add(legend)) {
      fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, legendSet.size()));
    }

    for (MonitorLegend currentLegend : legendSet) {
      if (currentLegend.getName().equals(legend.getName())) {

        return currentLegend;
      }
    }

    throw new IllegalStateException();
  }

  public synchronized MonitorLegend flipVisibility (int index) {

    MonitorLegend legend;

    (legend = (MonitorLegend)getElementAt(index)).setVisible(!legend.isVisible());
    fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));

    return legend;
  }

  @Override
  public synchronized int getSize () {

    return legendSet.size();
  }

  @Override
  public synchronized Object getElementAt (int index) {

    if (legendSet.size() <= index) {
      throw new NoSuchElementException(index + ">" + (legendSet.size() - 1));
    }

    int pos = 0;
    for (MonitorLegend legend : legendSet) {
      if (pos++ == index) {

        return legend;
      }
    }

    throw new IllegalStateException();
  }

  private void fireContentsChanged (ListDataEvent listDataEvent) {

    for (ListDataListener listDataListener : listenerList) {
      listDataListener.contentsChanged(listDataEvent);
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
