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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import com.codeasylum.stress.api.TaskAvatar;
import org.smallmind.swing.ColorUtilities;

public class PaletteListCellRenderer implements ListCellRenderer {

  @Override
  public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    JLabel cellLabel;

    cellLabel = new JLabel(((TaskAvatar)value).getName(), ((TaskAvatar)value).getIcon32(), SwingConstants.LEFT);
    cellLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(3, 3, 2, 5)));
    cellLabel.setOpaque(true);

    if (isSelected) {
      cellLabel.setForeground(ColorUtilities.TEXT_COLOR);
      cellLabel.setBackground(ColorUtilities.HIGHLIGHT_COLOR);
    }
    else {
      cellLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      cellLabel.setBackground(list.getBackground());
    }

    return cellLabel;
  }
}
