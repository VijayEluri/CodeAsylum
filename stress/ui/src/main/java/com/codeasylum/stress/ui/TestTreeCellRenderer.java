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

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeCellRenderer;
import com.codeasylum.stress.api.Task;
import com.codeasylum.stress.api.TaskAvatar;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.SmallMindGrayFilter;

public class TestTreeCellRenderer implements TreeCellRenderer {

  private static final ImageIcon SINGULAR_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/ui/singular_16.png"));
  private static final ImageIcon GRAY_SINGULAR_ICON = new ImageIcon(SmallMindGrayFilter.createDisabledImage(SINGULAR_ICON.getImage()));
  private static final ImageIcon PLURAL_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/ui/plural_16.png"));
  private static final ImageIcon GRAY_PLURAL_ICON = new ImageIcon(SmallMindGrayFilter.createDisabledImage(PLURAL_ICON.getImage()));

  private TaskPalette palette;

  public TestTreeCellRenderer (TaskPalette palette) {

    this.palette = palette;
  }

  @Override
  public Component getTreeCellRendererComponent (JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    TaskAvatar taskAvatar = palette.getAvatar(((Task)value).getClass());
    JPanel taskPanel;
    JCheckBox enabledCheckBox;
    JLabel iconLabel;
    JLabel nameLabel;
    JLabel indicatorLabel;

    taskPanel = new JPanel();
    taskPanel.setOpaque(false);
    taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.X_AXIS));

    taskPanel.add(enabledCheckBox = new JCheckBox());
    enabledCheckBox.setOpaque(false);
    enabledCheckBox.setSelected(((Task)value).isEnabled());

    taskPanel.add(iconLabel = new JLabel(((Task)value).isEnabled() ? taskAvatar.getIcon24() : taskAvatar.getGrayIcon24()));
    iconLabel.setHorizontalAlignment(JLabel.LEFT);
    iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));

    switch (taskAvatar.getChildModel()) {
      case NONE:
        break;
      case SINGULAR:
        taskPanel.add(indicatorLabel = new JLabel(((Task)value).isEnabled() ? SINGULAR_ICON : GRAY_SINGULAR_ICON));
        indicatorLabel.setHorizontalAlignment(JLabel.LEFT);
        indicatorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        break;
      case PLURAL:
        taskPanel.add(indicatorLabel = new JLabel(((Task)value).isEnabled() ? PLURAL_ICON : GRAY_PLURAL_ICON));
        indicatorLabel.setHorizontalAlignment(JLabel.LEFT);
        indicatorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        break;
      default:
        throw new UnknownSwitchCaseException(taskAvatar.getChildModel().name());
    }

    taskPanel.add(nameLabel = new JLabel(((Task)value).getName(), SwingConstants.LEFT));
    nameLabel.setOpaque(true);

    taskPanel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));

    if (value.equals(((TestTreeModel)tree.getModel()).getHighlightedTask())) {
      nameLabel.setForeground(((Task)value).isEnabled() ? Color.BLACK : Color.GRAY);
      nameLabel.setBackground(ColorUtilities.INVERSE_HIGHLIGHT_COLOR);
      nameLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(1, 4, 1, 4)));
    }
    else if (selected) {
      nameLabel.setForeground(((Task)value).isEnabled() ? ColorUtilities.TEXT_COLOR : Color.GRAY);
      nameLabel.setBackground(ColorUtilities.HIGHLIGHT_COLOR);
      nameLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), BorderFactory.createEmptyBorder(1, 4, 1, 4)));
    }
    else {
      nameLabel.setForeground(((Task)value).isEnabled() ? ColorUtilities.TEXT_TEXT_COLOR : Color.GRAY);
      nameLabel.setBackground(tree.getBackground());
      nameLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
    }

    return taskPanel;
  }
}
