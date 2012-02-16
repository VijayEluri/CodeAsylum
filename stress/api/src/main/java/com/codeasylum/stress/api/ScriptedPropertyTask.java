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

import bsh.EvalError;
import org.smallmind.nutsnbolts.reflection.type.converter.StringConversionException;
import org.smallmind.nutsnbolts.util.PropertyExpanderException;

public class ScriptedPropertyTask extends AbstractTask {

  private Attribute<String> valueAttribute = new Attribute<String>(String.class, "", true);
  private String key;

  public ScriptedPropertyTask () {

  }

  public ScriptedPropertyTask (ScriptedPropertyTask scriptedPropertyTask) {

    super(scriptedPropertyTask);

    key = scriptedPropertyTask.getKey();
    valueAttribute = new Attribute<String>(String.class, scriptedPropertyTask.getValueAttribute());
  }

  public String getKey () {

    return key;
  }

  public void setKey (String key) {

    this.key = key;
  }

  public Attribute<String> getValueAttribute () {

    return valueAttribute;
  }

  public void setValueAttribute (Attribute<String> valueAttribute) {

    this.valueAttribute = valueAttribute;
  }

  @Override
  public void execute (int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws ScriptInterpolationException {

    PropertyContext.put(key, valueAttribute.get(this));
  }

  @Override
  public Task deepCopy () {

    return new ScriptedPropertyTask(this);
  }
}
