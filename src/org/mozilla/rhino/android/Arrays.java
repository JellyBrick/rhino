/*
 * Copyright 2018 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mozilla.rhino.android;

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
