/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
 * 
 * This file is part of the SmallMind Code Project.
 * 
 * The SmallMind Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The SmallMind Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The SmallMind Code Project. If not, see
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
import javax.swing.JList;
import com.codeasylum.stress.api.TaskAvatar;
import org.smallmind.swing.dragndrop.DragHandler;
import org.smallmind.swing.dragndrop.GhostPanel;

public class PaletteDragHandler extends DragHandler {

  private AtomicReference<TaskAvatar> avatarReference = new AtomicReference<TaskAvatar>();

  public PaletteDragHandler (GhostPanel ghostPanel, JList paletteList) {

    super(ghostPanel, paletteList, DnDConstants.ACTION_MOVE);
  }

  @Override
  public Transferable getTransferable (DragGestureEvent dragGestureEvent) {

    JList paletteList = (JList)dragGestureEvent.getComponent();
    TaskAvatar taskAvatar = (TaskAvatar)paletteList.getModel().getElementAt(paletteList.locationToIndex(dragGestureEvent.getDragOrigin()));

    avatarReference.compareAndSet(null, taskAvatar);

    return new TaskTransferable(taskAvatar);
  }

  @Override
  public Icon getDragIcon (DragGestureEvent dragGestureEvent, Point offset) {

    TaskAvatar taskAvatar;

    if ((taskAvatar = avatarReference.get()) != null) {
      offset.setLocation(-12, -12);

      return taskAvatar.getIcon24();
    }

    return null;
  }

  @Override
  public void dragTerminated (int action, boolean success) {

    avatarReference.set(null);
  }
}
