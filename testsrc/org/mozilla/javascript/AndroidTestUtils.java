package org.mozilla.javascript;

import android.support.test.InstrumentationRegistry;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.rhino.Objects;

public class AndroidTestUtils {

    private static final String LOG_TAG = "AndroidTestUtils";

    private static final File tempDir = new File(InstrumentationRegistry.getContext().getCacheDir(), "temp");

    private static final File assetsDir = new File(InstrumentationRegistry.getContext().getFilesDir(), "testassets");
    private static final File assetsName = new File(InstrumentationRegistry.getContext().getFilesDir(), "testassets.name");
    private static boolean assetsChecked = false;

    static {
        try {
            FileUtils.forceMkdir(tempDir);
            FileUtils.cleanDirectory(tempDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void decompressAssets() {
        try {
            try (InputStream in = InstrumentationRegistry.getContext().getAssets().open("testassets.zip");
                    OutputStream out = new FileOutputStream(new File(tempDir, "testassets.zip")) ) {
                IOUtils.copy(in, out);
            }

            ZipFile zipFile = new ZipFile(new File(tempDir, "testassets.zip"));
            zipFile.extractAll(assetsDir.getPath());
        } catch (ZipException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Context enterContext() {
        return new AndroidTestContextFactory().enterContext();
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static File tempFile(String filename) {
        return new File(tempDir, filename);
    }

    public static FileOutputStream tempFileOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(tempDir, filename));
    }

    public static FileInputStream tempFileInputStream(String filename) throws FileNotFoundException {
        return new FileInputStream(new File(tempDir, filename));
    }

    private static String fixFilename(String path) {
        if (path.startsWith("testsrc")) {
            path = path.substring("testsrc/".length());
        } else if (path.startsWith("test262")) {
            path = path.substring("test262/".length());
        }
        return path;
    }

    private static void ensureAssetFiles() throws IOException, ZipException {
        String except;
        try {
            except = FileUtils.readFileToString(assetsName, "UTF-8");
        } catch (IOException e) {
            except = null;
        }

        String actual = null;
        String[] assets = InstrumentationRegistry.getContext().getAssets().list("");
        for (String asset : assets) {
            if (asset.startsWith("testassets-") && asset.endsWith(".zip")) {
                actual = asset;
            }
        }
        if (actual == null) {
            throw new RuntimeException("Can't find test asset. Here is all assets: " + Arrays.toString(assets));
        }

        if (!Objects.equals(except, actual)) {
            Log.d(LOG_TAG, "Copy test assets");

            FileUtils.forceMkdir(assetsDir);
            FileUtils.cleanDirectory(assetsDir);

            try (InputStream in = InstrumentationRegistry.getContext().getAssets().open(actual);
                    OutputStream out = new FileOutputStream(new File(tempDir, "testassets.zip")) ) {
                IOUtils.copy(in, out);
            }

            ZipFile zipFile = new ZipFile(new File(tempDir, "testassets.zip"));
            zipFile.extractAll(assetsDir.getPath());

            FileUtils.writeStringToFile(assetsName, actual, "UTF-8");
        } else {
            Log.d(LOG_TAG, "Test assets UP-TO-DATE");
        }
    }

    public static File assetFile(String path) {
        try {
            if (!assetsChecked) {
                synchronized (AndroidTestUtils.class) {
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
        return new File(assetsDir, path);
    }

    public static InputStream assetStream(String path) throws FileNotFoundException {
        return new FileInputStream(assetFile(path));
    }

    public static Reader assetReader(String path) throws FileNotFoundException {
        return new FileReader(assetFile(path));
    }
}
