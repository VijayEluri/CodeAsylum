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
package com.codeasylum.stress.ui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import com.codeasylum.stress.api.PluralContainer;
import com.codeasylum.stress.api.SingularContainer;
import com.codeasylum.stress.api.Task;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.dragndrop.DropHandler;

public class TestDropHandler extends DropHandler {

  private JFrame parentFrame;
  private TaskPalette palette;
  private TreePath priorDropPath;

  public TestDropHandler (JFrame parentFrame, JTree testTree, TaskPalette palette) {

    super(testTree, DnDConstants.ACTION_COPY_OR_MOVE);

    this.parentFrame = parentFrame;
    this.palette = palette;
  }

  private TaskDataFlavor getTaskDataFlavor (DataFlavor[] flavors) {

    for (DataFlavor flavor : flavors) {
      if (flavor instanceof TaskDataFlavor) {

        return (TaskDataFlavor)flavor;
      }
    }

    return null;
  }

  private boolean checkHierarchyRestrictions (Class<? extends Task> dropTaskClass, Class<? extends Task> dragTaskClass) {

    return restrictionContains(dragTaskClass, palette.getAvatar(dropTaskClass).mustPrecede(), true) & (!restrictionContains(dragTaskClass, palette.getAvatar(dropTaskClass).mustNotPrecede(), false)) & restrictionContains(dropTaskClass, palette.getAvatar(dragTaskClass).mustProceed(), true) & (!restrictionContains(dropTaskClass, palette.getAvatar(dragTaskClass).mustNotProceed(), false));
  }

  private boolean restrictionContains (Class<? extends Task> taskClass, Class[] restrictions, boolean positive) {

    if ((restrictions == null) || (restrictions.length == 0)) {

      return positive;
    }

    for (Class restriction : restrictions) {
      if (restriction.isAssignableFrom(taskClass)) {

        return true;
      }
    }

    return false;
  }

