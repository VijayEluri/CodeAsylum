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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import com.codeasylum.stress.api.ChildModel;
import com.codeasylum.stress.api.PluralContainer;
import com.codeasylum.stress.api.SingularContainer;
import com.codeasylum.stress.api.Task;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class TestTreeModel implements TreeModel {

  private final WeakEventListenerList<TreeModelListener> listenerList = new WeakEventListenerList<TreeModelListener>();
  private final AtomicReference<Task> highlightedTaskRef = new AtomicReference<Task>();

  private TaskPalette palette;
  private JTree testTree;
  private TestPlan testPlan;

  public TestTreeModel (TaskPalette palette, JTree testTree, TestPlan testPlan) {

    this.palette = palette;
    this.testTree = testTree;
    this.testPlan = testPlan;
  }

  public void setHighlightedTask (Task task) {

    highlightedTaskRef.set(task);
  }

  public Task getHighlightedTask () {

    return highlightedTaskRef.get();
  }

  public TestPlan getTestPlan () {

    return testPlan;
  }

  @Override
  public Object getRoot () {

    return testPlan.getRootTask();
  }

  public void recursivelyExpandPath (TreePath currentPath) {

    testTree.expandPath(currentPath);

    switch (palette.getAvatar((Class<? extends Task>)currentPath.getLastPathComponent().getClass()).getChildModel()) {
      case NONE:
        break;
      case SINGULAR:

        Task singularTask;

        if ((singularTask = ((SingularContainer)currentPath.getLastPathComponent()).getTask()) != null) {
          recursivelyExpandPath(currentPath.pathByAddingChild(singularTask));
        }
        break;
      case PLURAL:
        for (Task pluralTask : ((PluralContainer)currentPath.getLastPathComponent())) {
          recursivelyExpandPath(currentPath.pathByAddingChild(pluralTask));
        }
        break;
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)currentPath.getLastPathComponent().getClass()).getChildModel().name());
    }
  }

  public void setEnabled (boolean enabled, TreePath subPath) {

    setEnabledUpstream(enabled, subPath);
    setEnabledDownstream(enabled, subPath);
  }

  public void setEnabledUpstream (boolean enabled, TreePath subPath) {

    TreePath parentPath = null;

    if (enabled) {
      for (int count = 0; count < subPath.getPathCount() - 1; count++) {
        ((Task)subPath.getPathComponent(count)).setEnabled(enabled);
        fireTreeNodesChanged(new TreeModelEvent(this, parentPath, (count == 0) ? null : new int[] {getIndexOfChild(subPath.getPathComponent(count - 1), subPath.getPathComponent(count))}, new Object[] {subPath.getPathComponent(count)}));
        parentPath = (parentPath == null) ? new TaskTreePath((Task)subPath.getPathComponent(count)) : parentPath.pathByAddingChild(subPath.getPathComponent(count));
      }
    }
  }

  private void setEnabledDownstream (boolean enabled, TreePath subPath) {

    TreePath parentPath = subPath.getParentPath();

    ((Task)subPath.getLastPathComponent()).setEnabled(enabled);
    fireTreeNodesChanged(new TreeModelEvent(this, parentPath, (parentPath == null) ? null : new int[] {getIndexOfChild(parentPath.getLastPathComponent(), subPath.getLastPathComponent())}, new Object[] {subPath.getLastPathComponent()}));

    switch (palette.getAvatar((Class<? extends Task>)subPath.getLastPathComponent().getClass()).getChildModel()) {
      case NONE:
        break;
      case SINGULAR:

        Task singularTask;

        if ((singularTask = ((SingularContainer)subPath.getLastPathComponent()).getTask()) != null) {
          setEnabledDownstream(enabled, subPath.pathByAddingChild(singularTask));
        }
        break;
      case PLURAL:
        for (Task pluralTask : ((PluralContainer)subPath.getLastPathComponent())) {
          setEnabledDownstream(enabled, subPath.pathByAddingChild(pluralTask));
        }
        break;
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)subPath.getLastPathComponent().getClass()).getChildModel().name());
    }
  }

  public TreePath getPathForTask (UUID uuid) {

    return constructPath(new TaskTreePath(testPlan.getRootTask()), uuid);
  }

  private TreePath constructPath (TreePath currentPath, UUID uuid) {

    if (((Task)currentPath.getLastPathComponent()).getUUID().equals(uuid)) {
      return currentPath;
    }

    switch (palette.getAvatar((Class<? extends Task>)currentPath.getLastPathComponent().getClass()).getChildModel()) {
      case NONE:
        return null;
      case SINGULAR:

        Task singularTask;

        if ((singularTask = ((SingularContainer)currentPath.getLastPathComponent()).getTask()) != null) {

          return constructPath(currentPath.pathByAddingChild(singularTask), uuid);
        }

        return null;
      case PLURAL:

        TreePath possiblePath;

        for (Task pluralTask : ((PluralContainer)currentPath.getLastPathComponent())) {
          if ((possiblePath = constructPath(currentPath.pathByAddingChild(pluralTask), uuid)) != null) {

            return possiblePath;
          }
        }

        return null;
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)currentPath.getLastPathComponent().getClass()).getChildModel().name());
    }
  }

  @Override
  public Object getChild (Object parent, int index) {

    switch (palette.getAvatar((Class<? extends Task>)parent.getClass()).getChildModel()) {
      case NONE:
        return null;
      case SINGULAR:
        if (index > 0) {
          throw new IndexOutOfBoundsException();
        }

        return ((SingularContainer)parent).getTask();
      case PLURAL:
        return ((PluralContainer)parent).getChild(index);
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)parent.getClass()).getChildModel().name());
    }
  }

  @Override
  public int getChildCount (Object parent) {

    switch (palette.getAvatar((Class<? extends Task>)parent.getClass()).getChildModel()) {
      case NONE:
        return 0;
      case SINGULAR:
        return (((SingularContainer)parent).getTask() == null) ? 0 : 1;
      case PLURAL:
        return ((PluralContainer)parent).size();
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)parent.getClass()).getChildModel().name());
    }
  }

  @Override
  public boolean isLeaf (Object node) {

    return palette.getAvatar((Class<? extends Task>)node.getClass()).getChildModel().equals(ChildModel.NONE);
  }

  protected TreeModelEvent eventForPathChanged (TreePath path, Object newValue) {

    TreePath parentPath = path.getParentPath();

    if (parentPath == null) {
      return new TreeModelEvent(this, parentPath, null, new Object[] {newValue});
    }
    else {

      int childIndex;

      if ((childIndex = getIndexOfChild(parentPath.getLastPathComponent(), path.getLastPathComponent())) < 0) {
        throw new IllegalStateException("Missing an expected child element");
      }

      return new TreeModelEvent(this, parentPath, new int[] {childIndex}, new Object[] {newValue});
    }
  }

  @Override
  public void valueForPathChanged (TreePath path, Object newValue) {

    fireTreeNodesChanged(eventForPathChanged(path, newValue));
  }

  @Override
  public int getIndexOfChild (Object parent, Object child) {

    switch (palette.getAvatar((Class<? extends Task>)parent.getClass()).getChildModel()) {
      case NONE:
        return -1;
      case SINGULAR:
        return ((((SingularContainer)parent).getTask() != null) && ((SingularContainer)parent).getTask().equals(child)) ? 0 : -1;
      case PLURAL:

        int index = 0;

        for (Task task : (PluralContainer)parent) {
          if (task.equals(child)) {
            return index;
          }

          index++;
        }

        return -1;
      default:
        throw new UnknownSwitchCaseException(palette.getAvatar((Class<? extends Task>)parent.getClass()).getChildModel().name());
    }
  }

  protected synchronized void fireTreeNodesChanged (TreeModelEvent treeModelEvent) {

    synchronized (listenerList) {
      for (TreeModelListener treeModelListener : listenerList) {
        treeModelListener.treeNodesChanged(treeModelEvent);
      }
    }
  }

  protected synchronized void fireTreeNodesInserted (TreeModelEvent treeModelEvent) {

    synchronized (listenerList) {
      for (TreeModelListener treeModelListener : listenerList) {
        treeModelListener.treeNodesInserted(treeModelEvent);
      }
    }
  }

  protected synchronized void fireTreeNodesRemoved (TreeModelEvent treeModelEvent) {

    synchronized (listenerList) {
      for (TreeModelListener treeModelListener : listenerList) {
        treeModelListener.treeNodesRemoved(treeModelEvent);
      }
    }
  }

  @Override
  public synchronized void addTreeModelListener (TreeModelListener treeModelListener) {

    listenerList.addListener(treeModelListener);
  }

  @Override
  public synchronized void removeTreeModelListener (TreeModelListener treeModelListener) {

    listenerList.removeListener(treeModelListener);
  }
}
