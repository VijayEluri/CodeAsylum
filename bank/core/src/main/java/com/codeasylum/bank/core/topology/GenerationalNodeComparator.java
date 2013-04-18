package com.codeasylum.bank.core.topology;

import java.util.Comparator;

public class GenerationalNodeComparator implements Comparator<Node> {

  private static final GenerationalNodeComparator INSTANCE = new GenerationalNodeComparator();

  public static GenerationalNodeComparator instance () {

    return INSTANCE;
  }

  @Override
  public int compare (Node node1, Node node2) {

    Generation generation1 = node1.getRange().getOldestGeneration();
    Generation generation2 = node2.getRange().getOldestGeneration();
    int stageComparison;
    int generationComparison;

    if ((stageComparison = generation1.getStage() - generation2.getStage()) != 0) {

      return stageComparison;
    }
    if ((generationComparison = generation2.size() - generation1.size()) != 0) {

      return generationComparison;
    }

    return (node1 == node2) ? 0 : 1;
  }
}
