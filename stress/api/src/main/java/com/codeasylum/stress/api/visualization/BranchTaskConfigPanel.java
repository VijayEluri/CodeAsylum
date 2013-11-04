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
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with the CodeAsylum Code Project. If not, see
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.BranchTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.text.FormulaTextField;

public class BranchTaskConfigPanel extends JPanel implements ItemListener, DocumentListener {

  private TestPlan testPlan;
  private BranchTask task;
  private FormulaTextField conditionTextField;

  public BranchTaskConfigPanel (TestPlan testPlan, BranchTask task) {

    GroupLayout groupLayout;
    JLabel conditionLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    conditionLabel = new JLabel("Condition", JLabel.LEFT);
    conditionLabel.setOpaque(true);
    conditionLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    conditionLabel.setForeground(ColorUtilities.TEXT_COLOR);
    conditionLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

    conditionTextField = new FormulaTextField(task.getConditionAttribute().getScript(), 15, task.getConditionAttribute().isFormula());
    conditionTextField.addItemListener(this);
    conditionTextField.addDocumentListener(this);

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(conditionLabel, 200, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(conditionTextField)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup().addComponent(conditionLabel))
      .addGroup(groupLayout.createParallelGroup().addComponent(conditionTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
  }

  @Override
  public void itemStateChanged (ItemEvent itemEvent) {

    task.getConditionAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    testPlan.setChanged(true);
  }

  private void updateTaskAttribute () {

    task.getConditionAttribute().setScript(conditionTextField.getText().trim());
    testPlan.setChanged(true);
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute();
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute();
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute();
  }
}
