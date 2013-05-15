package com.codeasylum.bank.core;

import org.smallmind.nutsnbolts.lang.FormattedException;

public class ProcessException extends FormattedException {

  public ProcessException (String message, Object... args) {

    super(message, args);
  }

  public ProcessException (Throwable throwable) {

    super(throwable);
  }
}


