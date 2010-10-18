package com.codeasylum.review

object TextualDiff {
   def buildPath(orig: Array[Object], rev: Array[Object]) {

      if (orig == null)
         throw new IllegalArgumentException(
            "original sequence is null")
      if (rev == null)
         throw new IllegalArgumentException(
            "revised sequence is null")


   val N = orig.length
   val M = rev.length
   val MAX = N + M + 1
   val size = 1 + 2 * MAX
   val middle = (size + 1) / 2

   final PathNode diagonal[] = new PathNode[size];

   PathNode path = null;

   diagonal[middle + 1] = new Snake(0, -1, null);
   for (int d = 0;
   d < MAX;
   d ++) {
      for (int k = -d;
   k <= d;
   k += 2) {
   final int kmiddle = middle + k;
   final int kplus = kmiddle + 1;
   final int kminus = kmiddle - 1;
   PathNode prev = null;

   int i;
   if ((k == -d)
      || (k != d && diagonal[kminus].i < diagonal[kplus].i)) {
      i = diagonal[kplus].i;
      prev = diagonal[kplus];
   } else {
      i = diagonal[kminus].i + 1;
      prev = diagonal[kminus];
   }

   diagonal[kminus] = null; // no longer used

   int j = i - k;

   PathNode node = new DiffNode(i, j, prev);

   // orig and rev are zero-based
   // but the algorithm is one-based
   // that's why there's no +1 when indexing the sequences
   while (i < N && j < M && orig[i].equals(rev[j])) {
      i ++;
      j ++;
   }
   if (i > node.i)
      node = new Snake(i, j, node);

   diagonal[kmiddle] = node;

   if (i >= N && j >= M) {
      return diagonal[kmiddle];
   }
}
diagonal[middle + d - 1] = null;

}
// According to Myers, this cannot happen
throw new DifferentiationFailedException (
"could not find a diff path");
}
}


}