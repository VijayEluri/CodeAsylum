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

import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.util.DotNotationComparator;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;
import org.smallmind.swing.dialog.WarningDialog;

public class MapTableModel implements TableModel {

  private final WeakEventListenerList<TableModelListener> tableModelListenerList = new WeakEventListenerList<TableModelListener>();

  private JFrame parentFrame;
  private TestPlan testPlan;
  private Map<String, String> backingMap;
  private TreeMap<String, String> propertyMap;
  private String valueName;

  public MapTableModel (JFrame parentFrame, TestPlan testPlan, Map<String, String> backingMap, String valueName, boolean reversed) {

    this.parentFrame = parentFrame;
    this.testPlan = testPlan;
    this.backingMap = backingMap;
    this.valueName = valueName;

    propertyMap = new TreeMap<String, String>(new DotNotationComparator(reversed));
    propertyMap.putAll(backingMap);
  }

  public synchronized boolean containsKey (String key) {

    return propertyMap.containsKey(key);
  }

  public synchronized int rowForKey (String key) {

    int index = 0;

    for (String propertyKey : propertyMap.keySet()) {
      if (propertyKey.equals(key)) {

        return index;
      }
      index++;
    }

    return -1;
  }

  public synchronized void put (String key, String value) {

    backingMap.put(key, value);
    propertyMap.put(key, value);

    testPlan.setChanged(true);
    fireTableChanged(new TableModelEvent(this));
  }

  public synchronized void remove (int index) {

    remove(getEntryForRow(index).getKey());
  }

  public synchronized void remove (String key) {

    backingMap.remove(key);
    propertyMap.remove(key);

    testPlan.setChanged(true);
    fireTableChanged(new TableModelEvent(this));
  }

  @Override
  public int getRowCount () {

    return propertyMap.size();
  }

  @Override
  public int getColumnCount () {

    return 2;
  }

  @Override
  public String getColumnName (int columnIndex) {

    return (columnIndex == 0) ? "Key" : valueName;
  }

  @Override
  public Class<?> getColumnClass (int columnIndex) {

    return String.class;
  }

  @Override
  public boolean isCellEditable (int rowIndex, int columnIndex) {

    return true;
  }

  @Override
  public synchronized Object getValueAt (int rowIndex, int columnIndex) {

    return (columnIndex == 0) ? getEntryForRow(rowIndex).getKey() : getEntryForRow(rowIndex).getValue();
  }

  @Override
  public synchronized void setValueAt (Object aValue, int rowIndex, int columnIndex) {

    Map.Entry<String, String> propertyEntry = getEntryForRow(rowIndex);

    if (columnIndex == 0) {
      if ((!propertyEntry.getKey().equals(aValue)) && propertyMap.containsKey(aValue.toString())) {
        WarningDialog.showWarningDialog(parentFrame, "The property key '" + aValue + "' is already in use");
      }
      else {
        remove(propertyEntry.getKey());
        put(aValue.toString(), propertyEntry.getValue());
      }
    }
    else {
      backingMap.put(propertyEntry.getKey(), aValue.toString());
      propertyMap.put(propertyEntry.getKey(), aValue.toString());

      testPlan.setChanged(true);
      fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex));
    }
  }

  private Map.Entry<String, String> getEntryForRow (int rowIndex) {

    int index = 0;

    for (Map.Entry<String, String> propertyEntry : propertyMap.entrySet()) {
      if (rowIndex == index++) {

        return propertyEntry;
      }
    }

    throw new IndexOutOfBoundsException(rowIndex + ">" + (index - 1));
  }

  private synchronized void fireTableChanged (TableModelEvent tableModelEvent) {

    for (TableModelListener tableModelListener : tableModelListenerList) {
      tableModelListener.tableChanged(tableModelEvent);
    }
  }

  @Override
  public synchronized void addTableModelListener (TableModelListener tableModelListener) {

    tableModelListenerList.addListener(tableModelListener);
  }

  @Override
  public synchronized void removeTableModelListener (TableModelListener tableModelListener) {

    tableModelListenerList.removeListener(tableModelListener);
  }
}
