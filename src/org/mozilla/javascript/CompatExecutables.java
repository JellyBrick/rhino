package org.mozilla.javascript;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class CompatExecutables {

  static boolean isVarArgs(AccessibleObject object) {
    if (object instanceof Constructor) {
      return ((Constructor<?>) object).isVarArgs();
    } else {
      return ((Method) object).isVarArgs();
    }
  }

  static Class<?> getDeclaringClass(AccessibleObject object) {
    if (object instanceof Constructor) {
      return ((Constructor<?>) object).getDeclaringClass();
    } else if (object instanceof Method) {
      return ((Method) object).getDeclaringClass();
    } else {
      throw new IllegalArgumentException("not Method or Constructor");
    }
  }

  static int getModifiers(AccessibleObject object) {
    if (object instanceof Constructor) {
      return ((Constructor<?>) object).getModifiers();
    } else if (object instanceof Method) {
      return ((Method) object).getModifiers();
    } else {
      throw new IllegalArgumentException("not Method or Constructor");
    }
  }

  static String getName(AccessibleObject object) {
    if (object instanceof Constructor) {
      return ((Constructor<?>) object).getName();
    } else if (object instanceof Method) {
      return ((Method) object).getName();
    } else {
      throw new IllegalArgumentException("not Method or Constructor");
    }
  }

  static Class<?>[] getParameterTypes(AccessibleObject object) {
    if (object instanceof Constructor) {
      return ((Constructor<?>) object).getParameterTypes();
    } else if (object instanceof Method) {
      return ((Method) object).getParameterTypes();
    } else {
      throw new IllegalArgumentException("not Method or Constructor");
    }
  }

  static int getParameterCount(AccessibleObject object) {
    return getParameterTypes(object).length;
  }
}
