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

import java.awt.GridLayout;
import java.rmi.RemoteException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.codeasylum.stress.api.TestExecutor;

public class RootTaskConfigPanel extends JPanel {

  private static final ImageIcon EXECUTE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/execute_24.png"));
  private static final ImageIcon MONITOR_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/monitor_24.png"));
  private static final ImageIcon DEBUG_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/debug_view_24.png"));
  private static final ImageIcon NETWORK_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/network_24.png"));

  public RootTaskConfigPanel (JFrame parentFrame, TestExecutor testExecutor)
    throws RemoteException {

    JTabbedPane testTabbedPane;
    RootTaskExecutionPanel executionPanel;
    RootTaskMonitorPanel monitorPanel;
    RootTaskDebugPanel debugPanel;
    RootTaskNetworkPanel networkPanel;

    setLayout(new GridLayout(1, 1));

    executionPanel = new RootTaskExecutionPanel(parentFrame, testExecutor);
    executionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    monitorPanel = new RootTaskMonitorPanel(parentFrame, testExecutor);
    monitorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    debugPanel = new RootTaskDebugPanel(parentFrame, testExecutor);
    debugPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    networkPanel = new RootTaskNetworkPanel(parentFrame, testExecutor);
    networkPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    add(testTabbedPane = new JTabbedPane());
    testTabbedPane.addTab("Execute", EXECUTE_ICON, executionPanel);
    testTabbedPane.addTab("Monitor", MONITOR_ICON, monitorPanel);
    testTabbedPane.addTab("Debug", DEBUG_ICON, debugPanel);
    testTabbedPane.addTab("Network", NETWORK_ICON, networkPanel);
  }
}
