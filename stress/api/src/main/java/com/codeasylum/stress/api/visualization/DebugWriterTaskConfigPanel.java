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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.DebugWriterTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.text.FormulaTextField;

public class DebugWriterTaskConfigPanel extends JPanel implements ItemListener, DocumentListener {

  private TestPlan testPlan;
  private DebugWriterTask task;
  private FormulaTextField outputTextField;

  public DebugWriterTaskConfigPanel (TestPlan testPlan, DebugWriterTask task) {

    GroupLayout groupLayout;
    JLabel outputLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    outputLabel = new JLabel("Output:");

    outputTextField = new FormulaTextField(task.getDebugAttribute().getScript(), 15, task.getDebugAttribute().isFormula());
    outputTextField.addItemListener(this);
    outputTextField.addDocumentListener(this);

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addComponent(outputLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(outputTextField));
    groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(outputLabel).addComponent(outputTextField));
  }

  @Override
  public void itemStateChanged (ItemEvent itemEvent) {

    task.getDebugAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    testPlan.setChanged(true);
  }

  private void updateTaskAttribute () {

    task.getDebugAttribute().setScript(outputTextField.getText().trim());
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
