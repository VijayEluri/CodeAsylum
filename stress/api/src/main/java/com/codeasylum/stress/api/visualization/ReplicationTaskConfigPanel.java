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

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.ReplicationTask;
import com.codeasylum.stress.api.TestPlan;

public class ReplicationTaskConfigPanel extends JPanel implements DocumentListener {

  private TestPlan testPlan;
  private ReplicationTask task;
  private JTextField keyTextField;

  public ReplicationTaskConfigPanel (TestPlan testPlan, ReplicationTask task) {

    super();

    GroupLayout groupLayout;
    NumericalAttributeEditingPanel numericalEditingPanel;
    JLabel keyLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    keyLabel = new JLabel("Key:");

    keyTextField = new JTextField((task.getKey() == null) ? "" : task.getKey());
    keyTextField.getDocument().addDocumentListener(this);

    numericalEditingPanel = new NumericalAttributeEditingPanel(testPlan, task.getSizeAttribute());
    numericalEditingPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Iterations"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addComponent(numericalEditingPanel)
      .addGroup(groupLayout.createSequentialGroup().addComponent(keyLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(keyTextField)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addComponent(numericalEditingPanel)
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(keyLabel).addComponent(keyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
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
