/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
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
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.ComponentUtilities;

public class DebugListCellRenderer implements ListCellRenderer {

  private static final int SCROLL_BAR_WIDTH = (Integer)UIManager.getDefaults().get("ScrollBar.width");

  @Override
  public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    JPanel debugPanel;
    JLabel serialLabel;
    JLabel hostLabel;
    JLabel taskLabel;
    JLabel messageLabel;

    debugPanel = new JPanel();
    debugPanel.setLayout(new BoxLayout(debugPanel, BoxLayout.X_AXIS));
    debugPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(2, 0, 1, 0)));

    debugPanel.add(serialLabel = new JLabel(String.valueOf(((DebugWrapper)value).getSerialNumber()), JLabel.RIGHT));
    serialLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
    ComponentUtilities.setMinimumWidth(serialLabel, 50);
    ComponentUtilities.setPreferredWidth(serialLabel, 50);
    ComponentUtilities.setMaximumWidth(serialLabel, 50);

    debugPanel.add(hostLabel = new JLabel(((DebugWrapper)value).getDebug().getHostId(), JLabel.LEFT));
    hostLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 1));
    ComponentUtilities.setMinimumWidth(hostLabel, 100);
    ComponentUtilities.setPreferredWidth(hostLabel, 100);
    ComponentUtilities.setMaximumWidth(hostLabel, 100);

    debugPanel.add(taskLabel = new JLabel(((DebugWrapper)value).getDebug().getTaskName()));
    taskLabel.setHorizontalTextPosition(JLabel.RIGHT);
    taskLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 1));
    ComponentUtilities.setMinimumWidth(taskLabel, 150);
    ComponentUtilities.setPreferredWidth(taskLabel, 150);
    ComponentUtilities.setMaximumWidth(taskLabel, 150);

    debugPanel.add(messageLabel = new JLabel(String.valueOf(((DebugWrapper)value).getDebug().getMessage())));
    messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5 + SCROLL_BAR_WIDTH));

    if (isSelected) {
      serialLabel.setForeground(ColorUtilities.TEXT_COLOR);
      hostLabel.setForeground(ColorUtilities.TEXT_COLOR);
      taskLabel.setForeground(ColorUtilities.TEXT_COLOR);
      messageLabel.setForeground(ColorUtilities.TEXT_COLOR);
      debugPanel.setBackground(ColorUtilities.HIGHLIGHT_COLOR);
    }
    else {
      serialLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      hostLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      taskLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      messageLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      debugPanel.setBackground(list.getBackground());
    }

    return debugPanel;
  }
}
