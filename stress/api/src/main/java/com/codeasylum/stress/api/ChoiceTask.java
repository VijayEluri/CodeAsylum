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

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;

public class ChoiceTask extends AbstractPluralContainer {

  private static final Random RANDOM = new SecureRandom();

  private int[] percentages = new int[] {50};

  public ChoiceTask () {

    super();
  }

  private ChoiceTask (ChoiceTask choiceTask) {

    super(choiceTask);

    percentages = choiceTask.getPercentages();
  }

  public int[] getPercentages () {

    return percentages;
  }

  public void setPercentages (int[] percentages) {

    this.percentages = percentages;
  }

  @Override
  public void execute (String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport) throws Exception {

    LinkedList<Task> taskList = new LinkedList<Task>();
    boolean tasked = false;
    int choiceRandom = RANDOM.nextInt(100);

    for (Task task : this) {
      if (task.isEnabled()) {
        if (!(task instanceof Replicated)) {
          taskList.add(task);
        }
        else {
          if (((Replicated)task).size() == 0) {
            throw new TaskExecutionException("The %s(%s) has been configured to iterate zero times (i.e. not at all)", task.getClass().getSimpleName(), task.getName());
          }

          for (int count = 0; count < ((Replicated)task).size(); count++) {
            taskList.add(task);
          }
        }
      }
    }

    if (taskList.size() != percentages.length + 1) {
      throw new TaskExecutionException("The number of choice ranges(%d) configured in %s(%s) doesn't match the number of enabled child tasks(%d)", percentages.length + 1, getClass().getSimpleName(), getName(), taskList.size());
    }

    for (int index = 0; index < percentages.length; index++) {
      if (choiceRandom < percentages[index]) {
        taskList.get(index).deepCopy().execute(hostId, ouroboros, exchangeTransport);
        tasked = true;
        break;
      }
    }
    if (!tasked) {
      taskList.getLast().deepCopy().execute(hostId, ouroboros, exchangeTransport);
    }
  }

  @Override
  public Task deepCopy () {

    return new ChoiceTask(this);
  }
}
