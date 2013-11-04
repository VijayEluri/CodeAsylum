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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.HttpTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.http.HttpMethod;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.text.FormulaTextField;

public class HttpTaskConfigPanel extends JPanel implements ActionListener, ItemListener, DocumentListener {

  private TestPlan testPlan;
  private HttpTask task;
  private JComboBox methodComboBox;
  private FormulaTextField serverTextField;
  private FormulaTextField portTextField;
  private FormulaTextField pathTextField;
  private FormulaTextField contentTypeTextField;
  private FormulaTextField bodyTextField;
  private JTextField responseKeyTextField;

  public HttpTaskConfigPanel (JFrame parentFrame, TestPlan testPlan, HttpTask task) {

    super();

    this.testPlan = testPlan;
    this.task = task;

    GroupLayout groupLayout;
    MapConfigPanel regexpConfigPanel;
    MapConfigPanel validationConfigPanel;
    JLabel serverLabel;
    JLabel portLabel;
    JLabel pathLabel;
    JLabel contentTypeLabel;
    JLabel methodLabel;
    JLabel bodyLabel;
    JLabel responseKeyLabel;

    setLayout(groupLayout = new GroupLayout(this));

    serverLabel = new JLabel("http://");
    portLabel = new JLabel(":");
    pathLabel = new JLabel("/");
    methodLabel = new JLabel("Http Method:");
    contentTypeLabel = new JLabel("Content Type:");
    bodyLabel = new JLabel("Body:");
    responseKeyLabel = new JLabel("Response Key:");

    serverTextField = new FormulaTextField(task.getServerAttribute().getScript(), task.getServerAttribute().isFormula());
    serverTextField.addItemListener(this);
    serverTextField.addDocumentListener(this);

    portTextField = new FormulaTextField(task.getPortAttribute().getScript(), task.getPortAttribute().isFormula());
    portTextField.addItemListener(this);
    portTextField.addDocumentListener(this);

    pathTextField = new FormulaTextField(task.getPathAttribute().getScript(), task.getPathAttribute().isFormula());
    pathTextField.addItemListener(this);
    pathTextField.addDocumentListener(this);

    contentTypeTextField = new FormulaTextField(task.getContentTypeAttribute().getScript(), task.getContentTypeAttribute().isFormula());
    contentTypeTextField.addItemListener(this);
    contentTypeTextField.addDocumentListener(this);

    bodyTextField = new FormulaTextField(task.getBodyAttribute().getScript(), task.getBodyAttribute().isFormula());
    bodyTextField.addItemListener(this);
    bodyTextField.addDocumentListener(this);

    responseKeyTextField = new JTextField((task.getRegexpMap() == null) ? "" : task.getResponseKey());
    responseKeyTextField.getDocument().addDocumentListener(this);

    methodComboBox = new JComboBox(new EnumComboBoxModel<HttpMethod>(HttpMethod.class, task.getHttpMethod()));
    methodComboBox.setEditable(false);
    methodComboBox.setRenderer(new EnumListCellRenderer(false));
    methodComboBox.setBackground(ColorUtilities.TEXT_COLOR);
    methodComboBox.setFocusable(false);
    methodComboBox.addActionListener(this);

    regexpConfigPanel = new MapConfigPanel(parentFrame, testPlan, "Regex", task.getRegexpMap(), "Regex", false, "*Regular Expression results will be available as '<key>.<group>', where '<key>.0' represents the base match");
    regexpConfigPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Response Parsing"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));

    validationConfigPanel = new MapConfigPanel(parentFrame, testPlan, "Validation", task.getValidationMap(), "Validation", false);
    validationConfigPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Response Validation"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(serverLabel).addGap(3).addComponent(serverTextField, 200, 200, 200).addGap(3).addComponent(portLabel).addGap(3).addComponent(portTextField, 125, 125, 125).addGap(3).addComponent(pathLabel).addGap(2).addComponent(pathTextField, 200, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(methodLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(methodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(contentTypeLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(contentTypeTextField, 200, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(bodyLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(bodyTextField, 200, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(responseKeyLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(responseKeyTextField))
      .addComponent(regexpConfigPanel)
      .addComponent(validationConfigPanel));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup().addComponent(serverLabel).addComponent(serverTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(portLabel).addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(pathLabel).addComponent(pathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(methodLabel).addComponent(methodComboBox, 20, 20, 20).addComponent(contentTypeLabel).addComponent(contentTypeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(bodyLabel).addComponent(bodyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(responseKeyLabel).addComponent(responseKeyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addComponent(regexpConfigPanel)
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addComponent(validationConfigPanel));
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    task.setHttpMethod((HttpMethod)methodComboBox.getSelectedItem());
    testPlan.setChanged(true);
  }

  @Override
  public void itemStateChanged (ItemEvent itemEvent) {

    if (serverTextField.containsFormulaButton((JToggleButton)itemEvent.getSource())) {
      task.getServerAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }
    else if (portTextField.containsFormulaButton((JToggleButton)itemEvent.getSource())) {
      task.getPortAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }
    else if (pathTextField.containsFormulaButton((JToggleButton)itemEvent.getSource())) {
      task.getPathAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }
    else if (contentTypeTextField.containsFormulaButton((JToggleButton)itemEvent.getSource())) {
      task.getContentTypeAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }
    else if (bodyTextField.containsFormulaButton((JToggleButton)itemEvent.getSource())) {
      task.getBodyAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }

    testPlan.setChanged(true);
  }

  private void updateTaskAttribute (DocumentEvent documentEvent) {

    if (documentEvent.getDocument() == responseKeyTextField.getDocument()) {
      task.setResponseKey(responseKeyTextField.getText().trim());
    }
    else if (serverTextField.containsDocument(documentEvent.getDocument())) {
      task.getServerAttribute().setScript(serverTextField.getText().trim());
    }
    else if (portTextField.containsDocument(documentEvent.getDocument())) {
      task.getPortAttribute().setScript(portTextField.getText().trim());
    }
    else if (pathTextField.containsDocument(documentEvent.getDocument())) {
      task.getPathAttribute().setScript(pathTextField.getText().trim());
    }
    else if (contentTypeTextField.containsDocument(documentEvent.getDocument())) {
      task.getContentTypeAttribute().setScript(contentTypeTextField.getText().trim());
    }
    else if (bodyTextField.containsDocument(documentEvent.getDocument())) {
      task.getBodyAttribute().setScript(bodyTextField.getText().trim());
    }

    testPlan.setChanged(true);
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute(documentEvent);
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute(documentEvent);
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute(documentEvent);
  }
}
