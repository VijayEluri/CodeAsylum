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
package com.codeasylum.stress.api;

import java.rmi.RemoteException;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import com.codeasylum.stress.api.visualization.RootTaskConfigPanel;

public class RootTaskAvatar extends AbstractTaskAvatar<RootTask> {

  private static final ImageIcon ICON_24 = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/root_24.png"));
  private static final ImageIcon ICON_32 = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/root_32.png"));

  private final MapOfOne mapOfOne = new MapOfOne();

  @Override
  public Class<RootTask> getTaskClass () {

    return RootTask.class;
  }

  @Override
  public String getName () {

    return "Test";
  }

  @Override
  public TaskType getType () {

    return TaskType.CONTROLLER;
  }

  @Override
  public ChildModel getChildModel () {

    return ChildModel.SINGULAR;
  }

  @Override
  public ImageIcon getIcon24 () {

    return ICON_24;
  }

  @Override
  public ImageIcon getIcon32 () {

    return ICON_32;
  }

  @Override
  public JComponent getVisualization (JFrame parentFrame, TestExecutor testExecutor, RootTask task)
    throws RemoteException {

    return mapOfOne.getRootTaskConfigPanel(parentFrame, testExecutor);
  }

  private class MapOfOne {

    private TestExecutor testExecutor;
    private RootTaskConfigPanel rootTaskConfigPanel;

    public synchronized RootTaskConfigPanel getRootTaskConfigPanel (JFrame parentFrame, TestExecutor testExecutor)
      throws RemoteException {

      if (this.testExecutor == testExecutor) {

        return (rootTaskConfigPanel == null) ? rootTaskConfigPanel = new RootTaskConfigPanel(parentFrame, testExecutor) : rootTaskConfigPanel;
      }
      else {

        this.testExecutor = testExecutor;

        return rootTaskConfigPanel = new RootTaskConfigPanel(parentFrame, testExecutor);
      }
    }
  }
}
