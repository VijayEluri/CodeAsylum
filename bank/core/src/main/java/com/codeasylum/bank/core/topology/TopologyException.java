package com.codeasylum.bank.core.topology;

import org.smallmind.nutsnbolts.lang.FormattedException;

public class TopologyException extends FormattedException {

  public TopologyException (String message, Object... args) {

    super(message, args);
  }
}
