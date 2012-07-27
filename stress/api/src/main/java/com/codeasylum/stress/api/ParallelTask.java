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

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class ParallelTask extends AbstractPluralContainer {

  public ParallelTask () {

    super();
  }

  private ParallelTask (ParallelTask parallel) {

    super(parallel);
  }

  @Override
  public void execute (int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws Exception {

    if (isEnabled() && ouroboros.isEnabled()) {

      LinkedList<Task> enabledList = new LinkedList<Task>();
      int threadCount = 0;

      for (Task task : this) {
        if (task.isEnabled()) {
          if ((task instanceof Replicated) && (((Replicated)task).size() == 0)) {
            throw new TaskExecutionException("The %s(%s) has been configured to iterate zero times (i.e. not at all)", task.getClass().getSimpleName(), task.getName());
          }

          enabledList.add(task);
          threadCount += (task instanceof Replicated) ? ((Replicated)task).size() : 1;
        }
      }

      CountDownLatch startLatch = new CountDownLatch(1);
      CountDownLatch stopLatch = new CountDownLatch(threadCount);
      LinkedList<ParallelWorker> workerList;
      ParallelWorker parallelWorker;

      workerList = new LinkedList<ParallelWorker>();
      for (Task task : enabledList) {
        if (!(task instanceof Replicated)) {
          workerList.add(parallelWorker = new ParallelWorker(hostIndex, hostId, ouroboros, exchangeTransport, task, -1, startLatch, stopLatch));
          new Thread(parallelWorker).start();
        }
        else {
          for (int count = 0; count < ((Replicated)task).size(); count++) {
            workerList.add(parallelWorker = new ParallelWorker(hostIndex, hostId, ouroboros, exchangeTransport, task, count, startLatch, stopLatch));
            new Thread(parallelWorker).start();
          }
        }
      }

      startLatch.countDown();
      stopLatch.await();

      for (ParallelWorker worker : workerList) {
        if (worker.getException() != null) {
          throw worker.getException();
        }
      }
    }
  }

  private class ParallelWorker implements Runnable {

    private Exception exception;
    private CountDownLatch startLatch;
    private CountDownLatch stopLatch;
    private Ouroboros ouroboros;
    private ExchangeTransport exchangeTransport;
    private Task task;
    private String hostId;
    private int hostIndex;
    private int taskIndex;

    private ParallelWorker (int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport, Task task, int taskIndex, CountDownLatch startLatch, CountDownLatch stopLatch) {

      this.hostIndex = hostIndex;
      this.hostId = hostId;
      this.ouroboros = ouroboros;
      this.exchangeTransport = exchangeTransport;
      this.task = task;
      this.taskIndex = taskIndex;
      this.startLatch = startLatch;
      this.stopLatch = stopLatch;
    }

    public Exception getException () {

      return exception;
    }

    public void run () {

      try {
        startLatch.await();
        if (ouroboros.isEnabled()) {
          if (taskIndex >= 0) {

            String key;

            if (((key = ((Replicated)task).getKey()) != null) && (key.length() > 0)) {
              PropertyContext.put(key, String.valueOf(taskIndex));
            }
          }

          task.deepCopy().execute(hostIndex, hostId, ouroboros, exchangeTransport);
        }
      }
      catch (Exception exception) {
        this.exception = exception;
      }
      finally {
        stopLatch.countDown();
      }
    }
  }

  @Override
  public Task deepCopy () {

    return new ParallelTask(this);
  }
}
