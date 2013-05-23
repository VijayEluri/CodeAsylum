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
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.smallmind.swing.ComponentUtilities;
import org.smallmind.swing.icon.SwatchIcon;

public class MonitorLegendListCellRenderer implements ListCellRenderer {

  @Override
  public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    JPanel cellPanel;
    JCheckBox visibilityCheckBox;
    JLabel nameLabel;

    cellPanel = new JPanel();
    cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.X_AXIS));
    cellPanel.setOpaque(false);
    cellPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

    cellPanel.add(visibilityCheckBox = new JCheckBox());
    visibilityCheckBox.setSelected(((MonitorLegend)value).isVisible());
    visibilityCheckBox.setOpaque(false);

    cellPanel.add(nameLabel = new JLabel(((MonitorLegend)value).getName(), new SwatchIcon(10, ((MonitorLegend)value).getColor()), JLabel.LEFT));
    ComponentUtilities.setMinimumWidth(nameLabel, 175);
    ComponentUtilities.setPreferredWidth(nameLabel, 175);
    ComponentUtilities.setMaximumWidth(nameLabel, 175);

    return cellPanel;
  }
}
