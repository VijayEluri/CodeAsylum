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

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import org.smallmind.nutsnbolts.lang.ClasspathClassGate;
import org.smallmind.nutsnbolts.lang.GatingClassLoader;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ExtendedTaskLoader {

  private static final LazyInstanceHolder lazyInstanceHolder = new LazyInstanceHolder();

  private final AtomicReference<Class<? extends Task>[]> extendedPaletteRef = new AtomicReference<Class<? extends Task>[]>();

  public synchronized static ExtendedTaskLoader init ()
    throws TaskExtensionException {

    ExtendedTaskLoader extendedTaskLoader;

    if ((extendedTaskLoader = lazyInstanceHolder.getExtendedTaskLoader()) == null) {
      lazyInstanceHolder.setExtendedTaskLoader(new ExtendedTaskLoader());
    }

    return extendedTaskLoader;
  }

  public static Class<? extends Task>[] getExtendedPalette () {

    return lazyInstanceHolder.getExtendedTaskLoader().getExtendedPaletteRef().get();
  }

  private ExtendedTaskLoader ()
    throws TaskExtensionException {

    File extensionFile = new File(System.getProperty("user.dir") + "/jormungandr-extension.xml");

    if (extensionFile.exists()) {

      FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(extensionFile.getAbsolutePath());
      TaskExtender taskExtender;

      try {
        taskExtender = applicationContext.getBean(TaskExtender.class);
      }
      catch (Throwable throwable) {
        throw new TaskExtensionException(throwable, "Unable to execute task extension configuration(%s)", extensionFile.getAbsolutePath());
      }

      if ((taskExtender.getClasspathComponents() != null) && (taskExtender.getClasspathComponents().length > 0)) {

        File classpathComponentFile;
        String[] normalizedPathComponents = new String[taskExtender.getClasspathComponents().length];
        int componentIndex = 0;

        for (String classpathComponent : taskExtender.getClasspathComponents()) {
          classpathComponentFile = new File(classpathComponent);
          normalizedPathComponents[componentIndex++] = classpathComponentFile.isAbsolute() ? classpathComponent : System.getProperty("user.dir") + '/' + classpathComponent;
        }

        Thread.currentThread().setContextClassLoader(new GatingClassLoader(Thread.currentThread().getContextClassLoader(), -1, new ClasspathClassGate(normalizedPathComponents)));
      }

      if ((taskExtender.getPalette() != null) && (taskExtender.getPalette().length > 0)) {

        Class<? extends Task>[] extendedPalette = new Class[taskExtender.getPalette().length];
        int paletteIndex = 0;

        try {
          for (String extendedPaletteClassName : taskExtender.getPalette()) {
            extendedPalette[paletteIndex++] = (Class<? extends Task>)Thread.currentThread().getContextClassLoader().loadClass(extendedPaletteClassName);
          }
        }
        catch (ClassNotFoundException classNotFoundException) {
          throw new TaskExtensionException(classNotFoundException);
        }

        extendedPaletteRef.set(extendedPalette);
      }
    }
  }

  public AtomicReference<Class<? extends Task>[]> getExtendedPaletteRef () {

    return extendedPaletteRef;
  }

  private static class LazyInstanceHolder {

    private ExtendedTaskLoader extendedTaskLoader;

    public ExtendedTaskLoader getExtendedTaskLoader () {

      return extendedTaskLoader;
    }

    public void setExtendedTaskLoader (ExtendedTaskLoader extendedTaskLoader) {

      this.extendedTaskLoader = extendedTaskLoader;
    }
  }
}
