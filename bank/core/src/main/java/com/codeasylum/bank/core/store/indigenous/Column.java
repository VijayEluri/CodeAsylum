package com.codeasylum.bank.core.store.indigenous;

public class Column<T> {

  private String key;
  private T value;

  public Column (String key, T value) {

    this.key = key;
    this.value = value;
  }

  public String getKey () {

    return key;
  }

  public T getValue () {

    return value;
  }

  @Override
  public String toString () {

    return new StringBuilder("[key=").append(key).append(", value=").append(value).append(']').toString();
  }
}
