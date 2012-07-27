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
package com.codeasylum.stress.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import com.codeasylum.stress.api.Task;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class TestTreeCellEditor implements TreeCellEditor, ItemListener {

  private final WeakEventListenerList<CellEditorListener> listenerList = new WeakEventListenerList<CellEditorListener>();

  private JTree testTree;
  private TreeCellRenderer treeCellRenderer;
  private AtomicReference<EditLink> editLinkRef = new AtomicReference<EditLink>();

  public TestTreeCellEditor (JTree testTree, TreeCellRenderer treeCellRenderer) {

    this.testTree = testTree;
    this.treeCellRenderer = treeCellRenderer;
  }

  @Override
  public void itemStateChanged (ItemEvent itemEvent) {

    stopCellEditing();
  }

  @Override
  public Component getTreeCellEditorComponent (JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

    JPanel editorPanel;
    JCheckBox editCheckBox;

    editorPanel = (JPanel)treeCellRenderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, false);
    editLinkRef.set(new EditLink((Task)value, editCheckBox = (JCheckBox)editorPanel.getComponent(0)));
    editCheckBox.addItemListener(this);

    return editorPanel;
  }

  @Override
  public Object getCellEditorValue () {

    EditLink editLink;

    if ((editLink = editLinkRef.get()) != null) {

      return editLink.getEditCheckBox().isSelected();
    }

    return null;
  }

  @Override
  public boolean isCellEditable (EventObject anEvent) {

    return (anEvent instanceof MouseEvent) && (((MouseEvent)anEvent).getClickCount() == 1);
  }

  @Override
  public boolean shouldSelectCell (EventObject anEvent) {

    return false;
  }

  public void cancelCellEditing (TreePath clickedPath) {

    if (editLinkRef.get() != null) {
      cancelCellEditing();
      testTree.setSelectionPath(clickedPath);
      testTree.grabFocus();
    }
  }

  @Override
  public void cancelCellEditing () {

    EditLink editLink;

    if ((editLink = editLinkRef.get()) != null) {
      editLink.getEditCheckBox().removeItemListener(this);
      editLinkRef.set(null);
      fireEditingCellCancelled();
      testTree.grabFocus();
    }
  }

  @Override
  public boolean stopCellEditing () {

    EditLink editLink;

    if ((editLink = editLinkRef.get()) != null) {

      TreePath editPath;

      editLink.getEditCheckBox().removeItemListener(this);

      if ((editPath = ((TestTreeModel)testTree.getModel()).getPathForTask(editLink.getEditTask().getUUID())) == null) {
        throw new IllegalStateException("Unexpectedly missing task");
      }

      ((TestTreeModel)testTree.getModel()).setEnabled(editLink.getEditCheckBox().isSelected(), editPath);

      editLinkRef.set(null);
      fireEditingCellStopped();
    }

    return true;
  }

  private void fireEditingCellCancelled () {

    synchronized (listenerList) {
      for (CellEditorListener cellEditorListener : listenerList) {
        cellEditorListener.editingCanceled(new ChangeEvent(this));
      }
    }
  }

  private void fireEditingCellStopped () {

    synchronized (listenerList) {
      for (CellEditorListener cellEditorListener : listenerList) {
        cellEditorListener.editingStopped(new ChangeEvent(this));
      }
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

  private class EditLink {

    private Task editTask;
    private JCheckBox editCheckBox;

    private EditLink (Task editTask, JCheckBox editCheckBox) {

      this.editTask = editTask;
      this.editCheckBox = editCheckBox;
    }

    public Task getEditTask () {

      return editTask;
    }

    public JCheckBox getEditCheckBox () {

      return editCheckBox;
    }
  }
}
