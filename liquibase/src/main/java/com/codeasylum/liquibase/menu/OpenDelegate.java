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
package com.codeasylum.liquibase.menu;

import java.io.FileReader;
import com.codeasylum.liquibase.LiquidateConfig;
import com.thoughtworks.xstream.XStream;
import org.smallmind.nutsnbolts.io.ExtensionFileFilter;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.file.FileChooserDialog;
import org.smallmind.swing.file.FileChooserState;
import org.smallmind.swing.menu.MenuDelegate;
import org.smallmind.swing.menu.MenuHandler;

public class OpenDelegate implements MenuDelegate {

  @Override
  public void execute (MenuHandler menuHandler) {

    FileChooserDialog fileChooser = new FileChooserDialog(menuHandler.getParentFrame(), FileChooserState.OPEN, (((LiquidateMenuHandler)menuHandler).getLqdFile() == null) ? null : ((LiquidateMenuHandler)menuHandler).getLqdFile().getParentFile(), new ExtensionFileFilter("Liquidate Config", "lqd"));

    fileChooser.setVisible(true);

    if (fileChooser.getChosenFile() != null) {

      XStream xstream = new XStream();
      FileReader jdrReader;

      try {
        jdrReader = new FileReader(fileChooser.getChosenFile());
        ((LiquidateMenuHandler)menuHandler).setConfig((LiquidateConfig)xstream.fromXML(jdrReader));
        jdrReader.close();

        ((LiquidateMenuHandler)menuHandler).setLqdFile(fileChooser.getChosenFile());
        menuHandler.getParentFrame().setTitle("Liquidate - " + ((LiquidateMenuHandler)menuHandler).getLqdFile().getName());
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog(menuHandler.getParentFrame(), this, exception);
      }
    }
  }
}
