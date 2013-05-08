package com.codeasylum.bank.core;

public interface Store<K extends Key, V extends Value> {

  public abstract void insert (K key, V value);

  public abstract void update (K key, V value);

  public abstract void delete (K key);
}
