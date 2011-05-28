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

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class EnumComboBoxModel<E extends Enum<E>> implements ComboBoxModel {

  private final WeakEventListenerList<ListDataListener> listenerList = new WeakEventListenerList<ListDataListener>();

  private E selectedEnum;
  private Class<E> enumClass;

  public EnumComboBoxModel (Class<E> enumClass, E selectedEnum) {

    this.enumClass = enumClass;
    this.selectedEnum = selectedEnum;
  }

  @Override
  public synchronized void setSelectedItem (Object anItem) {

    selectedEnum = enumClass.cast(anItem);
  }

  @Override
  public synchronized Object getSelectedItem () {

    return selectedEnum;
  }

  @Override
  public int getSize () {

    return enumClass.getEnumConstants().length;
  }

  @Override
  public Object getElementAt (int index) {

    return enumClass.getEnumConstants()[index];
  }

  @Override
  public synchronized void addListDataListener (ListDataListener listDataListener) {

    listenerList.addListener(listDataListener);
  }

  @Override
  public synchronized void removeListDataListener (ListDataListener listDataListener) {

    listenerList.removeListener(listDataListener);
  }
}
