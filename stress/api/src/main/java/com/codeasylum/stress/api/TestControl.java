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
package com.codeasylum.stress.api;

import javax.swing.JFrame;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.dialog.StopDialog;

public class TestControl {

  public static void cancel (Object source, JFrame parentFrame, TestExecutor testExecutor) {

    Exception[] exceptions;

    if ((exceptions = testExecutor.cancel()).length > 0) {
      for (Exception exception : exceptions) {
        if (exception instanceof TaskExecutionException) {
          StopDialog.showStopDialog(parentFrame, exception.getMessage());
        }
        else {
          JavaErrorDialog.showJavaErrorDialog(parentFrame, source, exception);
        }
      }
    }
  }

  public static void execute (Object source, JFrame parentFrame, TestExecutor testExecutor) {

    new Thread(new ExecuteWorker(source, parentFrame, testExecutor)).start();
  }

  private static class ExecuteWorker implements Runnable {

    private Object source;
    private JFrame parentFrame;
    private TestExecutor testExecutor;

    private ExecuteWorker (Object source, JFrame parentFrame, TestExecutor testExecutor) {

      this.source = source;
      this.parentFrame = parentFrame;
      this.testExecutor = testExecutor;
    }

    @Override
    public void run () {

      Exception[] exceptions;

      if ((exceptions = testExecutor.execute()).length > 0) {
        for (Exception exception : exceptions) {
          if (exception instanceof TaskExecutionException) {
            StopDialog.showStopDialog(parentFrame, exception.getMessage());
          }
          else {
            JavaErrorDialog.showJavaErrorDialog(parentFrame, source, exception);
          }
        }
      }
    }
  }
}
