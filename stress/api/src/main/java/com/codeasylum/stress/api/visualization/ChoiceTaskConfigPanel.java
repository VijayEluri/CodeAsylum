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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import com.codeasylum.stress.api.ChoiceTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.SmallMindGrayFilter;
import org.smallmind.swing.slider.MultiThumbSlider;
import org.smallmind.swing.slider.ThumbEvent;
import org.smallmind.swing.slider.ThumbListener;

public class ChoiceTaskConfigPanel extends JPanel implements ActionListener, ThumbListener {

  private static final ImageIcon ADD_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/add_16.png"));
  private static final ImageIcon DELETE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/delete_16.png"));

  private TestPlan testPlan;
  private ChoiceTask task;
  private MultiThumbSlider choiceSlider;
  private JButton addButton;
  private JButton deleteButton;

  public ChoiceTaskConfigPanel (TestPlan testPlan, ChoiceTask task) {

    GroupLayout groupLayout;
    JLabel sliderLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    sliderLabel = new JLabel("Percentile Ranges:");

    addButton = new JButton(ADD_ICON);
    addButton.setMargin(new Insets(2, 2, 2, 2));
    addButton.setDisabledIcon(new ImageIcon(SmallMindGrayFilter.createDisabledImage(ADD_ICON.getImage())));
    addButton.setFocusable(false);
    addButton.setToolTipText("add a percentile range");
    addButton.addActionListener(this);

    deleteButton = new JButton(DELETE_ICON);
    deleteButton.setMargin(new Insets(2, 2, 2, 2));
    deleteButton.setDisabledIcon(new ImageIcon(SmallMindGrayFilter.createDisabledImage(DELETE_ICON.getImage())));
    deleteButton.setEnabled(task.getPercentages().length > 0);
    deleteButton.setFocusable(false);
    deleteButton.setToolTipText("remove a percentile range");
    deleteButton.addActionListener(this);

    choiceSlider = new MultiThumbSlider(MultiThumbSlider.HORIZONTAL);
    choiceSlider.setMinorTickSpacing(2);
    choiceSlider.setMajorTickSpacing(10);

    for (int percentage : task.getPercentages()) {
      choiceSlider.addThumb(percentage);
    }

    choiceSlider.getModel().addThumbListener(this);

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addComponent(sliderLabel)
      .addGroup(groupLayout.createSequentialGroup().addComponent(deleteButton).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(choiceSlider).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(addButton)));
    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addComponent(sliderLabel)
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(deleteButton).addComponent(choiceSlider).addComponent(addButton)));
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent.getSource() == deleteButton) {

      int thumbIndex = -1;
      int maxValue = -1;
      int thumbValue;

      for (int index = 0; index < choiceSlider.getThumbCount(); index++) {
        if ((thumbValue = choiceSlider.getThumbValue(index)) > maxValue) {
          thumbIndex = index;
          maxValue = thumbValue;
        }
      }

      if (thumbIndex >= 0) {
        choiceSlider.removeThumb(thumbIndex);
        deleteButton.setEnabled(choiceSlider.getModel().getThumbCount() > 0);
      }
    }
    else if (actionEvent.getSource() == addButton) {

      int[] thumbValues = choiceSlider.getThumbValues();
      int lastThumbValue = 0;
      int maxRangeStart = 0;
      int maxRangeEnd = 0;

      for (int thumbValue : thumbValues) {
        if (thumbValue - lastThumbValue > maxRangeEnd - maxRangeStart) {
          maxRangeStart = lastThumbValue;
          maxRangeEnd = thumbValue;
        }
        lastThumbValue = thumbValue;
      }
      if (100 - lastThumbValue > maxRangeEnd - maxRangeStart) {
        maxRangeStart = lastThumbValue;
        maxRangeEnd = 100;
      }

      choiceSlider.addThumb(maxRangeStart + ((maxRangeEnd - maxRangeStart) / 2));
      deleteButton.setEnabled(choiceSlider.getModel().getThumbCount() > 0);
    }
  }

  @Override
  public synchronized void thumbAdded (ThumbEvent thumbEvent) {

    task.setPercentages(choiceSlider.getThumbValues());
    testPlan.setChanged(true);
  }

  @Override
  public synchronized void thumbRemoved (ThumbEvent thumbEvent) {

    task.setPercentages(choiceSlider.getThumbValues());
    testPlan.setChanged(true);
  }

  @Override
  public synchronized void thumbMoved (ThumbEvent thumbEvent) {

    if (!thumbEvent.isAdjusting()) {
      task.setPercentages(choiceSlider.getThumbValues());
      testPlan.setChanged(true);
    }
  }
}
