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
package com.codeasylum.stress.ui.menu;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestPlan;
import com.codeasylum.stress.ui.Jormungandr;
import com.codeasylum.stress.ui.TaskPalette;
import org.smallmind.swing.menu.MenuActionProvider;
import org.smallmind.swing.menu.MenuDelegateFactory;
import org.smallmind.swing.menu.MenuHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class JormungandrMenuHandler extends MenuHandler implements ActionListener, MenuActionProvider {

  private Jormungandr jormungandr;
  private File jdrFile;

  public JormungandrMenuHandler (Jormungandr jormungandr, MenuDelegateFactory menuDelegateFactory)
    throws IOException, SAXException, ParserConfigurationException {

    super(jormungandr, menuDelegateFactory, new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/codeasylum/stress/ui/menu.xml")));

    this.jormungandr = jormungandr;
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
}
