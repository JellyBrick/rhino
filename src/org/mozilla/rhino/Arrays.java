package org.mozilla.rhino;

public class Arrays {

  static boolean deepEquals0(Object e1, Object e2) {
    assert e1 != null;
    boolean eq;
    if (e1 instanceof Object[] && e2 instanceof Object[])
      eq = java.util.Arrays.deepEquals((Object[]) e1, (Object[]) e2);
    else if (e1 instanceof byte[] && e2 instanceof byte[])
      eq = java.util.Arrays.equals((byte[]) e1, (byte[]) e2);
    else if (e1 instanceof short[] && e2 instanceof short[])
      eq = java.util.Arrays.equals((short[]) e1, (short[]) e2);
    else if (e1 instanceof int[] && e2 instanceof int[])
      eq = java.util.Arrays.equals((int[]) e1, (int[]) e2);
    else if (e1 instanceof long[] && e2 instanceof long[])
      eq = java.util.Arrays.equals((long[]) e1, (long[]) e2);
    else if (e1 instanceof char[] && e2 instanceof char[])
      eq = java.util.Arrays.equals((char[]) e1, (char[]) e2);
    else if (e1 instanceof float[] && e2 instanceof float[])
      eq = java.util.Arrays.equals((float[]) e1, (float[]) e2);
    else if (e1 instanceof double[] && e2 instanceof double[])
      eq = java.util.Arrays.equals((double[]) e1, (double[]) e2);
    else if (e1 instanceof boolean[] && e2 instanceof boolean[])
      eq = java.util.Arrays.equals((boolean[]) e1, (boolean[]) e2);
    else
      eq = e1.equals(e2);
    return eq;
  }
}
