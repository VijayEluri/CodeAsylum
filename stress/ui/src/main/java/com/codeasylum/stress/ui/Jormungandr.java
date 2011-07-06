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
package com.codeasylum.stress.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.xml.parsers.ParserConfigurationException;
import com.codeasylum.stress.api.ExtendedTaskLoader;
import com.codeasylum.stress.api.RootTask;
import com.codeasylum.stress.api.TestExecutor;
import com.codeasylum.stress.api.TestExecutorEvent;
import com.codeasylum.stress.api.TestExecutorListener;
import com.codeasylum.stress.api.TestPlan;
import com.codeasylum.stress.ui.menu.JormungandrMenuHandler;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.dialog.OptionType;
import org.smallmind.swing.dialog.YesNoCancelDialog;
import org.smallmind.swing.dragndrop.GhostPanel;
import org.smallmind.swing.menu.MenuDelegateFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

public class Jormungandr extends JFrame implements WindowListener, TestExecutorListener {

  private MenuDelegateFactory menuDelegateFactory;
  private TaskPalette palette;
  private JormungandrMenuHandler menuHandler;
  private PalettePanel palettePanel;
  private TestPanel testPanel;
  private TestExecutor testExecutor;

  public Jormungandr () {

    super("Jormungandr");
  }

  private synchronized Jormungandr init ()
    throws RemoteException, IOException, SAXException, ParserConfigurationException {

    TestPlan testPlan;
    GhostPanel ghostPanel;
    GroupLayout groupLayout;
    ViewPanel viewPanel;
    JTree testTree;
    JSplitPane workSplitPane;
    JScrollPane paletteScrollPane;
    JScrollPane testScrollPane;

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    menuHandler = new JormungandrMenuHandler(this, menuDelegateFactory);

    testExecutor = new TestExecutor(testPlan = new TestPlan());
    testExecutor.addTestExecutorListener(this);
    testPlan.getRootTask().setName(palette.getAvatar(RootTask.class).getName());

    setGlassPane(ghostPanel = new GhostPanel());
    setLayout(groupLayout = new GroupLayout(getContentPane()));

    testTree = new JTree();
    palettePanel = new PalettePanel(this, ghostPanel, palette);
    testPanel = new TestPanel(this, ghostPanel, palette, viewPanel = new ViewPanel(testTree), testTree);

    paletteScrollPane = new JScrollPane(palettePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    testScrollPane = new JScrollPane(testPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    workSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, testScrollPane, viewPanel);
    workSplitPane.setDoubleBuffered(true);
    workSplitPane.setContinuousLayout(true);
    workSplitPane.setResizeWeight(0.3);

    groupLayout.setAutoCreateContainerGaps(true);

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addComponent(paletteScrollPane, (int)palettePanel.getMinimumSize().getWidth() + 3, (int)palettePanel.getPreferredSize().getWidth() + 3, (int)palettePanel.getMaximumSize().getWidth() + 3).addComponent(workSplitPane));
    groupLayout.setVerticalGroup(groupLayout.createParallelGroup().addComponent(paletteScrollPane, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(workSplitPane, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

    setSize(new Dimension(Math.max(800, ((int)getLayout().preferredLayoutSize(this).getWidth()) + 120), Math.min(600, ((int)getLayout().preferredLayoutSize(this).getHeight()) + 38)));
    setLocationByPlatform(true);

    addWindowListener(this);

    return this;
  }

  public synchronized void setMenuDelegateFactory (MenuDelegateFactory menuDelegateFactory) {

    this.menuDelegateFactory = menuDelegateFactory;
  }

  public synchronized TaskPalette getPalette () {

    return palette;
  }

  public synchronized void setPalette (TaskPalette palette) {

    this.palette = palette;
  }

  public JormungandrMenuHandler getMenuHandler () {

    return menuHandler;
  }

  public synchronized TestExecutor getTestExecutor () {

    return testExecutor;
  }

  public synchronized void setTestPlan (TestPlan testPlan)
    throws RemoteException {

    testExecutor = new TestExecutor(testPlan);
    testExecutor.addTestExecutorListener(this);

    testPanel.setTestPlan(testPlan);
  }

  @Override
  public void executionStarted (TestExecutorEvent event) {

    menuHandler.setEnabled("File/Open...", false);
    menuHandler.setEnabled("Execute/Run...", false);
    menuHandler.setEnabled("Execute/Cancel", true);
  }

  @Override
  public void executionStopped (TestExecutorEvent event) {

    menuHandler.setEnabled("Execute/Cancel", false);
    menuHandler.setEnabled("Execute/Run...", true);
    menuHandler.setEnabled("File/Open...", true);
  }

  public synchronized void solidify (Workspace workspace) {

    switch (workspace) {
      case PALETTE:
        testPanel.blur();
        break;
      case TEST:
        palettePanel.blur();
        break;
      case VIEW:
        palettePanel.blur();
        testPanel.blur();
        break;
      default:
        throw new UnknownSwitchCaseException(workspace.name());
    }
  }

  @Override
  public synchronized void windowOpened (WindowEvent windowEvent) {

  }

  @Override
  public synchronized void windowClosing (WindowEvent windowEvent) {

    if (testExecutor.getTestPlan().isChanged()) {
      switch (YesNoCancelDialog.showYesNoCancelDialog(this, OptionType.WARNING, "Save your work before closing?")) {
        case YES:
          menuHandler.getDelegate("File/Save...").execute(menuHandler);
          if (!testExecutor.getTestPlan().isChanged()) {
            setVisible(false);
            dispose();
          }
          break;
        case NO:
          setVisible(false);
          dispose();
          break;
      }
    }
    else {
      setVisible(false);
      dispose();
    }
  }

  @Override
  public synchronized void windowClosed (WindowEvent windowEvent) {

    System.exit(0);
  }

  @Override
  public synchronized void windowIconified (WindowEvent windowEvent) {

  }

  @Override
  public synchronized void windowDeiconified (WindowEvent windowEvent) {

  }

  @Override
  public synchronized void windowActivated (WindowEvent windowEvent) {

  }

  @Override
  public synchronized void windowDeactivated (WindowEvent windowEvent) {

  }

  public static void main (String... args) {

    boolean init = false;

    try {
      new ExtendedTaskLoader();
      init = true;
    }
    catch (Exception exception) {
      JavaErrorDialog.showJavaErrorDialog(null, null, exception);
    }

    if (init) {

      final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/codeasylum/stress/ui/jormungandr.xml");

      Jormungandr jormungandr = applicationContext.getBean("jormungandr", Jormungandr.class);

      try {
        jormungandr.addWindowListener(new WindowAdapter() {

          @Override
          public void windowClosed (WindowEvent windowEvent) {

            applicationContext.close();

          }
        });

        jormungandr.init().setVisible(true);
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog(jormungandr, jormungandr, exception);
        jormungandr.dispose();
      }
    }
  }
}