  private TreePath isPermittedDropLocation (JTree testTree, TaskTransferData transferData, Point location, int dropAction) {

    TreePath dropPath;
    Task dropTask;

    if ((dropPath = testTree.getPathForLocation((int)location.getX(), (int)location.getY())) == null) {

      return null;
    }
    if (transferData.getTransferType().equals(TransferType.MOVE_OR_COPY)) {
      if (((Task)dropPath.getLastPathComponent()).getUUID().equals(transferData.getTaskUUID())) {

        return null;
      }
      else if (dropAction == DnDConstants.ACTION_MOVE) {

        TreePath dragPath;

        if (((dragPath = ((TestTreeModel)testTree.getModel()).getPathForTask(transferData.getTaskUUID())) != null) && dragPath.isDescendant(dropPath)) {

          return null;
        }
      }
    }

    switch (palette.getAvatar((dropTask = (Task)dropPath.getLastPathComponent()).getClass()).getChildModel()) {
      case NONE:
        if ((dropPath.getParentPath() != null) && (PluralContainer.class.isAssignableFrom((dropTask = (Task)dropPath.getParentPath().getLastPathComponent()).getClass()))) {
          if (checkHierarchyRestrictions(dropTask.getClass(), transferData.getTaskClass())) {

            return dropPath;
          }
        }

        return null;
      case SINGULAR:
        if (((SingularContainer)dropTask).getTask() == null) {
          if (checkHierarchyRestrictions(dropTask.getClass(), transferData.getTaskClass())) {

            return dropPath;
          }
        }

        return null;
      case PLURAL:

        int size;

        if ((dropAction == DnDConstants.ACTION_MOVE) & ((size = ((PluralContainer)dropTask).size()) > 0) && ((PluralContainer)dropTask).getChild(size - 1).getUUID().equals(transferData.getTaskUUID())) {

          return null;
        }

        if (checkHierarchyRestrictions(dropTask.getClass(), transferData.getTaskClass())) {

          return dropPath;
        }
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)dropPath.getLastPathComponent().getClass()).getChildModel().name());
    }
  }

  @Override
  public boolean canDrop (DropTargetDragEvent dropTargetDragEvent) {

    TaskDataFlavor flavor;
    TaskTransferData transferData;
    JTree testTree;
    TreePath dropPath;

    if ((flavor = getTaskDataFlavor(dropTargetDragEvent.getCurrentDataFlavors())) == null) {

      return false;
    }

    try {
      transferData = (TaskTransferData)dropTargetDragEvent.getTransferable().getTransferData(flavor);
    }
    catch (Exception exception) {
      JavaErrorDialog.showJavaErrorDialog(parentFrame, this, exception);

      return false;
    }

    testTree = (JTree)dropTargetDragEvent.getDropTargetContext().getComponent();
    dropPath = isPermittedDropLocation(testTree, transferData, dropTargetDragEvent.getLocation(), dropTargetDragEvent.getDropAction());

    if (dropPath != null) {
      if ((priorDropPath == null) || (!dropPath.equals(priorDropPath))) {
        ((TestTreeModel)testTree.getModel()).setHighlightedTask((Task)dropPath.getLastPathComponent());
        if (priorDropPath != null) {
          ((TestTreeModel)testTree.getModel()).fireTreeNodesChanged(((TestTreeModel)testTree.getModel()).eventForPathChanged(priorDropPath, priorDropPath.getLastPathComponent()));
        }
        ((TestTreeModel)testTree.getModel()).fireTreeNodesChanged(((TestTreeModel)testTree.getModel()).eventForPathChanged(priorDropPath = dropPath, dropPath.getLastPathComponent()));
      }

      return true;
    }
    else {
      if (priorDropPath != null) {
        ((TestTreeModel)testTree.getModel()).setHighlightedTask(null);
        ((TestTreeModel)testTree.getModel()).fireTreeNodesChanged(((TestTreeModel)testTree.getModel()).eventForPathChanged(priorDropPath, priorDropPath.getLastPathComponent()));
        priorDropPath = null;
      }

      return false;
    }
  }

  @Override
  public boolean dropComplete (DropTargetDropEvent dropTargetDropEvent) {

    JTree testTree = (JTree)dropTargetDropEvent.getDropTargetContext().getComponent();
    TaskDataFlavor flavor;
    TaskTransferData transferData;
    TreePath dropPath;
    TreePath insertPath;
    Task dropTask;
    Task insertedTask;
    int insertionIndex = 0;

    try {
      if (priorDropPath != null) {
        ((TestTreeModel)testTree.getModel()).setHighlightedTask(null);
        ((TestTreeModel)testTree.getModel()).fireTreeNodesChanged(((TestTreeModel)testTree.getModel()).eventForPathChanged(priorDropPath, priorDropPath.getLastPathComponent()));
        priorDropPath = null;
      }

      if ((flavor = getTaskDataFlavor(dropTargetDropEvent.getCurrentDataFlavors())) == null) {
        throw new UnsupportedFlavorException(dropTargetDropEvent.getCurrentDataFlavors()[0]);
      }
      else {
        transferData = (TaskTransferData)dropTargetDropEvent.getTransferable().getTransferData(flavor);
      }

      if ((dropPath = isPermittedDropLocation(testTree, transferData, dropTargetDropEvent.getLocation(), dropTargetDropEvent.getDropAction())) == null) {

        return false;
      }
      else {
        insertPath = dropPath;
      }

      if (transferData.getTransferType().equals(TransferType.MOVE_OR_COPY)) {

        TreePath dragPath;

        if ((dragPath = ((TestTreeModel)testTree.getModel()).getPathForTask(transferData.getTaskUUID())) == null) {
          throw new IllegalStateException("Unexpectedly missing task");
        }

        if (dropTargetDropEvent.getDropAction() == DnDConstants.ACTION_MOVE) {

          TreeModelEvent deletionEvent;
          Task parentTask;

          deletionEvent = new TreeModelEvent(this, dragPath.getParentPath(), new int[] {testTree.getModel().getIndexOfChild(dragPath.getParentPath().getLastPathComponent(), dragPath.getLastPathComponent())}, new Object[] {dragPath.getLastPathComponent()});

          parentTask = (Task)dragPath.getParentPath().getLastPathComponent();
          switch (palette.getAvatar(parentTask.getClass()).getChildModel()) {
            case NONE:
              throw new IllegalStateException("Attempted removal of a child from a leaf node parent (yes, this makes  no sense)");
            case SINGULAR:
              ((SingularContainer)parentTask).setTask(null);
              break;
            case PLURAL:
              ((PluralContainer)parentTask).remove((Task)dragPath.getLastPathComponent());
              break;
            default:
              throw new UnknownSwitchCaseException(palette.getAvatar(parentTask.getClass()).getChildModel().name());
          }

          ((TestTreeModel)testTree.getModel()).getTestPlan().setChanged(true);
          ((TestTreeModel)testTree.getModel()).fireTreeNodesRemoved(deletionEvent);
          insertedTask = (Task)dragPath.getLastPathComponent();
        }
        else {
          insertedTask = ((Task)dragPath.getLastPathComponent()).deepCopy();
        }
      }
      else {
        insertedTask = transferData.getTaskClass().newInstance();
        insertedTask.setName(palette.getAvatar(insertedTask.getClass()).getName());
      }

      dropTask = (Task)dropPath.getLastPathComponent();
      switch (palette.getAvatar(dropTask.getClass()).getChildModel()) {
        case NONE:
          if ((dropPath.getParentPath() != null) && (PluralContainer.class.isAssignableFrom(dropPath.getParentPath().getLastPathComponent().getClass()))) {
            if ((insertionIndex = ((PluralContainer)dropPath.getParentPath().getLastPathComponent()).indexOf((Task)dropPath.getLastPathComponent())) < 0) {
              throw new IllegalStateException("Unexpectedly missing task");
            }

            ((PluralContainer)dropPath.getParentPath().getLastPathComponent()).add(insertionIndex, insertedTask);
            insertPath = dropPath.getParentPath();
          }
          else {
            throw new IllegalStateException("Attempt to add a child to a leaf node");
          }
          break;
        case SINGULAR:
          ((SingularContainer)dropTask).setTask(insertedTask);
          break;
        case PLURAL:
          insertionIndex = ((PluralContainer)dropTask).size();
          ((PluralContainer)dropTask).add(insertedTask);
          break;
        default:
          throw new UnknownSwitchCaseException(palette.getAvatar(dropTask.getClass()).getChildModel().name());
      }

      ((TestTreeModel)testTree.getModel()).getTestPlan().setChanged(true);
      ((TestTreeModel)testTree.getModel()).fireTreeNodesInserted(new TreeModelEvent(this, insertPath, new int[] {insertionIndex}, new Object[] {insertedTask}));
      ((TestTreeModel)testTree.getModel()).recursivelyExpandPath(insertPath);

      if (insertedTask.isEnabled()) {
        ((TestTreeModel)testTree.getModel()).setEnabledUpstream(true, insertPath.pathByAddingChild(insertedTask));
      }

      return true;
    }
    catch (Exception exception) {
      JavaErrorDialog.showJavaErrorDialog(parentFrame, this, exception);

      return false;
    }
  }
}