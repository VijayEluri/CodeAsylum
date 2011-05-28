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
package com.codeasylum.stress.api.visualization;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.smallmind.nutsnbolts.util.StringUtilities;
import org.smallmind.swing.ColorUtilities;

public class EnumListCellRenderer implements ListCellRenderer {

  private boolean displayCase;

  public EnumListCellRenderer () {

    this(true);
  }

  public EnumListCellRenderer (boolean displayCase) {

    this.displayCase = displayCase;
  }

  @Override
  public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    JLabel cellLabel;

    cellLabel = new JLabel((displayCase) ? StringUtilities.toDisplayCase(((Enum)value).name(), '_') : ((Enum)value).name());
    cellLabel.setOpaque(true);

    if (isSelected) {
      cellLabel.setBackground(ColorUtilities.HIGHLIGHT_COLOR);
      cellLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), BorderFactory.createMatteBorder(0, 1, 0, 1, ColorUtilities.HIGHLIGHT_COLOR)));
    }
    else {
      cellLabel.setBackground(list.getBackground());
      cellLabel.setBorder(BorderFactory.createMatteBorder(1, 2, 1, 2, list.getBackground()));
    }

    return cellLabel;
  }
}
