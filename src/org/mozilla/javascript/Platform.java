package org.mozilla.javascript;

import android.os.Build;

abstract class Platform {

  private static final SpecifiedPlatform PLATFORM =
      "Dalvik".equals(System.getProperty("java.vm.name")) ? new AndroidPlatform() : new JavaPlatform();

  public static boolean isAndroid() {
    return PLATFORM.isAndroid();
  }

  public static boolean atLeastJava8() {
    return PLATFORM.atLeastJava8();
  }

  private static abstract class SpecifiedPlatform {
    public abstract boolean isAndroid();
    public abstract boolean atLeastJava8();
  }

  private static class JavaPlatform extends SpecifiedPlatform {
    @Override
    public boolean isAndroid() {
      return false;
    }
    @Override
    public boolean atLeastJava8() {
      return true;
    }
  }

  private static class AndroidPlatform extends SpecifiedPlatform {
    @Override
    public boolean isAndroid() {
      return true;
    }
    @Override
    public boolean atLeastJava8() {
      return Build.VERSION.SDK_INT >= 24;
    }
  }
}
