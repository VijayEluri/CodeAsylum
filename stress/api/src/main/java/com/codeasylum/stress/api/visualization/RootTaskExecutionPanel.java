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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.codeasylum.stress.api.TestControl;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestExecutorEvent;
import com.codeasylum.stress.api.TestExecutorListener;
import com.codeasylum.stress.api.format.FormattingException;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.file.FileChooserDialog;
import org.smallmind.swing.file.FileChooserState;
import org.smallmind.swing.spinner.DefaultSpinnerRenderer;
import org.smallmind.swing.spinner.IntegerSpinnerEditor;
import org.smallmind.swing.spinner.IntegerSpinnerModel;
import org.smallmind.swing.spinner.Spinner;

public class RootTaskExecutionPanel extends JPanel implements TestExecutorListener, ChangeListener, ActionListener, ListSelectionListener, DocumentListener {

  private static final ImageIcon RUN_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/run_16.png"));
  private static final ImageIcon CANCEL_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/cancel_16.png"));
  private static final ImageIcon CLEAR_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/clear_16.png"));
  private static final ImageIcon REQUEST_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/request_24.png"));
  private static final ImageIcon RESPONSE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/response_24.png"));
  private static final ImageIcon BROWSE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/folder_view_16.png"));
  private static final int SCROLL_BAR_WIDTH = (Integer)UIManager.getDefaults().get("ScrollBar.width");
  private static final JLabel SERIAL_LABEL;
  private static final JLabel HOST_LABEL;
  private static final JLabel TEST_LABEL;
  private static final JLabel TIME_LABEL;

  private JFrame parentFrame;
  private TestExecutor testExecutor;
  private JList summaryList;
  private Spinner summarySpinner;
  private IntegerSpinnerModel summarySpinnerModel;
  private JTextArea requestTextArea;
  private JTextArea responseTextArea;
  private JButton runButton;
  private JButton cancelButton;
  private JButton clearButton;
  private JButton browseButton;
  private JTextField fileTextField;

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

