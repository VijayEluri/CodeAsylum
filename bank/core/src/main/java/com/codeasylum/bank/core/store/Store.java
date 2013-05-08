package com.codeasylum.bank.core.store;

import com.codeasylum.bank.core.Key;
import com.codeasylum.bank.core.Value;

public interface Store<K extends Key, V extends Value> {

  public abstract void insert (K key, V value);

  public abstract void update (K key, V value);

  public abstract void delete (K key);
}
