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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import com.codeasylum.stress.api.Task;
import com.codeasylum.stress.api.TaskAvatar;

public class TaskTransferable implements Transferable {

  private DataFlavor dataFlavor;

  public TaskTransferable (Task task) {

    dataFlavor = new TaskDataFlavor(new TaskTransferData(TransferType.MOVE_OR_COPY, task.getClass(), task.getUUID()));
  }

  public TaskTransferable (TaskAvatar taskAvatar) {

    dataFlavor = new TaskDataFlavor(new TaskTransferData(TransferType.CREATE, taskAvatar.getTaskClass(), null));
  }

  @Override
  public DataFlavor[] getTransferDataFlavors () {

    return new DataFlavor[] {dataFlavor};
  }

  @Override
  public boolean isDataFlavorSupported (DataFlavor flavor) {

    return flavor instanceof TaskDataFlavor;
  }

  @Override
  public Object getTransferData (DataFlavor flavor)
    throws UnsupportedFlavorException, IOException {

    if (!isDataFlavorSupported(flavor)) {
      throw new UnsupportedFlavorException(flavor);
    }

    return ((TaskDataFlavor)flavor).getTransferData();
  }
}
