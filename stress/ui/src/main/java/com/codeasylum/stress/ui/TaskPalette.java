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
package com.codeasylum.stress.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.codeasylum.stress.api.ExtendedTaskLoader;
import com.codeasylum.stress.api.RootTask;
import com.codeasylum.stress.api.RootTaskAvatar;
import com.codeasylum.stress.api.Task;
import com.codeasylum.stress.api.TaskAvatar;
import com.codeasylum.stress.api.TaskType;
import org.smallmind.nutsnbolts.util.AlphaNumericComparator;
import org.smallmind.nutsnbolts.util.AlphaNumericConverter;

public class TaskPalette {

  private static final HashMap<Class<? extends Task>, TaskAvatar<?>> AVATAR_MAP = new HashMap<Class<? extends Task>, TaskAvatar<?>>();
  private static final HashMap<TaskType, LinkedList<TaskAvatar<?>>> PALETTE_MAP = new HashMap<TaskType, LinkedList<TaskAvatar<?>>>();
  private static final AlphaNumericComparator<TaskAvatar> AVATAR_COMPARATOR = new AlphaNumericComparator<TaskAvatar>(new AlphaNumericConverter<TaskAvatar>() {

    @Override
    public String toString (TaskAvatar taskAvatar) {

      return taskAvatar.getName();
    }
  });

  public TaskPalette (Set<Class<? extends Task>> taskClassSet)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException {

    TaskAvatar<?> taskAvatar;
    LinkedList<TaskAvatar<?>> avatarList;

    AVATAR_MAP.put(RootTask.class, new RootTaskAvatar());

    for (Class<? extends Task> taskClass : taskClassSet) {
      taskAvatar = (TaskAvatar)Thread.currentThread().getContextClassLoader().loadClass(taskClass.getName() + "Avatar").newInstance();

      AVATAR_MAP.put(taskClass, taskAvatar);
      if ((avatarList = PALETTE_MAP.get(taskAvatar.getType())) == null) {
        PALETTE_MAP.put(taskAvatar.getType(), avatarList = new LinkedList<TaskAvatar<?>>());
      }
      avatarList.add(taskAvatar);
    }

    if (ExtendedTaskLoader.getExtendedPalette() != null) {
      for (Class<? extends Task> taskClass : ExtendedTaskLoader.getExtendedPalette()) {
        taskAvatar = (TaskAvatar)Thread.currentThread().getContextClassLoader().loadClass(taskClass.getName() + "Avatar").newInstance();

        AVATAR_MAP.put(taskClass, taskAvatar);
        if ((avatarList = PALETTE_MAP.get(taskAvatar.getType())) == null) {
          PALETTE_MAP.put(taskAvatar.getType(), avatarList = new LinkedList<TaskAvatar<?>>());
        }
        avatarList.add(taskAvatar);
      }
    }

    for (TaskType taskType : TaskType.values()) {
      if ((avatarList = PALETTE_MAP.get(taskType)) != null) {
        Collections.sort(avatarList, AVATAR_COMPARATOR);
      }
    }
  }

  public TaskAvatar<?> getAvatar (Class<? extends Task> taskClass) {

    return AVATAR_MAP.get(taskClass);
  }

  public List<TaskAvatar<?>> getAvatars (TaskType taskType) {

    LinkedList<TaskAvatar<?>> avatarList;

    return ((avatarList = PALETTE_MAP.get(taskType)) == null) ? null : Collections.unmodifiableList(avatarList);
  }
}
