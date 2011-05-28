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
import java.util.HashMap;
import java.util.Map;

public class PropertyTask extends AbstractTask {

  private HashMap<String, String> propertyMap;

  public PropertyTask () {

    propertyMap = new HashMap<String, String>();
  }

  private PropertyTask (PropertyTask propertyTask) {

    super(propertyTask);

    propertyMap = new HashMap<String, String>(propertyTask.getPropertyMap());
  }

  public HashMap<String, String> getPropertyMap () {

    return propertyMap;
  }

  public void setPropertyMap (HashMap<String, String> propertyMap) {

    this.propertyMap = propertyMap;
  }

  @Override
  public void execute (String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport)
    throws RemoteException {

    if (isEnabled() && ouroboros.isEnabled()) {
      for (Map.Entry<String, String> propertyEntry : propertyMap.entrySet()) {
        PropertyContext.put(propertyEntry.getKey(), propertyEntry.getValue());
      }
    }
  }

  @Override
  public Task deepCopy () {

    return new PropertyTask(this);
  }
}
