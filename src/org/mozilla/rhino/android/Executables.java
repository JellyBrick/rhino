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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Executables {

    public static boolean isVarArgs(AccessibleObject object) {
        if (object instanceof Constructor) {
            return ((Constructor<?>) object).isVarArgs();
        } else {
            return ((Method) object).isVarArgs();
        }
    }

    public static Class<?> getDeclaringClass(AccessibleObject object) {
        if (object instanceof Constructor) {
            return ((Constructor<?>) object).getDeclaringClass();
        } else if (object instanceof Method) {
            return ((Method) object).getDeclaringClass();
        } else {
            throw new IllegalArgumentException("not Method or Constructor");
        }
    }

    public static int getModifiers(AccessibleObject object) {
        if (object instanceof Constructor) {
            return ((Constructor<?>) object).getModifiers();
        } else if (object instanceof Method) {
            return ((Method) object).getModifiers();
        } else {
            throw new IllegalArgumentException("not Method or Constructor");
        }
    }

    public static String getName(AccessibleObject object) {
        if (object instanceof Constructor) {
            return ((Constructor<?>) object).getName();
        } else if (object instanceof Method) {
            return ((Method) object).getName();
        } else {
            throw new IllegalArgumentException("not Method or Constructor");
        }
    }

    public static Class<?>[] getParameterTypes(AccessibleObject object) {
        if (object instanceof Constructor) {
            return ((Constructor<?>) object).getParameterTypes();
        } else if (object instanceof Method) {
            return ((Method) object).getParameterTypes();
        } else {
            throw new IllegalArgumentException("not Method or Constructor");
        }
    }

    public static int getParameterCount(AccessibleObject object) {
        return getParameterTypes(object).length;
    }
}
