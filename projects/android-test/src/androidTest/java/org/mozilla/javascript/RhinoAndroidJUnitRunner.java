package org.mozilla.javascript;

import androidx.test.runner.AndroidJUnitRunner;
import java.util.Locale;
import java.util.TimeZone;

public class RhinoAndroidJUnitRunner extends AndroidJUnitRunner {

  @Override
  public void onStart() {
    System.setProperty("mozilla.js.tests", "testsrc/tests");
    System.setProperty("mozilla.js.tests.timeout", "180000");
    System.setProperty("user.language", "en");
    System.setProperty("user.country", "US");
    System.setProperty("user.timezone", "America/Los_Angeles");
    System.setProperty("file.encoding", "UTF-8");

    TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    Locale.setDefault(Locale.US);

    super.onStart();
  }
}
