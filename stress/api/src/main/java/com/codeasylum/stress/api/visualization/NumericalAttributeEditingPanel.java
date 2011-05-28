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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.Attribute;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.swing.spinner.DefaultSpinnerRenderer;
import org.smallmind.swing.spinner.IntegerSpinnerEditor;
import org.smallmind.swing.spinner.IntegerSpinnerModel;
import org.smallmind.swing.spinner.Spinner;
import org.smallmind.swing.text.FormulaTextField;

public class NumericalAttributeEditingPanel extends JPanel implements ActionListener, ItemListener, ChangeListener, DocumentListener {

  private static enum InputStyle {NUMBER, FORMULA}

  private TestPlan testPlan;
  private Attribute attribute;
  private Spinner numberSpinner;
  private IntegerSpinnerModel numberSpinnerModel;
  private FormulaTextField formulaTextField;
  private JRadioButton numberButton;
  private JRadioButton formulaButton;

  public NumericalAttributeEditingPanel (TestPlan testPlan, Attribute attribute) {

    super();

    GroupLayout groupLayout;
    ButtonGroup radioGroup;
    InputStyle inputStyle;
    int textFieldHeight;

    this.testPlan = testPlan;
    this.attribute = attribute;

    setLayout(groupLayout = new GroupLayout(this));

    numberButton = new JRadioButton("Number:");
    numberButton.setFocusable(false);
    numberButton.addActionListener(this);

    formulaButton = new JRadioButton("Formula:");
    formulaButton.setFocusable(false);
    formulaButton.addActionListener(this);

    radioGroup = new ButtonGroup();
    radioGroup.add(numberButton);
    radioGroup.add(formulaButton);

    try {
      Integer.parseInt(attribute.getScript());
      numberButton.setSelected(true);
      inputStyle = InputStyle.NUMBER;
    }
    catch (NumberFormatException n) {
      formulaButton.setSelected(true);
      inputStyle = InputStyle.FORMULA;
    }

    formulaTextField = new FormulaTextField(inputStyle.equals(InputStyle.FORMULA) ? attribute.getScript() : "", attribute.isFormula());
    formulaTextField.addItemListener(this);
    formulaTextField.addDocumentListener(this);

    numberSpinner = new Spinner(numberSpinnerModel = new IntegerSpinnerModel(inputStyle.equals(InputStyle.NUMBER) ? Integer.parseInt(attribute.getScript()) : 0, 1, 0, Integer.MAX_VALUE), 100);
    numberSpinner.setSpinnerRenderer(new DefaultSpinnerRenderer(SwingConstants.RIGHT));
    numberSpinner.setSpinnerEditor(new IntegerSpinnerEditor(numberSpinnerModel));
    numberSpinnerModel.addChangeListener(this);

    useStyle(inputStyle, false);

    textFieldHeight = (int)formulaTextField.getPreferredSize().getHeight();

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup().addComponent(numberButton).addComponent(formulaButton))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup().addComponent(numberSpinner, 85, 85, 85).addComponent(formulaTextField)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(numberButton).addComponent(numberSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(formulaButton, textFieldHeight, textFieldHeight, textFieldHeight).addComponent(formulaTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
  }

  private void useStyle (InputStyle inputStyle, boolean updateTask) {

    switch (inputStyle) {
      case NUMBER:
        formulaTextField.setEnabled(false);
        numberSpinner.setEnabled(true);

        if (updateTask) {
          attribute.setScript(String.valueOf(numberSpinnerModel.getValue()));
          attribute.setFormula(false);
          testPlan.setChanged(true);
        }
        break;
      case FORMULA:
        numberSpinner.setEnabled(false);
        formulaTextField.setEnabled(true);

        if (updateTask) {
          attribute.setScript(formulaTextField.getText().trim());
          attribute.setFormula(formulaTextField.isFormula());
          testPlan.setChanged(true);
        }
        break;
      default:
        throw new UnknownSwitchCaseException(inputStyle.name());
    }
  }

  @Override
  public void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent.getSource() == numberButton) {
      useStyle(InputStyle.NUMBER, true);
    }
    else if (actionEvent.getSource() == formulaButton) {
      useStyle(InputStyle.FORMULA, true);
    }
  }

  @Override
  public void itemStateChanged (ItemEvent itemEvent) {

    attribute.setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    testPlan.setChanged(true);
  }

  @Override
  public synchronized void stateChanged (ChangeEvent changeEvent) {

    attribute.setScript(String.valueOf(numberSpinnerModel.getValue()));
    testPlan.setChanged(true);
  }

  private void updateFormula () {

    attribute.setScript(formulaTextField.getText().trim());
    testPlan.setChanged(true);
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    updateFormula();
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    updateFormula();
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    updateFormula();
  }
}
