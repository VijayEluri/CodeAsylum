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

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import com.codeasylum.stress.api.Task;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.Separator;

public class DefaultTaskPanel extends JPanel implements DocumentListener {

  private TestPlan testPlan;
  private Task task;
  private TreePath taskPath;
  private JTree testTree;

  private JTextField taskNameTextField;

  public DefaultTaskPanel (TestPlan testPlan, Task task, TreePath taskPath, JTree testTree, JComponent viewComponent) {

    GroupLayout groupLayout;
    GroupLayout.ParallelGroup horizontalGroup;
    GroupLayout.SequentialGroup verticalGroup;
    JLabel taskNameLabel;
    Separator separator;

    this.testPlan = testPlan;
    this.task = task;
    this.taskPath = taskPath;
    this.testTree = testTree;

    taskNameLabel = new JLabel("Title:");
    separator = new Separator();

    taskNameTextField = new JTextField(task.getName());
    taskNameTextField.getDocument().addDocumentListener(this);

    setLayout(groupLayout = new GroupLayout(this));

    groupLayout.setAutoCreateContainerGaps(true);

    groupLayout.setHorizontalGroup(horizontalGroup = groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(taskNameLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(taskNameTextField))
      .addComponent(separator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

    if (viewComponent != null) {
      horizontalGroup.addComponent(viewComponent);
    }

    groupLayout.setVerticalGroup(verticalGroup = groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup().addComponent(taskNameLabel).addComponent(taskNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

    if (viewComponent != null) {
      verticalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(viewComponent, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
    }
  }

  private void updateTaskName () {

    TreePath parentPath;

    task.setName(taskNameTextField.getText().trim());
    testPlan.setChanged(true);
    ((TestTreeModel)testTree.getModel()).fireTreeNodesChanged(new TreeModelEvent(this, parentPath = taskPath.getParentPath(), (parentPath == null) ? null : new int[] {testTree.getModel().getIndexOfChild(parentPath.getLastPathComponent(), task)}, new Object[] {task}));
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    updateTaskName();
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    updateTaskName();
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    updateTaskName();
  }
}
