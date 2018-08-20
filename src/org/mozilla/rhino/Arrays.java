package org.mozilla.rhino;

public class Arrays {

    static boolean deepEquals0(Object e1, Object e2) {
        // BEGIN Android-changed: getComponentType() is faster than instanceof()
        Class<?> cl1 = e1.getClass().getComponentType();
        Class<?> cl2 = e2.getClass().getComponentType();

        if (cl1 != cl2) {
            return false;
        }
        if (e1 instanceof Object[])
            return java.util.Arrays.deepEquals((Object[]) e1, (Object[]) e2);
        else if (cl1 == byte.class)
            return java.util.Arrays.equals((byte[]) e1, (byte[]) e2);
        else if (cl1 == short.class)
            return java.util.Arrays.equals((short[]) e1, (short[]) e2);
        else if (cl1 == int.class)
            return java.util.Arrays.equals((int[]) e1, (int[]) e2);
        else if (cl1 == long.class)
            return java.util.Arrays.equals((long[]) e1, (long[]) e2);
        else if (cl1 == char.class)
            return java.util.Arrays.equals((char[]) e1, (char[]) e2);
        else if (cl1 == float.class)
            return java.util.Arrays.equals((float[]) e1, (float[]) e2);
        else if (cl1 == double.class)
            return java.util.Arrays.equals((double[]) e1, (double[]) e2);
        else if (cl1 == boolean.class)
            return java.util.Arrays.equals((boolean[]) e1, (boolean[]) e2);
        else
            return e1.equals(e2);
        // END Android-changed: getComponentType() is faster than instanceof()
    }
}