    TEST_LABEL = new JLabel("TEST", JLabel.LEFT);
    TEST_LABEL.setOpaque(true);
    TEST_LABEL.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    TEST_LABEL.setForeground(ColorUtilities.TEXT_COLOR);
    TEST_LABEL.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 5, 2, 0, ColorUtilities.INVERSE_TEXT_COLOR)));

    TIME_LABEL = new JLabel("TIME", JLabel.RIGHT);
    TIME_LABEL.setOpaque(true);
    TIME_LABEL.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    TIME_LABEL.setForeground(ColorUtilities.TEXT_COLOR);
    TIME_LABEL.setBorder(BorderFactory.createMatteBorder(2, 15, 2, 5 + SCROLL_BAR_WIDTH, ColorUtilities.INVERSE_TEXT_COLOR));
  }

  public RootTaskExecutionPanel (JFrame parentFrame, TestExecutor testExecutor) {

    super();

    GroupLayout groupLayout;
    GroupLayout fileGroupLayout;
    GroupLayout controlGroupLayout;
    JTabbedPane detailTabbedPane;
    JPanel filePanel;
    JPanel controlPanel;
    JScrollPane summaryScrollPane;
    JLabel fileLabel;
    JLabel bufferLabel;

    this.parentFrame = parentFrame;
    this.testExecutor = testExecutor;

    setLayout(groupLayout = new GroupLayout(this));

    fileLabel = new JLabel("Output File:");
    bufferLabel = new JLabel("Buffer:");

    browseButton = new JButton("Browse...", BROWSE_ICON);
    browseButton.setMargin(new Insets(2, 2, 2, 2));
    browseButton.setFocusable(false);
    browseButton.setToolTipText("browse for a file");
    browseButton.addActionListener(this);

    fileTextField = new JTextField((testExecutor.getTestPlan().getOutputPath() == null) ? "" : testExecutor.getTestPlan().getOutputPath());
    fileTextField.getDocument().addDocumentListener(this);

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

    summarySpinner = new Spinner(summarySpinnerModel = new IntegerSpinnerModel(testExecutor.getTestPlan().getExchangeBufferSize(), 100, 100, (int)Short.MAX_VALUE), 100);
    summarySpinner.setSpinnerRenderer(new DefaultSpinnerRenderer(SwingConstants.RIGHT));
    summarySpinner.setSpinnerEditor(new IntegerSpinnerEditor(summarySpinnerModel));
    summarySpinnerModel.addChangeListener(this);

    summaryList = new JList(testExecutor.getExchangeListModel());
    summaryList.setDoubleBuffered(true);
    summaryList.setCellRenderer(new ExchangeListCellRenderer());
    summaryList.getSelectionModel().addListSelectionListener(this);
    summaryList.setPrototypeCellValue(new ExchangeWrapper(Integer.MAX_VALUE, new PrototypeExchange()));
    summaryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    requestTextArea = new JTextArea();
    requestTextArea.setEditable(false);

    responseTextArea = new JTextArea();
    responseTextArea.setEditable(false);

    filePanel = new JPanel();
    filePanel.setLayout(fileGroupLayout = new GroupLayout(filePanel));

    controlPanel = new JPanel();
    controlPanel.setLayout(controlGroupLayout = new GroupLayout(controlPanel));

    detailTabbedPane = new JTabbedPane();
    detailTabbedPane.addTab("Request", REQUEST_ICON, new JScrollPane(requestTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    detailTabbedPane.addTab("Response", RESPONSE_ICON, new JScrollPane(responseTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

    summaryScrollPane = new JScrollPane(summaryList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    fileGroupLayout.setHorizontalGroup(fileGroupLayout.createSequentialGroup().addComponent(fileLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(fileTextField, 150, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(browseButton));
    fileGroupLayout.setVerticalGroup(fileGroupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(fileLabel).addComponent(fileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(browseButton, 20, 20, 20));

    controlGroupLayout.setHorizontalGroup(controlGroupLayout.createSequentialGroup().addComponent(bufferLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(summarySpinner, 100, 100, 100).addGap(0, 0, Short.MAX_VALUE).addComponent(runButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(clearButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
    controlGroupLayout.setVerticalGroup(controlGroupLayout.createParallelGroup().addComponent(bufferLabel).addComponent(summarySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(runButton, 20, 20, 20).addComponent(cancelButton, 20, 20, 20).addComponent(clearButton, 20, 20, 20));

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        .addComponent(filePanel, 425 + SCROLL_BAR_WIDTH, 425 + SCROLL_BAR_WIDTH, 425 + SCROLL_BAR_WIDTH)
        .addComponent(controlPanel, 425 + SCROLL_BAR_WIDTH, 425 + SCROLL_BAR_WIDTH, 425 + SCROLL_BAR_WIDTH)
        .addGroup(groupLayout.createSequentialGroup().addComponent(SERIAL_LABEL, 50, 50, 50).addComponent(HOST_LABEL, 100, 100, 100).addComponent(TEST_LABEL, 175, 175, 175).addComponent(TIME_LABEL, 100 + SCROLL_BAR_WIDTH, 100 + SCROLL_BAR_WIDTH, 100 + SCROLL_BAR_WIDTH))
        .addComponent(summaryScrollPane, 425 + SCROLL_BAR_WIDTH, 425 + SCROLL_BAR_WIDTH, 425 + SCROLL_BAR_WIDTH))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addComponent(detailTabbedPane));

    groupLayout.setVerticalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup()
        .addComponent(filePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(groupLayout.createParallelGroup().addComponent(SERIAL_LABEL).addComponent(HOST_LABEL).addComponent(TEST_LABEL).addComponent(TIME_LABEL))
        .addComponent(summaryScrollPane, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
      .addComponent(detailTabbedPane, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

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
    fileTextField.setEnabled(false);
    browseButton.setEnabled(false);
    summarySpinner.setEnabled(false);

    summaryList.clearSelection();
  }

  @Override
  public synchronized void executionStopped (TestExecutorEvent event) {

    runButton.setEnabled(true);
    cancelButton.setEnabled(false);
    fileTextField.setEnabled(true);
    browseButton.setEnabled(true);
    summarySpinner.setEnabled(true);
  }

  @Override
  public void stateChanged (ChangeEvent changeEvent) {

    testExecutor.getTestPlan().setExchangeBufferSize((Integer)summarySpinnerModel.getValue());
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent.getSource() == browseButton) {

      FileChooserDialog fileChooser = new FileChooserDialog(parentFrame, FileChooserState.SAVE);

      fileChooser.setVisible(true);
      if (fileChooser.getChosenFile() != null) {
        fileTextField.setText(fileChooser.getChosenFile().getAbsolutePath());
      }
    }
    else if (actionEvent.getSource() == runButton) {
      TestControl.execute(this, parentFrame, testExecutor);
    }
    else if (actionEvent.getSource() == cancelButton) {
      TestControl.cancel(this, parentFrame, testExecutor);
    }
    else if (actionEvent.getSource() == clearButton) {
      summaryList.clearSelection();
      ((ExchangeListModel)summaryList.getModel()).clear();
    }
  }

  @Override
  public synchronized void valueChanged (ListSelectionEvent listSelectionEvent) {

    if (!listSelectionEvent.getValueIsAdjusting()) {

      ExchangeWrapper exchangeWrapper;

      if ((exchangeWrapper = (ExchangeWrapper)summaryList.getSelectedValue()) == null) {
        requestTextArea.setText("");
        responseTextArea.setText("");
      }
      else {
        try {
          requestTextArea.setText(exchangeWrapper.getExchange().getFormattedRequest());
          requestTextArea.setCaretPosition(0);

          responseTextArea.setText(exchangeWrapper.getExchange().getFormattedResponse());
          responseTextArea.setCaretPosition(0);
        }
        catch (FormattingException formattingException) {
          JavaErrorDialog.showJavaErrorDialog(parentFrame, this, formattingException);
        }
      }
    }
  }

  private void updateOutputPath () {

    testExecutor.getTestPlan().setOutputPath(fileTextField.getText().trim());
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    updateOutputPath();
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    updateOutputPath();
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    updateOutputPath();
  }
}
