package com.codeasylum.bank.core.store.indigenous;

import java.util.Iterator;

public abstract class Converter implements Iterator<Column<?>> {

  @Override
  public final void remove () {

    throw new UnsupportedOperationException();
  }
}
