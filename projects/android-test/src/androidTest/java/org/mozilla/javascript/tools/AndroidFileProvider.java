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

  private final File tempDir = new File(InstrumentationRegistry.getInstrumentation().getContext().getCacheDir(), "temp");
  private final File assetsDir = new File(InstrumentationRegistry.getInstrumentation().getContext().getFilesDir(), "testassets");
  private final File assetsName = new File(InstrumentationRegistry.getInstrumentation().getContext().getFilesDir(), "testassets.name");

  private boolean assetsChecked = false;

  public AndroidFileProvider() {
    try {
      FileUtils.forceMkdir(tempDir);
      FileUtils.cleanDirectory(tempDir);
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
      except = FileUtils.readFileToString(assetsName, "UTF-8");
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

      FileUtils.forceMkdir(assetsDir);
      FileUtils.cleanDirectory(assetsDir);

      try (InputStream in = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open("testassets.zip");
          OutputStream out = new FileOutputStream(new File(tempDir, "testassets.zip")) ) {
        IOUtils.copy(in, out);
      }

      ZipFile zipFile = new ZipFile(new File(tempDir, "testassets.zip"));
      zipFile.extractAll(assetsDir.getPath());

      FileUtils.writeStringToFile(assetsName, actual, "UTF-8");

      Log.d(LOG_TAG, "All test assets are copied");
    } else {
      Log.d(LOG_TAG, "Test assets UP-TO-DATE");
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
      return assetsDir;
    }
    path = fixFilename(path);

    if (path.startsWith("/")) {
      return new File(path);
    } else {
      return new File(assetsDir, path);
    }
  }
}
