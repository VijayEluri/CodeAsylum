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
package com.codeasylum.stress.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.codeasylum.stress.api.TaskAvatar;
import com.codeasylum.stress.api.TaskType;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.dragndrop.GhostPanel;

public class PalettePanel extends JPanel implements Translucent, ListSelectionListener {

  private Jormungandr jormungandr;
  private LinkedList<JList> paletteListList = new LinkedList<JList>();
  private boolean selectionSensitive = true;

  public PalettePanel (Jormungandr jormungandr, GhostPanel ghostPanel, TaskPalette palette) {

    GroupLayout layout;
    GroupLayout.ParallelGroup horizontalGroup;
    GroupLayout.SequentialGroup verticalGroup;
    PaletteListCellRenderer cellRenderer;
    LinkedList<JComponent> componentList;
    JComponent widestComponent = null;
    int widestWidth;

    this.jormungandr = jormungandr;

    setLayout(layout = new GroupLayout(this));

    layout.setAutoCreateContainerGaps(false);

    layout.setHorizontalGroup(horizontalGroup = layout.createParallelGroup());
    layout.setVerticalGroup(verticalGroup = layout.createSequentialGroup());

    cellRenderer = new PaletteListCellRenderer();
    componentList = new LinkedList<JComponent>();
    for (TaskType taskType : TaskType.values()) {

      List<TaskAvatar<?>> avatarList;

      if ((avatarList = palette.getAvatars(taskType)) != null) {

        JLabel paletteLabel;
        JList paletteList;

        componentList.add(paletteLabel = new JLabel(taskType.getDisplay(), SwingConstants.LEFT));
        paletteLabel.setOpaque(true);
        paletteLabel.setForeground(ColorUtilities.HIGHLIGHT_COLOR.darker());
        paletteLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(5, 3, 2, 5)));

        componentList.add(paletteList = new JList(avatarList.toArray()));
        paletteListList.add(paletteList);

        paletteList.setCellRenderer(cellRenderer);
        paletteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        new PaletteDragHandler(ghostPanel, paletteList);

        paletteLabel.setBackground(paletteList.getBackground());

        widestComponent = widest(widestComponent, paletteLabel);
        widestComponent = widest(widestComponent, paletteList);

        paletteList.addListSelectionListener(this);
      }
    }

    widestWidth = (int)widestComponent.getPreferredSize().getWidth() + (Integer)UIManager.getDefaults().get("ScrollBar.width");
    for (JComponent component : componentList) {
      horizontalGroup.addComponent(component, widestWidth, widestWidth, widestWidth);
      verticalGroup.addComponent(component);
    }

    this.setMinimumSize(new Dimension(widestWidth, (int)getMinimumSize().getHeight()));
    this.setPreferredSize(new Dimension(widestWidth, (int)getPreferredSize().getHeight()));
    this.setMaximumSize(new Dimension(widestWidth, (int)getMaximumSize().getHeight()));
  }

  private JComponent widest (JComponent previous, JComponent current) {

    return (previous == null) ? current : (current.getPreferredSize().getWidth() > previous.getPreferredSize().getWidth()) ? current : previous;
  }

  @Override
  public synchronized void blur () {

    removeSelections(null);
  }

  @Override
  public synchronized void valueChanged (ListSelectionEvent listSelectionEvent) {

    removeSelections((JList)listSelectionEvent.getSource());
  }

  private void removeSelections (JList protectedList) {

    int vanishingIndex;

    if (selectionSensitive) {
      selectionSensitive = false;

      if (protectedList != null) {
        jormungandr.solidify(Workspace.PALETTE);
      }

      for (JList paletteList : paletteListList) {
        if (paletteList != protectedList) {
          if ((vanishingIndex = paletteList.getSelectedIndex()) >= 0) {
            paletteList.removeSelectionInterval(vanishingIndex, vanishingIndex);
          }
        }
      }

      selectionSensitive = true;
    }
  }
}
