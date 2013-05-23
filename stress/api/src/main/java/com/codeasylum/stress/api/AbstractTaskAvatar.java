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
package com.codeasylum.stress.api;

import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ImageIcon;
import org.smallmind.swing.SmallMindGrayFilter;

public abstract class AbstractTaskAvatar<T extends Task> implements TaskAvatar<T> {

  private final AtomicReference<ImageIcon> GRAY_ICON_24 = new AtomicReference<ImageIcon>();

  @Override
  public Class[] mustPrecede () {

    return null;
  }

  @Override
  public Class[] mustNotPrecede () {

    return null;
  }

  @Override
  public Class[] mustProceed () {

    return null;
  }

  @Override
  public Class[] mustNotProceed () {

    return null;
  }

  @Override
  public ImageIcon getGrayIcon24 () {

    ImageIcon grayIcon;

    if ((grayIcon = GRAY_ICON_24.get()) == null) {
      GRAY_ICON_24.compareAndSet(null, grayIcon = new ImageIcon(SmallMindGrayFilter.createDisabledImage(getIcon24().getImage())));
    }

    return grayIcon;
  }
}
