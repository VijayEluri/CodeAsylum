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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import com.codeasylum.stress.api.PluralContainer;
import com.codeasylum.stress.api.RootTask;
import com.codeasylum.stress.api.SingularContainer;
import com.codeasylum.stress.api.Task;
import com.codeasylum.stress.api.TaskAvatar;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.dialog.StopDialog;
import org.smallmind.swing.dragndrop.GhostPanel;

public class TestPanel extends JPanel implements Translucent, TreeSelectionListener, MouseListener, KeyListener {

  private Jormungandr jormungandr;
  private TaskPalette palette;
  private ViewPanel viewPanel;
  private JTree testTree;
  private TestTreeCellEditor testTreeCellEditor;

  public TestPanel (Jormungandr jormungandr, GhostPanel ghostPanel, TaskPalette palette, ViewPanel viewPanel, JTree testTree) {

    GroupLayout groupLayout;
    TestTreeCellRenderer testTreeCellRenderer;

    this.jormungandr = jormungandr;
    this.palette = palette;
    this.viewPanel = viewPanel;
    this.testTree = testTree;

    setLayout(groupLayout = new GroupLayout(this));

    testTree.setModel(new TestTreeModel(palette, testTree, jormungandr.getTestExecutor().getTestPlan()));
    testTree.setRootVisible(true);
    testTree.setCellRenderer(testTreeCellRenderer = new TestTreeCellRenderer(palette));
    testTree.setCellEditor(testTreeCellEditor = new TestTreeCellEditor(testTree, testTreeCellRenderer));
    testTree.addTreeSelectionListener(this);
    testTree.addMouseListener(this);
    testTree.addKeyListener(this);
    testTree.setEditable(true);

    setBackground(testTree.getBackground());

    new TestDragHandler(ghostPanel, testTree, palette);
    new TestDropHandler(jormungandr, testTree, palette);

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addComponent(testTree));
    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup().addComponent(testTree, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
  }

  public void setTestPlan (TestPlan testPlan) {

    TestTreeModel testTreeModel;

    testTree.setSelectionPath(null);
    testTree.setModel(testTreeModel = new TestTreeModel(palette, testTree, testPlan));
    testTreeModel.recursivelyExpandPath(new TaskTreePath((Task)testTree.getModel().getRoot()));
  }

  @Override
  public void blur () {

    testTree.setSelectionPath(null);
  }

  @Override
  public void valueChanged (TreeSelectionEvent treeSelectionEvent) {

    TreePath selectedPath;

    if ((selectedPath = treeSelectionEvent.getNewLeadSelectionPath()) != null) {

      Task selectedTask;
      TaskAvatar selectedAvatar;
      JComponent visualization;

      jormungandr.solidify(Workspace.TEST);

      selectedAvatar = palette.getAvatar((selectedTask = (Task)selectedPath.getLastPathComponent()).getClass());

      try {
        visualization = selectedAvatar.getVisualization(jormungandr, jormungandr.getTestExecutor(), selectedTask);
        viewPanel.setViewComponent(jormungandr.getTestExecutor().getTestPlan(), selectedTask, selectedPath, visualization);
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog(jormungandr, this, exception);
      }
    }
    else {
      viewPanel.clearViewComponent();
    }
  }

  @Override
  public void mouseClicked (MouseEvent mouseEvent) {

    TreePath clickedPath;

    if ((clickedPath = testTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY())) != null) {
      testTreeCellEditor.cancelCellEditing(clickedPath);
    }
  }

  @Override
  public void mousePressed (MouseEvent mouseEvent) {

  }

  @Override
  public void mouseReleased (MouseEvent mouseEvent) {

  }

  @Override
  public void mouseEntered (MouseEvent mouseEvent) {

  }

  @Override
  public void mouseExited (MouseEvent mouseEvent) {

  }

  @Override
  public void keyTyped (KeyEvent keyEvent) {

  }

  @Override
  public void keyPressed (KeyEvent keyEvent) {

  }

  @Override
  public void keyReleased (KeyEvent keyEvent) {

    TreePath selectionPath;

    if ((keyEvent.getKeyCode() == KeyEvent.VK_DELETE) && (keyEvent.getModifiers() == 0) && ((selectionPath = testTree.getSelectionPath()) != null)) {

      TreeModelEvent deletionEvent;
      Task parentTask;

      if (RootTask.class.isAssignableFrom(selectionPath.getLastPathComponent().getClass())) {
        StopDialog.showStopDialog(jormungandr, "You can't delete the root node of your test");
      }
      else {

        TreePath newSelectionPath;
        int deletionRow;

        if ((deletionRow = testTree.getRowForPath(selectionPath)) < 0) {
          throw new IllegalStateException("Unexpectedly missing task");
        }

        deletionEvent = new TreeModelEvent(this, selectionPath.getParentPath(), new int[] {testTree.getModel().getIndexOfChild(selectionPath.getParentPath().getLastPathComponent(), selectionPath.getLastPathComponent())}, new Object[] {selectionPath.getLastPathComponent()});

        parentTask = (Task)selectionPath.getParentPath().getLastPathComponent();
        switch (palette.getAvatar(parentTask.getClass()).getChildModel()) {
          case NONE:
            throw new IllegalStateException("Attempted removal of a child from a leaf node parent (yes, this makes  no sense)");
          case SINGULAR:
            ((SingularContainer)parentTask).setTask(null);
            break;
          case PLURAL:
            ((PluralContainer)parentTask).remove((Task)selectionPath.getLastPathComponent());
            break;
          default:
            throw new UnknownSwitchCaseException(palette.getAvatar(parentTask.getClass()).getChildModel().name());
        }

        ((TestTreeModel)testTree.getModel()).getTestPlan().setChanged(true);
        ((TestTreeModel)testTree.getModel()).fireTreeNodesRemoved(deletionEvent);

        testTree.setSelectionPath(((newSelectionPath = testTree.getPathForRow(deletionRow)) == null) ? testTree.getPathForRow(deletionRow - 1) : newSelectionPath);
      }
    }
  }
}
