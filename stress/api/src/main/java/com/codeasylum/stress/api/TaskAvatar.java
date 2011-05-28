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
package com.codeasylum.stress.api;

import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

public interface TaskAvatar<T extends Task> extends Serializable {

  public abstract Class<T> getTaskClass ();

  public abstract String getName ();

  public abstract TaskType getType ();

  public abstract ChildModel getChildModel ();

  public abstract Class[] mustPrecede ();

  public abstract Class[] mustNotPrecede ();

  public abstract Class[] mustProceed ();

  public abstract Class[] mustNotProceed ();

  public abstract ImageIcon getIcon24 ();

  public abstract ImageIcon getGrayIcon24 ();

  public abstract ImageIcon getIcon32 ();

  public abstract JComponent getVisualization (JFrame parentFrame, TestExecutor testExecutioner, T task)
    throws Exception;
}
