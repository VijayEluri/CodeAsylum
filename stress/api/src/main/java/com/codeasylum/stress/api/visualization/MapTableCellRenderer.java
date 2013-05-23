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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.label.PlainLabel;

public class MapTableCellRenderer implements TableCellRenderer {

  @Override
  public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    PlainLabel cellLabel;

    cellLabel = new PlainLabel(value.toString(), SwingConstants.LEFT);
    cellLabel.setOpaque(true);
    cellLabel.setBackground(isSelected ? ColorUtilities.HIGHLIGHT_COLOR : table.getBackground());

    if (isSelected) {
      cellLabel.setForeground(ColorUtilities.TEXT_COLOR);
      cellLabel.setBorder(BorderFactory.createLineBorder(ColorUtilities.HIGHLIGHT_COLOR, 2));
    }
    else {
      cellLabel.setForeground(ColorUtilities.TEXT_TEXT_COLOR);
      cellLabel.setBorder(BorderFactory.createLineBorder(table.getBackground(), 2));
    }

    return cellLabel;
  }
}
