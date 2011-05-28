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

import java.io.Serializable;
import bsh.Interpreter;
import org.smallmind.nutsnbolts.lang.StaticInitializationError;
import org.smallmind.nutsnbolts.reflection.type.TypeUtility;
import org.smallmind.nutsnbolts.reflection.type.converter.DefaultStringConverterFactory;
import org.smallmind.nutsnbolts.reflection.type.converter.StringConverterFactory;
import org.smallmind.nutsnbolts.util.PropertyExpander;
import org.smallmind.nutsnbolts.util.PropertyExpanderException;
import org.smallmind.nutsnbolts.util.SystemPropertyMode;

public class Attribute<T> implements Serializable {

  private static final StringConverterFactory STRING_CONVERTER_FACTORY = new DefaultStringConverterFactory();
  private static final PropertyExpander PROPERTY_EXPANDER;
  private static final ThreadLocal<Interpreter> INTERPRETER_THREAD_LOCAL = new ThreadLocal<Interpreter>() {

    @Override
    protected Interpreter initialValue () {

      return new Interpreter();
    }
  };

  private Class<T> managedClass;
  private String script;
  private boolean formula;

  static {

    try {
      PROPERTY_EXPANDER = new PropertyExpander(false, SystemPropertyMode.NEVER, false);
    }
    catch (PropertyExpanderException propertyExpanderException) {
      throw new StaticInitializationError(propertyExpanderException);
    }
  }

  public Attribute (Class<T> managedClass) {

    this(managedClass, null, false);
  }

  public Attribute (Class<T> managedClass, boolean formula) {

    this(managedClass, null, formula);
  }

  public Attribute (Class<T> managedClass, String script) {

    this(managedClass, script, false);
  }

  public Attribute (Class<T> managedClass, String script, boolean formula) {

    this.managedClass = managedClass;
    this.script = script;
    this.formula = formula;
  }

  public Attribute (Class<T> managedClass, Attribute attribute) {

    if (!managedClass.equals(attribute.getManagedClass())) {
      throw new IllegalArgumentException("Mismatched managed classes");
    }

    this.managedClass = managedClass;

    script = attribute.getScript();
    formula = attribute.isFormula();
  }

  public Class<T> getManagedClass () {

    return managedClass;
  }

  public void setManagedClass (Class<T> managedClass) {

    this.managedClass = managedClass;
  }

  public String getScript () {

    return script;
  }

  public void setScript (String script) {

    this.script = script;
  }

  public boolean isFormula () {

    return formula;
  }

  public void setFormula (boolean formula) {

    this.formula = formula;
  }

  public T get (Task task)
    throws ScriptInterpolationException {

    if (script == null) {

      return managedClass.cast(TypeUtility.getDefaultValue(managedClass));
    }

    try {
      return (!formula) ? managedClass.cast(STRING_CONVERTER_FACTORY.getStringConverter(managedClass).convert(PROPERTY_EXPANDER.expand(script, PropertyContext.getMap()))) : managedClass.cast(INTERPRETER_THREAD_LOCAL.get().eval(PROPERTY_EXPANDER.expand(script, PropertyContext.getMap())));
    }
    catch (Throwable throwable) {
      throw new ScriptInterpolationException(throwable, task);
    }
  }
}
