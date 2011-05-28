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

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import com.codeasylum.stress.api.RootTask;
import com.codeasylum.stress.api.Task;
import org.smallmind.swing.dragndrop.DragHandler;
import org.smallmind.swing.dragndrop.GhostPanel;

public class TestDragHandler extends DragHandler {

  private TaskPalette palette;
  private AtomicReference<Task> taskReference = new AtomicReference<Task>();

  public TestDragHandler (GhostPanel ghostPanel, JTree testTree, TaskPalette palette) {

    super(ghostPanel, testTree, DnDConstants.ACTION_COPY_OR_MOVE);

    this.palette = palette;
  }

  @Override
  public Transferable getTransferable (DragGestureEvent dragGestureEvent) {

    JTree testTree = (JTree)dragGestureEvent.getComponent();
    TreePath dragPath;

    if ((dragPath = testTree.getPathForLocation((int)dragGestureEvent.getDragOrigin().getX(), (int)dragGestureEvent.getDragOrigin().getY())) != null) {
      if ((dragPath.getLastPathComponent() != null) && (!RootTask.class.isAssignableFrom(dragPath.getLastPathComponent().getClass()))) {
        taskReference.set((Task)dragPath.getLastPathComponent());
        testTree.getCellEditor().cancelCellEditing();

        return new TaskTransferable((Task)dragPath.getLastPathComponent());
      }
    }

    return null;
  }

  @Override
  public Icon getDragIcon (DragGestureEvent dragGestureEvent, Point offset) {

    return palette.getAvatar(taskReference.get().getClass()).getIcon24();
  }

  @Override
  public void dragTerminated (int action, boolean success) {

    taskReference.set(null);
  }
}
