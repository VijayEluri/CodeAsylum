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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.FileTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.file.FileChooserDialog;
import org.smallmind.swing.file.FileChooserState;
import org.smallmind.swing.text.FormulaTextField;

public class FileTaskConfigPanel extends JPanel implements ActionListener, ItemListener, DocumentListener {

  private static final ImageIcon BROWSE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/folder_view_16.png"));
  private static final ImageIcon HOMOGENIZED_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/homogenized_16.png"));
  private static final ImageIcon DIFFERENTIATED_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/stress/api/differentiated_16.png"));

  private JFrame parentFrame;
  private TestPlan testPlan;
  private FileTask task;
  private FormulaTextField fileTextField;
  private JTextField keyTextField;
  private JToggleButton homogenizedButton;

  public FileTaskConfigPanel (JFrame parentFrame, TestPlan testPlan, FileTask task) {

    super();

    GroupLayout groupLayout;
    JButton browseButton;
    JLabel fileLabel;
    JLabel keyLabel;

    this.parentFrame = parentFrame;
    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    fileLabel = new JLabel("File:");
    keyLabel = new JLabel("Key:");

    browseButton = new JButton("Browse...", BROWSE_ICON);
    browseButton.setMargin(new Insets(2, 2, 2, 2));
    browseButton.setFocusable(false);
    browseButton.setToolTipText("browse for a file");
    browseButton.addActionListener(this);

    homogenizedButton = new JToggleButton(task.isHomogenized() ? "Sample per File" : "Sample per Line", DIFFERENTIATED_ICON, task.isHomogenized());
    homogenizedButton.setSelectedIcon(HOMOGENIZED_ICON);
    homogenizedButton.setMargin(new Insets(2, 2, 2, 2));
    homogenizedButton.setFocusable(false);
    homogenizedButton.setToolTipText(task.isHomogenized() ? "a sample per file" : "a sample per line");
    homogenizedButton.addItemListener(this);

    fileTextField = new FormulaTextField(task.getFileAttribute().getScript(), task.getFileAttribute().isFormula());
    fileTextField.addItemListener(this);
    fileTextField.addDocumentListener(this);

    keyTextField = new JTextField((task.getKey() == null) ? "" : task.getKey());
    keyTextField.getDocument().addDocumentListener(this);

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup().addComponent(fileLabel).addComponent(keyLabel))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup()
        .addGroup(groupLayout.createSequentialGroup().addComponent(fileTextField).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(homogenizedButton).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(browseButton))
        .addComponent(keyTextField)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fileLabel).addComponent(fileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(homogenizedButton, 20, 20, 20).addComponent(browseButton, 20, 20, 20))
      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(keyLabel).addComponent(keyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
  }

  @Override
  public void actionPerformed (ActionEvent actionEvent) {

    FileChooserDialog fileChooser = new FileChooserDialog(parentFrame, FileChooserState.OPEN);

    fileChooser.setVisible(true);
    if (fileChooser.getChosenFile() != null) {
      fileTextField.setText(fileChooser.getChosenFile().getAbsolutePath());
    }
  }

  @Override
  public synchronized void itemStateChanged (ItemEvent itemEvent) {

    if (itemEvent.getSource() == homogenizedButton) {
      task.setHomogenized(itemEvent.getStateChange() == ItemEvent.SELECTED);
      homogenizedButton.setText((itemEvent.getStateChange() == ItemEvent.SELECTED) ? "Sample per File" : "Sample per Line");
      homogenizedButton.setToolTipText((itemEvent.getStateChange() == ItemEvent.SELECTED) ? "a sample per file" : "a sample per line");
    }
    else {
      task.getFileAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }

    testPlan.setChanged(true);
  }

  private void updateFile () {

    task.getFileAttribute().setScript(fileTextField.getText().trim());
    testPlan.setChanged(true);
  }

  private void updateKey () {

    task.setKey(keyTextField.getText().trim());
    testPlan.setChanged(true);
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    if (fileTextField.containsDocument(documentEvent.getDocument())) {
      updateFile();
    }
    else {
      updateKey();
    }
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    if (fileTextField.containsDocument(documentEvent.getDocument())) {
      updateFile();
    }
    else {
      updateKey();
    }
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    if (fileTextField.containsDocument(documentEvent.getDocument())) {
      updateFile();
    }
    else {
      updateKey();
    }
  }
}
