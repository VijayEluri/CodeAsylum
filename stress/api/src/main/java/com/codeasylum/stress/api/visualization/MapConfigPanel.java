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

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.SmallMindGrayFilter;
import org.smallmind.swing.label.PlainLabel;

public class MapConfigPanel extends JPanel implements KeyListener, ActionListener, DocumentListener {

  private static final ImageIcon ADD_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/add_16.png"));

  private JTable propertyTable;
  private MapTableModel propertyTableModel;
  private JButton addButton;
  private JTextField addKeyTextField;
  private JTextField addValueTextField;

  public MapConfigPanel (JFrame parentFrame, TestPlan testPlan, String thing, Map<String, String> map, String valueName, boolean reversed) {

    this(parentFrame, testPlan, thing, map, valueName, reversed, null);
  }

  public MapConfigPanel (JFrame parentFrame, TestPlan testPlan, String thing, Map<String, String> map, String valueName, boolean reversed, String footnote) {

    super();

    GroupLayout groupLayout;
    GroupLayout.ParallelGroup horizontalGroup;
    GroupLayout.SequentialGroup verticalGroup;
    MapTableHeaderRenderer headerRenderer;
    JScrollPane propertyScrollPane;
    JLabel keyLabel;
    JLabel valueLabel;
    int labelHeight;
    int textFieldHeight;

    setLayout(groupLayout = new GroupLayout(this));

    keyLabel = new JLabel("Key", JLabel.LEFT);
    keyLabel.setOpaque(true);
    keyLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    keyLabel.setForeground(ColorUtilities.TEXT_COLOR);
    keyLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR)));

    valueLabel = new JLabel(valueName, JLabel.LEFT);
    valueLabel.setOpaque(true);
    valueLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    valueLabel.setForeground(ColorUtilities.TEXT_COLOR);
    valueLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

    addButton = new JButton("Add " + thing, ADD_ICON);
    addButton.setMargin(new Insets(2, 2, 2, 2));
    addButton.setDisabledIcon(new ImageIcon(SmallMindGrayFilter.createDisabledImage(ADD_ICON.getImage())));
    addButton.setEnabled(false);
    addButton.setFocusable(false);
    addButton.setHorizontalAlignment(JLabel.RIGHT);
    addButton.addActionListener(this);

    addKeyTextField = new JTextField();
    addKeyTextField.getDocument().addDocumentListener(this);

    addValueTextField = new JTextField();
    addValueTextField.getDocument().addDocumentListener(this);

    headerRenderer = new MapTableHeaderRenderer();
    propertyTable = new JTable(propertyTableModel = new MapTableModel(parentFrame, testPlan, map, valueName, reversed));
    propertyTable.setDefaultRenderer(String.class, new MapTableCellRenderer());
    propertyTable.setDefaultEditor(String.class, new MapTableCellEditor(propertyTableModel));
    propertyTable.setGridColor(Color.LIGHT_GRAY);
    propertyTable.setAutoscrolls(true);
    propertyTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    propertyTable.getTableHeader().setResizingAllowed(false);
    propertyTable.getTableHeader().setReorderingAllowed(false);
    propertyTable.setRowSelectionAllowed(false);
    propertyTable.setColumnSelectionAllowed(false);
    propertyTable.setCellSelectionEnabled(true);
    propertyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    propertyTable.setRowHeight((int)addKeyTextField.getPreferredSize().getHeight());
    propertyTable.getColumnModel().getColumn(0).setHeaderRenderer(headerRenderer);
    propertyTable.getColumnModel().getColumn(1).setHeaderRenderer(headerRenderer);
    propertyTable.getColumnModel().getColumn(0).setMinWidth(200);
    propertyTable.getColumnModel().getColumn(0).setMaxWidth(200);
    propertyTable.addKeyListener(this);

    propertyScrollPane = new JScrollPane(propertyTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    labelHeight = (int)keyLabel.getPreferredSize().getHeight();
    textFieldHeight = (int)addKeyTextField.getPreferredSize().getHeight() + 2;

    groupLayout.setHorizontalGroup(horizontalGroup = groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
      .addComponent(addButton)
      .addGroup(groupLayout.createSequentialGroup().addComponent(keyLabel, 200, 200, 200).addComponent(valueLabel, 200, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(addKeyTextField, 200, 200, 200).addComponent(addValueTextField))
      .addComponent(propertyScrollPane));

    groupLayout.setVerticalGroup(verticalGroup = groupLayout.createSequentialGroup()
      .addComponent(addButton, labelHeight, labelHeight, labelHeight).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(keyLabel).addComponent(valueLabel))
      .addGroup(groupLayout.createParallelGroup().addComponent(addKeyTextField, textFieldHeight, textFieldHeight, textFieldHeight).addComponent(addValueTextField, textFieldHeight, textFieldHeight, textFieldHeight))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(propertyScrollPane, textFieldHeight * 5, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

    if (footnote != null) {

      PlainLabel footnoteLabel;

      footnoteLabel = new PlainLabel(footnote);
      footnoteLabel.setFontSize(11.0F);

      horizontalGroup.addComponent(footnoteLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
      verticalGroup.addComponent(footnoteLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
    }
  }

  @Override
  public void setEnabled (boolean enabled) {

    addButton.setEnabled(enabled);
    addKeyTextField.setEnabled(enabled);
    addValueTextField.setEnabled(enabled);
  }

  private void updateAddPropertyButton () {

    String key;

    addButton.setEnabled(((key = addKeyTextField.getText().trim()).length() > 0) && (addValueTextField.getText().trim().length() > 0) && (!propertyTableModel.containsKey(key)));
    addKeyTextField.setForeground(propertyTableModel.containsKey(key) ? Color.RED : UIManager.getDefaults().getColor("textText"));
  }

  @Override
  public void keyTyped (KeyEvent keyEvent) {

  }

  @Override
  public void keyPressed (KeyEvent keyEvent) {

  }

  @Override
  public void keyReleased (KeyEvent keyEvent) {

    if ((keyEvent.getKeyCode() == KeyEvent.VK_DELETE) && (keyEvent.getModifiers() == 0)) {
      if (propertyTable.getSelectedColumn() == 0) {
        propertyTableModel.remove(propertyTable.getSelectedRow());
      }
      else {
        propertyTable.editCellAt(propertyTable.getSelectedRow(), 1);
      }
    }
  }

  @Override
  public void actionPerformed (ActionEvent actionEvent) {

    String key;

    propertyTableModel.put(key = addKeyTextField.getText().trim(), addValueTextField.getText().trim());
    addKeyTextField.setText("");
    addValueTextField.setText("");
    propertyTable.changeSelection(propertyTableModel.rowForKey(key), 0, false, false);
    propertyTable.grabFocus();
  }

  @Override
  public void insertUpdate (DocumentEvent documentEvent) {

    updateAddPropertyButton();
  }

  @Override
  public void removeUpdate (DocumentEvent documentEvent) {

    updateAddPropertyButton();
  }

  @Override
  public void changedUpdate (DocumentEvent documentEvent) {

    updateAddPropertyButton();
  }
}
