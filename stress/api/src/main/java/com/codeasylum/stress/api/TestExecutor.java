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

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.naming.NamingException;
import com.codeasylum.stress.api.visualization.DebugListModel;
import com.codeasylum.stress.api.visualization.ExchangeListModel;
import org.smallmind.nutsnbolts.csv.CSVWriter;
import org.smallmind.nutsnbolts.util.WeakEventListenerList;

public class TestExecutor implements ExchangeListener {

  private final WeakEventListenerList<TestExecutorListener> listenerList = new WeakEventListenerList<TestExecutorListener>();
  private final AtomicReference<CsvWriterStash> outputWriterStashRef = new AtomicReference<CsvWriterStash>();
  private final AtomicBoolean executing = new AtomicBoolean(false);

  private ExchangeTransport exchangeTransport;
  private ExchangeListModel exchangeListModel;
  private DebugListModel debugListModel;
  private TestPlan testPlan;

  public TestExecutor (TestPlan testPlan, ExchangeTransport exchangeTransport)
    throws RemoteException {

    this.testPlan = testPlan;
    this.exchangeTransport = exchangeTransport;

    exchangeTransport.clear();
    exchangeTransport.addExchangeListener(this);

    exchangeListModel = new ExchangeListModel(testPlan, exchangeTransport);
    debugListModel = new DebugListModel(testPlan, exchangeTransport);
  }

  public TestPlan getTestPlan () {

    return testPlan;
  }

  public ExchangeTransport getExchangeTransport () {

    return exchangeTransport;
  }

  public ExchangeListModel getExchangeListModel () {

    return exchangeListModel;
  }

  public DebugListModel getDebugListModel () {

    return debugListModel;
  }

  public boolean isExecuting () {

    return executing.get();
  }

  @Override
  public void receive (ExchangeEvent exchangeEvent) {

    if ((outputWriterStashRef.get() != null) && (outputWriterStashRef.get().getException() == null)) {

      CSVWriter outputWriter;

      if ((outputWriter = outputWriterStashRef.get().getOutputWriter()) != null) {
        try {

          Exchange<? extends Task> exchange = exchangeEvent.getExchange();

          synchronized (outputWriter) {
            outputWriter.write(exchange.getHostId(), exchange.getTaskName(), String.valueOf(exchange.isSuccess()), String.valueOf(exchange.getStartMillis()), String.valueOf(exchange.getStopMillis()));
          }
        }
        catch (Exception exception) {
          outputWriterStashRef.get().setException(exception);
          cancel();
        }
      }
    }
  }

  private void fireExecutionStarted (TestExecutorEvent testExecutorEvent) {

    for (TestExecutorListener testExecutorListener : listenerList) {
      testExecutorListener.executionStarted(testExecutorEvent);
    }
  }

  private void fireExecutionStopped (TestExecutorEvent testExecutorEvent) {

    for (TestExecutorListener testExecutorListener : listenerList) {
      testExecutorListener.executionStopped(testExecutorEvent);
    }
  }

  private OuroborosCart[] getOuroborosCarts ()
    throws NamingException {

    OuroborosCart[] ouroborosCarts;
    LinkedList<OuroborosCart> ouroborosCartList;

    ouroborosCartList = new LinkedList<OuroborosCart>();
    for (Map.Entry<String, String> serverEntry : testPlan.getServerMap().entrySet()) {
      ouroborosCartList.add(new OuroborosCart(serverEntry.getKey(), OuroborosImpl.getRemoteInterface(serverEntry.getValue())));
    }

    ouroborosCarts = new OuroborosCart[ouroborosCartList.size()];
    ouroborosCartList.toArray(ouroborosCarts);

    return ouroborosCarts;
  }

  public Exception[] cancel () {

    Exception[] exceptions;
    LinkedList<Exception> exceptionList;
    OuroborosCart[] ouroborosCarts;
    CancelWorker[] cancelWorkers;
    CountDownLatch terminationLatch;
    int workerIndex = 0;

    try {
      ouroborosCarts = getOuroborosCarts();
      cancelWorkers = new CancelWorker[ouroborosCarts.length];
      terminationLatch = new CountDownLatch(ouroborosCarts.length);

      for (OuroborosCart ouroborosCart : ouroborosCarts) {
        new Thread(cancelWorkers[workerIndex++] = new CancelWorker(ouroborosCart.getOuroboros(), terminationLatch)).start();
      }

      terminationLatch.await();

      exceptionList = new LinkedList<Exception>();
      for (CancelWorker cancelWorker : cancelWorkers) {
        if (cancelWorker.getException() != null) {
          exceptionList.add(cancelWorker.getException());
        }
      }

      exceptions = new Exception[exceptionList.size()];
      exceptionList.toArray(exceptions);

      return exceptions;
    }
    catch (Exception exception) {

      return new Exception[] {exception};
    }
  }

