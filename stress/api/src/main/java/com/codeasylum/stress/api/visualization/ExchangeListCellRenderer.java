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
package com.codeasylum.stress.api.visualization;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.ComponentUtilities;

public class ExchangeListCellRenderer implements ListCellRenderer {

  private static final ImageIcon SUCCESS_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/success_16.png"));
  private static final ImageIcon FAILURE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/failure_16.png"));
  private static final int SCROLL_BAR_WIDTH = (Integer)UIManager.getDefaults().get("ScrollBar.width");

  @Override
  public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    JPanel exchangePanel;
    JLabel serialLabel;
    JLabel hostLabel;
    JLabel taskLabel;
    JLabel durationLabel;

    exchangePanel = new JPanel();
    exchangePanel.setLayout(new BoxLayout(exchangePanel, BoxLayout.X_AXIS));
    exchangePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(2, 0, 1, 0)));

    exchangePanel.add(serialLabel = new JLabel(String.valueOf(((ExchangeWrapper)value).getSerialNumber()), JLabel.RIGHT));
    serialLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
    ComponentUtilities.setMinimumWidth(serialLabel, 50);
    ComponentUtilities.setPreferredWidth(serialLabel, 50);
    ComponentUtilities.setMaximumWidth(serialLabel, 50);

    exchangePanel.add(hostLabel = new JLabel(((ExchangeWrapper)value).getExchange().getHostId(), JLabel.LEFT));
    hostLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 1));
    ComponentUtilities.setMinimumWidth(hostLabel, 100);
    ComponentUtilities.setPreferredWidth(hostLabel, 100);
    ComponentUtilities.setMaximumWidth(hostLabel, 100);

    exchangePanel.add(taskLabel = new JLabel(((ExchangeWrapper)value).getExchange().getTaskName(), ((ExchangeWrapper)value).getExchange().isSuccess() ? SUCCESS_ICON : FAILURE_ICON, JLabel.LEFT));
    taskLabel.setHorizontalTextPosition(JLabel.RIGHT);
    taskLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 1));
    ComponentUtilities.setMinimumWidth(taskLabel, 175);
    ComponentUtilities.setPreferredWidth(taskLabel, 175);
    ComponentUtilities.setMaximumWidth(taskLabel, 175);

    exchangePanel.add(durationLabel = new JLabel(String.valueOf(((ExchangeWrapper)value).getExchange().getStopMillis() - ((ExchangeWrapper)value).getExchange().getStartMillis()) + " ms", JLabel.RIGHT));
    durationLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 5 + SCROLL_BAR_WIDTH));
    ComponentUtilities.setMinimumWidth(durationLabel, 100 + SCROLL_BAR_WIDTH);
    ComponentUtilities.setPreferredWidth(durationLabel, 100 + SCROLL_BAR_WIDTH);
    ComponentUtilities.setMaximumWidth(durationLabel, 100 + SCROLL_BAR_WIDTH);

    if (isSelected) {
      serialLabel.setForeground(ColorUtilities.TEXT_COLOR);
      hostLabel.setForeground(ColorUtilities.TEXT_COLOR);
      taskLabel.setForeground(ColorUtilities.TEXT_COLOR);
      durationLabel.setForeground(ColorUtilities.TEXT_COLOR);
      exchangePanel.setBackground(ColorUtilities.HIGHLIGHT_COLOR);
    }
    else {
      serialLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      hostLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      taskLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      durationLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      exchangePanel.setBackground(list.getBackground());
    }

    return exchangePanel;
  }
}
