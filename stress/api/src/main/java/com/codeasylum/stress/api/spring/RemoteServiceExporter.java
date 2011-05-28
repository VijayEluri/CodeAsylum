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
package com.codeasylum.stress.api.spring;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.rmi.RmiBasedExporter;

public class RemoteServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean {

  private Registry registry;
  private Remote exportedObject;
  private RMIClientSocketFactory clientSocketFactory;
  private RMIServerSocketFactory serverSocketFactory;
  private RMIClientSocketFactory registryClientSocketFactory;
  private RMIServerSocketFactory registryServerSocketFactory;
  private String serviceName;
  private String registryHost;
  private int servicePort = 0;  // anonymous port
  private int registryPort = Registry.REGISTRY_PORT;
  private boolean alwaysCreateRegistry = false;
  private boolean replaceExistingBinding = true;
  private boolean createdRegistry = false;

  public void setServiceName (String serviceName) {

    this.serviceName = serviceName;
  }

  public void setServicePort (int servicePort) {

    this.servicePort = servicePort;
  }

  public void setClientSocketFactory (RMIClientSocketFactory clientSocketFactory) {

    this.clientSocketFactory = clientSocketFactory;
  }

  public void setServerSocketFactory (RMIServerSocketFactory serverSocketFactory) {

    this.serverSocketFactory = serverSocketFactory;
  }

  public void setRegistry (Registry registry) {

    this.registry = registry;
  }

  public void setRegistryHost (String registryHost) {

    this.registryHost = registryHost;
  }

  public void setRegistryPort (int registryPort) {

    this.registryPort = registryPort;
  }

  public void setRegistryClientSocketFactory (RMIClientSocketFactory registryClientSocketFactory) {

    this.registryClientSocketFactory = registryClientSocketFactory;
  }

  public void setRegistryServerSocketFactory (RMIServerSocketFactory registryServerSocketFactory) {

    this.registryServerSocketFactory = registryServerSocketFactory;
  }

  public void setAlwaysCreateRegistry (boolean alwaysCreateRegistry) {

    this.alwaysCreateRegistry = alwaysCreateRegistry;
  }

  public void setReplaceExistingBinding (boolean replaceExistingBinding) {

    this.replaceExistingBinding = replaceExistingBinding;
  }

  public void afterPropertiesSet () throws RemoteException {

    prepare();
  }

  public void prepare () throws RemoteException {

    checkService();

    if (this.serviceName == null) {
      throw new IllegalArgumentException("Property 'serviceName' is required");
    }

    // Check socket factories for exported object.
    if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
      this.serverSocketFactory = (RMIServerSocketFactory)this.clientSocketFactory;
    }
    if ((this.clientSocketFactory != null && this.serverSocketFactory == null) ||
      (this.clientSocketFactory == null && this.serverSocketFactory != null)) {
      throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
    }

    // Check socket factories for RMI registry.
    if (this.registryClientSocketFactory instanceof RMIServerSocketFactory) {
      this.registryServerSocketFactory = (RMIServerSocketFactory)this.registryClientSocketFactory;
    }
    if (this.registryClientSocketFactory == null && this.registryServerSocketFactory != null) {
      throw new IllegalArgumentException("RMIServerSocketFactory without RMIClientSocketFactory for registry not supported");
    }

    this.createdRegistry = false;

    // Determine RMI registry to use.
    if (this.registry == null) {
      this.registry = getRegistry(this.registryHost, this.registryPort, this.registryClientSocketFactory, this.registryServerSocketFactory);
      this.createdRegistry = true;
    }

    // Initialize and cache exported object.
    this.exportedObject = getObjectToExport();

    if (logger.isInfoEnabled()) {
      logger.info("Binding service '" + this.serviceName + "' to RMI registry: " + this.registry);
    }

