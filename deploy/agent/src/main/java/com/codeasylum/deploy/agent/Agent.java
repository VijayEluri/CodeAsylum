package com.codeasylum.deploy.agent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Agent extends Remote {

  public abstract void execute ()
    throws RemoteException;
}
