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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class MapTableCellEditor implements TableCellEditor, FocusListener, DocumentListener {

  private final WeakEventListenerList<CellEditorListener> listenerList = new WeakEventListenerList<CellEditorListener>();

  private MapTableModel tableModel;
  private AtomicReference<JTextField> editorRef = new AtomicReference<JTextField>();

  public MapTableCellEditor (MapTableModel tableModel) {

    this.tableModel = tableModel;
  }

  @Override
  public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int column) {

    JTextField editor;

    editorRef.set(editor = new JTextField(value.toString()));
    editor.setBorder(BorderFactory.createLineBorder(table.getBackground(), 2));

    editor.addFocusListener(this);
    if (column == 0) {
      editor.getDocument().addDocumentListener(this);
    }

    return editor;
  }

  @Override
  public Object getCellEditorValue () {

    JTextField editor;

    if ((editor = editorRef.get()) != null) {

      String value = editor.getText().trim();

      editorRef.set(null);

      return value;
    }

    return null;
  }

  @Override
  public boolean isCellEditable (EventObject anEvent) {

    return (anEvent == null) || ((anEvent instanceof MouseEvent) && (((MouseEvent)anEvent).getClickCount() == 2));
  }

  @Override
  public boolean shouldSelectCell (EventObject anEvent) {

    return false;
  }

  @Override
  public boolean stopCellEditing () {

    fireEditingStopped();

    return true;
  }

  @Override
  public synchronized void cancelCellEditing () {

    editorRef.set(null);
    fireEditingCanceled();
  }

  private synchronized void fireEditingStopped () {

    for (CellEditorListener cellEditorListener : listenerList) {
      cellEditorListener.editingStopped(new ChangeEvent(this));
    }
  }

  private void fireEditingCanceled () {

    for (CellEditorListener cellEditorListener : listenerList) {
      cellEditorListener.editingCanceled(new ChangeEvent(this));
    }
  }

  @Override
  public synchronized void addCellEditorListener (CellEditorListener cellEditorListener) {

    listenerList.addListener(cellEditorListener);
  }

  @Override
  public synchronized void removeCellEditorListener (CellEditorListener cellEditorListener) {

    listenerList.removeListener(cellEditorListener);
  }

  private void editorUpdated () {

    JTextField editor;

    if ((editor = editorRef.get()) != null) {
      editor.setForeground(tableModel.containsKey(editor.getText().trim()) ? Color.RED : UIManager.getDefaults().getColor("textText"));
    }
  }

  @Override
  public void focusGained (FocusEvent focusEvent) {

  }

  @Override
  public void focusLost (FocusEvent focusEvent) {

    cancelCellEditing();
  }

  @Override
  public void insertUpdate (DocumentEvent documentEvent) {

    editorUpdated();
  }

  @Override
  public void removeUpdate (DocumentEvent documentEvent) {

    editorUpdated();
  }

  @Override
  public void changedUpdate (DocumentEvent documentEvent) {

    editorUpdated();
  }
}