    // Bind RMI object to registry.
    try {
      if (this.replaceExistingBinding) {
        this.registry.rebind(this.serviceName, this.exportedObject);
      }
      else {
        this.registry.bind(this.serviceName, this.exportedObject);
      }
    }
    catch (AlreadyBoundException ex) {
      // Already an RMI object bound for the specified service name...
      unexportObjectSilently();
      throw new IllegalStateException("Already an RMI object bound for name '" + this.serviceName + "': " + ex.toString());
    }
    catch (RemoteException ex) {
      // Registry binding failed: let's unexport the RMI object as well.
      unexportObjectSilently();
      throw ex;
    }
  }

  protected Registry getRegistry (String registryHost, int registryPort, RMIClientSocketFactory clientSocketFactory, RMIServerSocketFactory serverSocketFactory)
    throws RemoteException {

    if (registryHost != null) {
      // Host explicitly specified: only lookup possible.
      if (logger.isInfoEnabled()) {
        logger.info("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]");
      }
      Registry reg = LocateRegistry.getRegistry(registryHost, registryPort, clientSocketFactory);
      testRegistry(reg);
      return reg;
    }
    else {
      return getRegistry(registryPort, clientSocketFactory, serverSocketFactory);
    }
  }

  protected Registry getRegistry (int registryPort, RMIClientSocketFactory clientSocketFactory, RMIServerSocketFactory serverSocketFactory)
    throws RemoteException {

    if (clientSocketFactory != null) {
      if (this.alwaysCreateRegistry) {
        logger.info("Creating new RMI registry");
        return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
      }
      if (logger.isInfoEnabled()) {
        logger.info("Looking for RMI registry at port '" + registryPort + "', using custom socket factory");
      }
      synchronized (LocateRegistry.class) {
        try {
          // Retrieve existing registry.
          Registry reg = LocateRegistry.getRegistry(null, registryPort, clientSocketFactory);
          testRegistry(reg);
          return reg;
        }
        catch (RemoteException ex) {
          logger.debug("RMI registry access threw exception", ex);
          logger.info("Could not detect RMI registry - creating new one");
          // Assume no registry found -> create new one.
          return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
        }
      }
    }
    else {
      return getRegistry(registryPort);
    }
  }

  protected Registry getRegistry (int registryPort) throws RemoteException {

    if (this.alwaysCreateRegistry) {
      logger.info("Creating new RMI registry");
      return LocateRegistry.createRegistry(registryPort);
    }
    if (logger.isInfoEnabled()) {
      logger.info("Looking for RMI registry at port '" + registryPort + "'");
    }
    synchronized (LocateRegistry.class) {
      try {
        // Retrieve existing registry.
        Registry reg = LocateRegistry.getRegistry(registryPort);
        testRegistry(reg);
        return reg;
      }
      catch (RemoteException ex) {
        logger.debug("RMI registry access threw exception", ex);
        logger.info("Could not detect RMI registry - creating new one");
        // Assume no registry found -> create new one.
        return LocateRegistry.createRegistry(registryPort);
      }
    }
  }

  protected void testRegistry (Registry registry) throws RemoteException {

    registry.list();
  }

  public void destroy () throws RemoteException {

    if (logger.isInfoEnabled()) {
      logger.info("Unbinding RMI service '" + this.serviceName + "' from registry" + (this.createdRegistry ? (" at port '" + this.registryPort + "'") : ""));
    }
    try {
      this.registry.unbind(this.serviceName);
    }
    catch (NotBoundException ex) {
      if (logger.isWarnEnabled()) {
        logger.warn("RMI service '" + this.serviceName + "' is not bound to registry" + (this.createdRegistry ? (" at port '" + this.registryPort + "' anymore") : ""), ex);
      }
    }
    finally {
      unexportObjectSilently();
    }
  }

  private void unexportObjectSilently () {

    try {
      UnicastRemoteObject.unexportObject(this.exportedObject, true);
    }
    catch (NoSuchObjectException ex) {
      if (logger.isWarnEnabled()) {
        logger.warn("RMI object for service '" + this.serviceName + "' isn't exported anymore", ex);
      }
    }
  }
}