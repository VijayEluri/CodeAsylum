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

import java.io.FileWriter;
import com.thoughtworks.xstream.XStream;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.menu.MenuDelegate;
import org.smallmind.swing.menu.MenuHandler;

public class SaveDelegate implements MenuDelegate {

  @Override
  public void execute (MenuHandler menuHandler) {

    if (((JormungandrMenuHandler)menuHandler).getJdrFile() == null) {
      menuHandler.getDelegate("File/Save As...").execute(menuHandler);
    }
    else {
      if (((JormungandrMenuHandler)menuHandler).getJdrFile() != null) {
        XStream xstream = new XStream();
        FileWriter jdrWriter;

        try {
          jdrWriter = new FileWriter(((JormungandrMenuHandler)menuHandler).getJdrFile());
          xstream.toXML(((JormungandrMenuHandler)menuHandler).getTestExecutor().getTestPlan(), jdrWriter);
          jdrWriter.close();
          ((JormungandrMenuHandler)menuHandler).getTestExecutor().getTestPlan().setChanged(false);
        }
        catch (Exception exception) {
          JavaErrorDialog.showJavaErrorDialog(menuHandler.getParentFrame(), this, exception);
        }
      }
    }
  }
}