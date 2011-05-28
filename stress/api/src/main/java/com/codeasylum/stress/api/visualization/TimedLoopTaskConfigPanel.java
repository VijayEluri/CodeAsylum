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
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.TestPlan;
import com.codeasylum.stress.api.TimedLoopTask;
import org.smallmind.swing.ColorUtilities;

public class TimedLoopTaskConfigPanel extends JPanel implements ActionListener, DocumentListener {

  private TestPlan testPlan;
  private TimedLoopTask task;
  private JTextField keyTextField;
  private JComboBox timeUnitComboBox;

  public TimedLoopTaskConfigPanel (TestPlan testPlan, TimedLoopTask task) {

    super();

    GroupLayout groupLayout;
    NumericalAttributeEditingPanel numericalEditingPanel;
    JLabel timeUnitLabel;
    JLabel keyLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    timeUnitLabel = new JLabel("Time Unit:");
    keyLabel = new JLabel("Key:");

    keyTextField = new JTextField((task.getKey() == null) ? "" : task.getKey());
    keyTextField.getDocument().addDocumentListener(this);

    timeUnitComboBox = new JComboBox(new EnumComboBoxModel<TimeUnit>(TimeUnit.class, task.getLoopTimeUnit()));
    timeUnitComboBox.setEditable(false);
    timeUnitComboBox.setRenderer(new EnumListCellRenderer());
    timeUnitComboBox.setBackground(ColorUtilities.TEXT_COLOR);
    timeUnitComboBox.setFocusable(false);
    timeUnitComboBox.addActionListener(this);

    numericalEditingPanel = new NumericalAttributeEditingPanel(testPlan, task.getLoopTimeAttribute());
    numericalEditingPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Loop Time"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup().addComponent(numericalEditingPanel)
      .addGroup(groupLayout.createSequentialGroup().addComponent(timeUnitLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(timeUnitComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(keyLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(keyTextField)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup().addComponent(numericalEditingPanel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(timeUnitLabel).addComponent(timeUnitComboBox, 20, 20, 20))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(keyLabel).addComponent(keyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
  }

  @Override
  public void actionPerformed (ActionEvent actionEvent) {

    task.setLoopTimeUnit((TimeUnit)timeUnitComboBox.getSelectedItem());
    testPlan.setChanged(true);
  }

  private void updateKey () {

    task.setKey(keyTextField.getText().trim());
    testPlan.setChanged(true);
  }

  @Override
  public void insertUpdate (DocumentEvent documentEvent) {

    updateKey();
  }

  @Override
  public void removeUpdate (DocumentEvent documentEvent) {

    updateKey();
  }

  @Override
  public void changedUpdate (DocumentEvent documentEvent) {

    updateKey();
  }
}
