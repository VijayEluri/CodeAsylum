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
package com.codeasylum.liquibase;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.smallmind.nutsnbolts.io.StenographEvent;
import org.smallmind.nutsnbolts.io.StenographEventListener;
import org.smallmind.nutsnbolts.io.StenographWriter;
import org.smallmind.swing.dialog.JavaErrorDialog;

public class PreviewDialog extends JDialog implements WindowListener, StenographEventListener {

  private CountDownLatch terminationLatch;
  private JTextArea previewTextArea;
  private LinkedBlockingQueue<StenographEvent> stenographEventQueue;
  private AtomicBoolean finished = new AtomicBoolean(false);

  public static void showPreviewDialog (JFrame parent, StenographWriter previewWriter) {

    PreviewDialog previewDialog = new PreviewDialog(parent, previewWriter);

    previewDialog.setVisible(true);
  }

  public PreviewDialog (JFrame parent, StenographWriter previewWriter) {

    super(parent, "Liquibase Preview...");

    Container contentPane;
    JScrollPane previewScrollPane;

    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    previewTextArea = new JTextArea();
    previewScrollPane = new JScrollPane(previewTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    contentPane = getContentPane();
    contentPane.setLayout(new GridLayout(1, 0));
    contentPane.add(previewScrollPane);

    setSize(1024, 600);
    setLocationRelativeTo(parent);

    terminationLatch = new CountDownLatch(1);
    stenographEventQueue = new LinkedBlockingQueue<StenographEvent>();
    new Thread(new PreviewHandler()).start();

    previewWriter.addStenographListener(this);
    addWindowListener(this);
  }

  private void finish ()
    throws InterruptedException {

    finished.set(true);
    terminationLatch.await();
  }

  @Override
  public synchronized void flush (StenographEvent stenographEvent) {

    stenographEventQueue.add(stenographEvent);
  }

  @Override
  public void windowOpened (WindowEvent windowEvent) {

  }

  @Override
  public synchronized void windowClosing (WindowEvent windowEvent) {

    try {
      finish();
    }
    catch (InterruptedException interruptedException) {
      JavaErrorDialog.showJavaErrorDialog((JFrame)getParent(), this, interruptedException);
    }

    setVisible(false);
    dispose();
  }

  @Override
  public void windowClosed (WindowEvent windowEvent) {

  }

  @Override
  public void windowIconified (WindowEvent windowEvent) {

  }

  @Override
  public void windowDeiconified (WindowEvent windowEvent) {

  }

  @Override
  public void windowActivated (WindowEvent windowEvent) {

  }

  @Override
  public void windowDeactivated (WindowEvent windowEvent) {

  }

  private class PreviewHandler implements Runnable {

    @Override
    public void run () {

      try {
        while (!finished.get()) {

          StenographEvent stenographEvent;

          if ((stenographEvent = stenographEventQueue.poll(50, TimeUnit.MILLISECONDS)) != null) {
            SwingUtilities.invokeAndWait(new PreviewUpdater(stenographEvent));
          }
        }
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog((JFrame)getParent(), this, exception);
      }

      terminationLatch.countDown();
    }
  }

  private class PreviewUpdater implements Runnable {

    private StenographEvent stenographEvent;

    private PreviewUpdater (StenographEvent stenographEvent) {

      this.stenographEvent = stenographEvent;
    }

    @Override
    public void run () {

      previewTextArea.append(stenographEvent.getOutput());
    }
  }
}
