/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
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
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.codeasylum.stress.api.TestControl;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestExecutorEvent;
import com.codeasylum.stress.api.TestExecutorListener;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.spinner.DefaultSpinnerRenderer;
import org.smallmind.swing.spinner.IntegerSpinnerEditor;
import org.smallmind.swing.spinner.IntegerSpinnerModel;
import org.smallmind.swing.spinner.Spinner;

public class RootTaskDebugPanel extends JPanel implements TestExecutorListener, ChangeListener, ActionListener {

  private static final ImageIcon RUN_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/run_16.png"));
  private static final ImageIcon CANCEL_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/cancel_16.png"));
  private static final ImageIcon CLEAR_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/clear_16.png"));
  private static final int SCROLL_BAR_WIDTH = (Integer)UIManager.getDefaults().get("ScrollBar.width");
  private static final JLabel SERIAL_LABEL;
  private static final JLabel HOST_LABEL;
  private static final JLabel TASK_LABEL;
  private static final JLabel MESSAGE_LABEL;

  private JFrame parentFrame;
  private TestExecutor testExecutor;
  private JList outputList;
  private DebugListModel debugListModel;
  private Spinner outputSpinner;
  private IntegerSpinnerModel outputSpinnerModel;
  private JButton runButton;
  private JButton cancelButton;
  private JButton clearButton;

  static {

    SERIAL_LABEL = new JLabel("#", JLabel.RIGHT);
    SERIAL_LABEL.setOpaque(true);
    SERIAL_LABEL.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    SERIAL_LABEL.setForeground(ColorUtilities.TEXT_COLOR);
    SERIAL_LABEL.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 2, 2, 5, ColorUtilities.INVERSE_TEXT_COLOR)));

    HOST_LABEL = new JLabel("HOST", JLabel.LEFT);
    HOST_LABEL.setOpaque(true);
    HOST_LABEL.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    HOST_LABEL.setForeground(ColorUtilities.TEXT_COLOR);
    HOST_LABEL.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 5, 2, 0, ColorUtilities.INVERSE_TEXT_COLOR)));

    TASK_LABEL = new JLabel("TASK", JLabel.LEFT);
    TASK_LABEL.setOpaque(true);
    TASK_LABEL.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    TASK_LABEL.setForeground(ColorUtilities.TEXT_COLOR);
    TASK_LABEL.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 5, 2, 0, ColorUtilities.INVERSE_TEXT_COLOR)));

    MESSAGE_LABEL = new JLabel("MESSAGE", JLabel.LEFT);
    MESSAGE_LABEL.setOpaque(true);
    MESSAGE_LABEL.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    MESSAGE_LABEL.setForeground(ColorUtilities.TEXT_COLOR);
    MESSAGE_LABEL.setBorder(BorderFactory.createMatteBorder(2, 5, 2, 5 + SCROLL_BAR_WIDTH, ColorUtilities.INVERSE_TEXT_COLOR));
  }

  public RootTaskDebugPanel (JFrame parentFrame, TestExecutor testExecutor) {

    GroupLayout groupLayout;
    JScrollPane outputScrollPane;
    JLabel bufferLabel;

    this.parentFrame = parentFrame;
    this.testExecutor = testExecutor;

    setLayout(groupLayout = new GroupLayout(this));

    bufferLabel = new JLabel("Buffer:");

    runButton = new JButton("Run", RUN_ICON);
    runButton.setMargin(new Insets(2, 2, 2, 2));
    runButton.setFocusable(false);
    runButton.setToolTipText("start test run");
    runButton.addActionListener(this);

    cancelButton = new JButton("Cancel", CANCEL_ICON);
    cancelButton.setMargin(new Insets(2, 2, 2, 2));
    cancelButton.setFocusable(false);
    cancelButton.setToolTipText("cancel test run");
    cancelButton.addActionListener(this);

    clearButton = new JButton("Clear", CLEAR_ICON);
    clearButton.setMargin(new Insets(2, 2, 2, 2));
    clearButton.setFocusable(false);
    clearButton.setToolTipText("clear test execution log");
    clearButton.addActionListener(this);

    outputSpinner = new Spinner(outputSpinnerModel = new IntegerSpinnerModel(testExecutor.getTestPlan().getDebugBufferSize(), 10, 10, (int)Short.MAX_VALUE), 100);
    outputSpinner.setSpinnerRenderer(new DefaultSpinnerRenderer(SwingConstants.RIGHT));
    outputSpinner.setSpinnerEditor(new IntegerSpinnerEditor(outputSpinnerModel));
    outputSpinnerModel.addChangeListener(this);

    outputList = new JList(debugListModel = testExecutor.getDebugListModel());
    outputList.setCellRenderer(new DebugListCellRenderer());
    outputList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    outputScrollPane = new JScrollPane(outputList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(bufferLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(outputSpinner, 100, 100, 100).addGap(0, 0, Short.MAX_VALUE).addComponent(runButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(clearButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(SERIAL_LABEL, 50, 50, 50).addComponent(HOST_LABEL, 100, 100, 100).addComponent(TASK_LABEL, 150, 150, 150).addComponent(MESSAGE_LABEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
      .addComponent(outputScrollPane));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(bufferLabel).addComponent(outputSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(runButton, 20, 20, 20).addComponent(cancelButton, 20, 20, 20).addComponent(clearButton, 20, 20, 20))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(SERIAL_LABEL).addComponent(HOST_LABEL).addComponent(TASK_LABEL).addComponent(MESSAGE_LABEL))
      .addComponent(outputScrollPane));

    synchronized (this) {
      testExecutor.addTestExecutorListener(this);
      runButton.setEnabled(!testExecutor.isExecuting());
      cancelButton.setEnabled(testExecutor.isExecuting());
    }
  }

  @Override
  public synchronized void executionStarted (TestExecutorEvent event) {

    runButton.setEnabled(false);
    cancelButton.setEnabled(true);
    outputSpinner.setEnabled(false);
  }

  @Override
  public synchronized void executionStopped (TestExecutorEvent event) {

    runButton.setEnabled(true);
    cancelButton.setEnabled(false);
    outputSpinner.setEnabled(true);
  }

  @Override
  public void stateChanged (ChangeEvent changeEvent) {

    testExecutor.getTestPlan().setDebugBufferSize((Integer)outputSpinnerModel.getValue());
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent.getSource() == runButton) {
      TestControl.execute(this, parentFrame, testExecutor);
    }
    else if (actionEvent.getSource() == cancelButton) {
      TestControl.cancel(this, parentFrame, testExecutor);
    }
    else if (actionEvent.getSource() == clearButton) {
      outputList.clearSelection();
      debugListModel.clear();
    }
  }
}