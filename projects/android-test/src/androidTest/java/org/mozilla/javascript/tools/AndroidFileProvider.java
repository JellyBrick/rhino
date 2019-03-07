package org.mozilla.javascript.tools;

import android.util.Log;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class AndroidFileProvider extends FileProvider {

  private static final String LOG_TAG = "AndroidTestResources";

  private static final File TEMP_DIR = new File(InstrumentationRegistry.getInstrumentation().getContext().getCacheDir(), "temp");
  private static final File ASSETS_DIR = new File(InstrumentationRegistry.getInstrumentation().getContext().getFilesDir(), "testassets");
  private static final File ASSETS_NAME = new File(InstrumentationRegistry.getInstrumentation().getContext().getFilesDir(), "testassets.name");

  private final String[] XML_TEST_FILES = {
      "doctests/441417.doctest",
      "doctests/442922.doctest",
      "doctests/473761.doctest",
      "doctests/477233.doctest",
      "doctests/524931.doctest",
      "doctests/xmlOptions.doctest",
      "tests/e4x",
      "tests/js1_5/Regress/regress-407323.js",
      "tests/js1_6/Regress/regress-301574.js",
      "tests/js1_6/Regress/regress-314887.js",
      "tests/js1_6/Regress/regress-378492.js",
      "tests/js1_7/iterable/regress-355075-02.js",
      "tests/js1_7/regress/regress-352797-02.js",
      "tests/js1_7/regress/regress-416705.js",
      "tests/js1_7/regress/regress-428708.js",
      "tests/js1_8/regress/regress-465460-07.js",
      "tests/js1_8/regress/regress-471660.js",
      "tests/js1_8/regress/regress-479353.js",
  };

  private final String[] CODEGEN_TEST_FILES = {
      "doctests/javaadapter.doctest",
  };

  private final String[] OTHER_UNSUPPORTED_TEST_FILES = {
      // Bad file path
      "doctests/serialize.doctest",
      // 0x180E is a white space or not
      "doctests/string.trim.doctest",
      // Classpath doesn't include app classes
      "tests/lc2/Methods/method-001.js",
      "tests/lc2/Methods/method-002.js",
      "tests/lc2/Objects/object-001.js",
      "tests/lc2/Objects/object-002.js",
      "tests/lc2/misc/constructor.js",
  };

  private final String[][] UNSUPPORTED_TEST_FILES_SET = {
      XML_TEST_FILES,
      CODEGEN_TEST_FILES,
      OTHER_UNSUPPORTED_TEST_FILES,
  };

  private boolean assetsChecked = false;

  public AndroidFileProvider() {
    try {
      FileUtils.forceMkdir(TEMP_DIR);
      FileUtils.cleanDirectory(TEMP_DIR);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
  }

  private static String fixFilename(String path) {
    if (path.startsWith("testsrc")) {
      path = path.substring("testsrc/".length());
    } else if (path.startsWith("test262")) {
      path = path.substring("test262/".length());
    }
    return path;
  }

  private void ensureAssetFiles() throws IOException, ZipException {
    String except;
    try {
      except = FileUtils.readFileToString(ASSETS_NAME, "UTF-8");
    } catch (IOException e) {
      except = null;
    }

    String actual = null;
    String[] assets = InstrumentationRegistry.getInstrumentation().getContext().getAssets().list("");
    for (String asset : assets) {
      if (asset.startsWith("testassets-") && asset.endsWith(".crc32")) {
        actual = asset;
      }
    }
    if (actual == null) {
      throw new RuntimeException("Can't find test asset. Here is all assets: " + Arrays.toString(assets));
    }

    if (!equals(except, actual)) {
      Log.d(LOG_TAG, "except = " + except + ", actual = " + actual);
      Log.d(LOG_TAG, "Copy test assets");

      FileUtils.forceMkdir(ASSETS_DIR);
      FileUtils.cleanDirectory(ASSETS_DIR);

      try (InputStream in = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open("testassets.zip");
          OutputStream out = new FileOutputStream(new File(TEMP_DIR, "testassets.zip")) ) {
        IOUtils.copy(in, out);
      }

      ZipFile zipFile = new ZipFile(new File(TEMP_DIR, "testassets.zip"));
      zipFile.extractAll(ASSETS_DIR.getPath());

      FileUtils.writeStringToFile(ASSETS_NAME, actual, "UTF-8");

      Log.d(LOG_TAG, "All test assets are copied");
    } else {
      Log.d(LOG_TAG, "Test assets UP-TO-DATE");
    }

    // Delete unsupported tests
    for (String[] files : UNSUPPORTED_TEST_FILES_SET) {
      for (String file : files) {
        FileUtils.deleteQuietly(new File(ASSETS_DIR, file));
      }
    }
  }

  @Override
  public File getFile(String path) {
    try {
      if (!assetsChecked) {
        synchronized (this) {
          if (!assetsChecked) {
            assetsChecked = true;
            ensureAssetFiles();
          }
        }
      }
    } catch (IOException | ZipException e) {
      throw new RuntimeException(e);
    }

    if (path.isEmpty()) {
      return ASSETS_DIR;
    }
    path = fixFilename(path);

    if (path.startsWith("/")) {
      return new File(path);
    } else {
      return new File(ASSETS_DIR, path);
    }
  }
}
