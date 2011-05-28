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

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.smallmind.swing.ColorUtilities;

public class MapTableHeaderRenderer implements TableCellRenderer {

  @Override
  public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    if (column == 0) {

      JLabel keyLabel;

      keyLabel = new JLabel("Key", JLabel.LEFT);
      keyLabel.setOpaque(true);
      keyLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
      keyLabel.setForeground(ColorUtilities.TEXT_COLOR);
      keyLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR)));

      return keyLabel;
    }
    else {

      JLabel valueLabel;

      valueLabel = new JLabel((String)value, JLabel.LEFT);
      valueLabel.setOpaque(true);
      valueLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
      valueLabel.setForeground(ColorUtilities.TEXT_COLOR);
      valueLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

      return valueLabel;
    }
  }
}
