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
package com.codeasylum.stress.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.rmi.RemoteException;
import javax.swing.Action;
import javax.swing.JFrame;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestPlan;
import com.codeasylum.stress.ui.Jormungandr;
import com.codeasylum.stress.ui.TaskPalette;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.menu.MenuActionProvider;
import org.smallmind.swing.menu.MenuModel;
import org.smallmind.swing.menu.MenuXmlParser;
import org.xml.sax.InputSource;

public class MenuHandler implements ActionListener, MenuActionProvider {

  private Jormungandr jormungandr;
  private MenuModel menuModel;
  private MenuDelegateFactory menuDelegateFactory;
  private File jdrFile;

  public MenuHandler (Jormungandr jormungandr, MenuDelegateFactory menuDelegateFactory) {

    this.jormungandr = jormungandr;
    this.menuDelegateFactory = menuDelegateFactory;

    try {
      menuModel = MenuXmlParser.parse(new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/codeasylum/stress/ui/menu.xml")), this);
      jormungandr.setJMenuBar(menuModel.getMenuBar(0));
    }
    catch (Exception exception) {
      JavaErrorDialog.showJavaErrorDialog(jormungandr, this, exception);
    }
  }

  public JFrame getParentFrame () {

    return jormungandr;
  }

  public TestExecutor getTestExecutor () {

    return jormungandr.getTestExecutor();
  }

  public void setTestPlan (TestPlan testPlan)
    throws RemoteException {

    jormungandr.setTestPlan(testPlan);
  }

  public TaskPalette getPalette () {

    return jormungandr.getPalette();
  }

  public File getJdrFile () {

    return jdrFile;
  }

  public void setJdrFile (File jdrFile) {

    this.jdrFile = jdrFile;
  }

  public MenuDelegate getDelegate (String menuPath) {

    return menuDelegateFactory.getDelegate(menuPath);
  }

  public void setEnabled (String menuPath, boolean enabled) {

    menuModel.getMenuItem(menuPath).setEnabled(enabled);
  }

  @Override
  public void actionPerformed (ActionEvent actionEvent) {

    getDelegate(actionEvent.getActionCommand()).execute(this);
  }

  @Override
  public ActionListener getDefaultActionListener () {

    return this;
  }

  @Override
  public Action getAction (String className) {

    throw new UnsupportedOperationException();
  }
}
