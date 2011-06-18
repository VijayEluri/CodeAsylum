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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OuroborosImpl extends UnicastRemoteObject implements Ouroboros {

  private static final AtomicInteger rmiPort = new AtomicInteger(0);

  private final AtomicBoolean enabled = new AtomicBoolean(false);

  public static Ouroboros getRemoteInterface (String rmiHost)
    throws NamingException {

    Ouroboros ouroboros;
    InitialContext initContext;
    Context rmiContext;

    initContext = new InitialContext();
    rmiContext = (Context)initContext.lookup("rmi://" + rmiHost + ':' + rmiPort.get());
    ouroboros = (Ouroboros)PortableRemoteObject.narrow(rmiContext.lookup(Ouroboros.class.getName()), Ouroboros.class);
    rmiContext.close();
    initContext.close();

    return ouroboros;
  }

  public OuroborosImpl ()
    throws RemoteException {

    super();
  }

  public void setRmiPort (int port) {

    rmiPort.set(port);
  }

  @Override
  public boolean isEnabled () {

    return enabled.get();
  }

  @Override
  public void setEnabled (boolean enabled) {

    this.enabled.set(enabled);
  }

  @Override
  public void execute (String hostId, RootTask rootTask, ExchangeTransport exchangeTransport)
    throws Exception {

    rootTask.execute(hostId, this, exchangeTransport);
  }

  @Override
  protected void finalize ()
    throws RemoteException, MalformedURLException, NotBoundException {

    Naming.unbind(Ouroboros.class.getName());
  }

  public static void main (String... args) {

    boolean init = false;

    try {
      new ExtendedTaskLoader();
      init = true;
    }
    catch (Exception exception) {
      JavaErrorDialog.showJavaErrorDialog(null, null, exception);
    }

    if (init) {
      new ClassPathXmlApplicationContext("com/codeasylum/stress/api/ouroboros.xml");
    }
  }
}