  public synchronized Exception[] execute () {

    Exception[] exceptions = null;
    LinkedList<Exception> exceptionList;
    OuroborosCart[] ouroborosCarts;
    ExecuteWorker[] executeWorkers;
    CountDownLatch terminationLatch;
    int workerIndex = 0;

    executing.set(true);
    try {
      fireExecutionStarted(new TestExecutorEvent(this));

      ouroborosCarts = getOuroborosCarts();
      executeWorkers = new ExecuteWorker[ouroborosCarts.length];
      terminationLatch = new CountDownLatch(ouroborosCarts.length);

      if ((testPlan.getOutputPath() != null) && (testPlan.getOutputPath().length() > 0)) {
        outputWriterStashRef.set(new CsvWriterStash(new CSVWriter(new FileOutputStream(testPlan.getOutputPath(), false), new String[] {"host", "task", "success", "start", "stop"})));
      }

      for (OuroborosCart ouroborosCart : ouroborosCarts) {
        new Thread(executeWorkers[workerIndex] = new ExecuteWorker(workerIndex++, ouroborosCart.getHostId(), ouroborosCart.getOuroboros(), terminationLatch)).start();
      }

      terminationLatch.await();

      exceptionList = new LinkedList<Exception>();
      for (ExecuteWorker executeWorker : executeWorkers) {
        if (executeWorker.getException() != null) {
          exceptionList.add(executeWorker.getException());
        }
      }

      exceptions = new Exception[exceptionList.size()];
      exceptionList.toArray(exceptions);

      return exceptions;
    }
    catch (Exception exception) {

      return (exceptions = new Exception[] {exception});
    }
    finally {
      try {
        if (outputWriterStashRef.get() != null) {
          if (outputWriterStashRef.get().getOutputWriter() != null) {
            outputWriterStashRef.get().getOutputWriter().close();
          }

          if (outputWriterStashRef.get().getException() != null) {
            if (exceptions == null) {

              return new Exception[] {outputWriterStashRef.get().getException()};
            }
            else {

              Exception[] expandedExceptions = new Exception[exceptions.length + 1];

              System.arraycopy(exceptions, 0, expandedExceptions, 0, exceptions.length);
              expandedExceptions[exceptions.length] = outputWriterStashRef.get().getException();

              return expandedExceptions;
            }
          }
        }
      }
      catch (IOException ioException) {
        if (exceptions == null) {

          return (outputWriterStashRef.get().getException() != null) ? new Exception[] {ioException, outputWriterStashRef.get().getException()} : new Exception[] {ioException};
        }
        else {

          Exception[] expandedExceptions = new Exception[exceptions.length + ((outputWriterStashRef.get().getException() != null) ? 2 : 1)];

          System.arraycopy(exceptions, 0, expandedExceptions, 0, exceptions.length);
          expandedExceptions[exceptions.length] = ioException;

          if (outputWriterStashRef.get().getException() != null) {
            expandedExceptions[exceptions.length + 1] = outputWriterStashRef.get().getException();
          }

          return expandedExceptions;
        }
      }
      finally {
        outputWriterStashRef.set(null);
        fireExecutionStopped(new TestExecutorEvent(this));
        executing.set(false);
      }
    }
  }

  public void addTestExecutorListener (TestExecutorListener testExecutorListener) {

    listenerList.addListener(testExecutorListener);
  }

  public void removeTestExecutorListener (TestExecutorListener testExecutorListener) {

    listenerList.removeListener(testExecutorListener);
  }

  private class CsvWriterStash {

    private Exception exception;
    private CSVWriter outputWriter;

    private CsvWriterStash (CSVWriter outputWriter) {

      this.outputWriter = outputWriter;
    }

    public CSVWriter getOutputWriter () {

      return outputWriter;
    }

    public Exception getException () {

      return exception;
    }

    public void setException (Exception exception) {

      this.exception = exception;
    }
  }

  private class OuroborosCart {

    private Ouroboros ouroboros;
    private String hostId;

    private OuroborosCart (String hostId, Ouroboros ouroboros) {

      this.hostId = hostId;
      this.ouroboros = ouroboros;
    }

    public String getHostId () {

      return hostId;
    }

    public Ouroboros getOuroboros () {

      return ouroboros;
    }
  }

  private class CancelWorker implements Runnable {

    private Exception exception;
    private CountDownLatch terminationLatch;
    private Ouroboros ouroboros;

    private CancelWorker (Ouroboros ouroboros, CountDownLatch terminationLatch) {

      this.ouroboros = ouroboros;
      this.terminationLatch = terminationLatch;
    }

    public Exception getException () {

      return exception;
    }

    @Override
    public void run () {

      try {
        ouroboros.setEnabled(false);
      }
      catch (Exception exception) {
        this.exception = exception;
      }
      finally {
        terminationLatch.countDown();
      }
    }
  }

  private class ExecuteWorker implements Runnable {

    private Exception exception;
    private CountDownLatch terminationLatch;
    private Ouroboros ouroboros;
    private String hostId;
    private int hostIndex;

    public ExecuteWorker (int hostIndex, String hostId, Ouroboros ouroboros, CountDownLatch terminationLatch) {

      this.hostIndex = hostIndex;
      this.hostId = hostId;
      this.ouroboros = ouroboros;
      this.terminationLatch = terminationLatch;
    }

    public Exception getException () {

      return exception;
    }

    @Override
    public void run () {

      try {
        ouroboros.setEnabled(true);
        ouroboros.execute(hostIndex, hostId, testPlan.getRootTask(), exchangeTransport);
      }
      catch (Exception exception) {
        this.exception = exception;
      }
      finally {
        try {
          ouroboros.setEnabled(false);
        }
        catch (RemoteException remoteException) {
          exception = (exception == null) ? remoteException : exception;
        }
        finally {
          terminationLatch.countDown();
        }
      }
    }
  }
}
