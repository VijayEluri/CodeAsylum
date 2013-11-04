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
package com.codeasylum.stress.api;

public class SerialTask extends AbstractPluralContainer {

  public SerialTask () {

    super();
  }

  private SerialTask (SerialTask serial) {

    super(serial);
  }

  @Override
  public void execute (long timeDifferential, int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws Exception {

    if (isEnabled() && ouroboros.isEnabled()) {
      for (Task task : this) {
        if (!ouroboros.isEnabled()) {
          break;
        }
        else {
          if (task.isEnabled()) {
            if (!(task instanceof Replicated)) {
              task.deepCopy().execute(timeDifferential, hostIndex, hostId, ouroboros, exchangeTransport);
            }
            else {
              if (((Replicated)task).size() == 0) {
                throw new TaskExecutionException("The %s(%s) has been configured to iterate zero times (i.e. not at all)", task.getClass().getSimpleName(), task.getName());
              }

              for (int count = 0; count < ((Replicated)task).size(); count++) {
                if (!ouroboros.isEnabled()) {
                  break;
                }
                else {

                  String key;

                  if (((key = ((Replicated)task).getKey()) != null) && (key.length() > 0)) {
                    PropertyContext.put(key, String.valueOf(count));
                  }

                  task.deepCopy().execute(timeDifferential, hostIndex, hostId, ouroboros, exchangeTransport);
                }
              }
            }
          }
        }
      }
    }
  }

  public Task deepCopy () {

    return new SerialTask(this);
  }
}
