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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import com.codeasylum.stress.api.DelayTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.ColorUtilities;

public class DelayTaskConfigPanel extends JPanel implements ActionListener {

  private TestPlan testPlan;
  private DelayTask task;
  private JComboBox timeUnitComboBox;

  public DelayTaskConfigPanel (TestPlan testPlan, DelayTask task) {

    super();

    GroupLayout groupLayout;
    NumericalAttributeEditingPanel staticEditingPanel;
    NumericalAttributeEditingPanel stochasticEditingPanel;
    JLabel timeUnitLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    timeUnitLabel = new JLabel("Time Unit:");

    timeUnitComboBox = new JComboBox(new EnumComboBoxModel<TimeUnit>(TimeUnit.class, task.getDelayTimeUnit()));
    timeUnitComboBox.setEditable(false);
    timeUnitComboBox.setRenderer(new EnumListCellRenderer());
    timeUnitComboBox.setBackground(ColorUtilities.TEXT_COLOR);
    timeUnitComboBox.setFocusable(false);
    timeUnitComboBox.addActionListener(this);

    staticEditingPanel = new NumericalAttributeEditingPanel(testPlan, task.getStaticTimeAttribute());
    staticEditingPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Static Delay Time"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    stochasticEditingPanel = new NumericalAttributeEditingPanel(testPlan, task.getStochasticTimeAttribute());
    stochasticEditingPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("plus Stochastic Delay Time"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(timeUnitLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(timeUnitComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addComponent(staticEditingPanel).addComponent(stochasticEditingPanel));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(timeUnitLabel).addComponent(timeUnitComboBox, 20, 20, 20))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addComponent(staticEditingPanel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(stochasticEditingPanel));
  }

  @Override
  public void actionPerformed (ActionEvent actionEvent) {

    task.setDelayTimeUnit((TimeUnit)timeUnitComboBox.getSelectedItem());
    testPlan.setChanged(true);
  }
}
