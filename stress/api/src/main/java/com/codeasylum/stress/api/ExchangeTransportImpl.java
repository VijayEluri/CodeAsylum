/*
 * Copyright (c) 2007, 2008, 2009, 2010 David Berkman
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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class ExchangeTransportImpl extends UnicastRemoteObject implements ExchangeTransport {

  private final WeakEventListenerList<ExchangeListener> exchangeListenerList;
  private final WeakEventListenerList<DebugListener> debugListenerList;
  private final LinkedBlockingQueue<Exchange<? extends Task>> exchangeQueue;
  private final LinkedBlockingQueue<Debug> debugQueue;

  public ExchangeTransportImpl ()
    throws RemoteException {

    exchangeListenerList = new WeakEventListenerList<ExchangeListener>();
    debugListenerList = new WeakEventListenerList<DebugListener>();
    exchangeQueue = new LinkedBlockingQueue<Exchange<? extends Task>>();
    debugQueue = new LinkedBlockingQueue<Debug>();

    new Thread(new ExchangePipeline()).start();
    new Thread(new DebugPipeline()).start();
  }

  public void send (Exchange<? extends Task> exchange) {

    exchangeQueue.add(exchange);
  }

  public void send (Debug debug) {

    debugQueue.add(debug);
  }

  public synchronized void addExchangeListener (ExchangeListener exchangeListener) {

    exchangeListenerList.addListener(exchangeListener);
  }

  public synchronized void removeExchangeListener (ExchangeListener exchangeListener) {

    exchangeListenerList.removeListener(exchangeListener);
  }

  public synchronized void addDebugListener (DebugListener debugListener) {

    debugListenerList.addListener(debugListener);
  }

  public synchronized void removeDebugListener (DebugListener debugListener) {

    debugListenerList.removeListener(debugListener);
  }

  private class ExchangePipeline implements Runnable {

    private CountDownLatch terminationLatch = new CountDownLatch(1);
    private AtomicBoolean finished = new AtomicBoolean(false);

    public void finish ()
      throws InterruptedException {

      finished.compareAndSet(false, true);
      terminationLatch.await();
    }

    @Override
    public void run () {

      Exchange<? extends Task> exchange;

      try {
        while (!finished.get()) {
          try {
            if ((exchange = exchangeQueue.poll(300, TimeUnit.MILLISECONDS)) != null) {

              ExchangeEvent exchangeEvent = new ExchangeEvent(this, exchange);

              for (ExchangeListener exchangeListener : exchangeListenerList) {
                exchangeListener.receive(exchangeEvent);
              }
            }
          }
          catch (InterruptedException interruptedException) {
          }
        }
      }
      finally {
        terminationLatch.countDown();
      }
    }

    @Override
    protected void finalize ()
      throws InterruptedException {

      finish();
    }
  }

  private class DebugPipeline implements Runnable {

    private CountDownLatch terminationLatch = new CountDownLatch(1);
    private AtomicBoolean finished = new AtomicBoolean(false);

    public void finish ()
      throws InterruptedException {

      finished.compareAndSet(false, true);
      terminationLatch.await();
    }

    @Override
    public void run () {

      Debug debug;

      try {
        while (!finished.get()) {
          try {
            if ((debug = debugQueue.poll(300, TimeUnit.MILLISECONDS)) != null) {

              DebugEvent debugEvent = new DebugEvent(this, debug);

              for (DebugListener debugListener : debugListenerList) {
                debugListener.receive(debugEvent);
              }
            }
          }
          catch (InterruptedException interruptedException) {
          }
        }
      }
      finally {
        terminationLatch.countDown();
      }
    }

    @Override
    protected void finalize ()
      throws InterruptedException {

      finish();
    }
  }
}
