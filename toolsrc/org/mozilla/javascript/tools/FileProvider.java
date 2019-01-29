package org.mozilla.javascript.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public abstract class FileProvider {

  private static FileProvider instance;

  static {
    try {
      instance = (FileProvider) Class.forName("org.mozilla.javascript.tools.AndroidFileProvider").newInstance();
    } catch (Throwable e) {
      instance = new JvmFileProvider();
    }
  }

  public static FileProvider getInstance() {
    return instance;
  }

  public abstract File getFile(String path);

  public FileInputStream getInputStream(String path) throws IOException {
    return new FileInputStream(getFile(path));
  }

  public FileOutputStream getOutputStream(String path) throws IOException {
    return new FileOutputStream(getFile(path));
  }

  public FileReader getReader(String path) throws IOException {
    return new FileReader(getFile(path));
  }

  private static class JvmFileProvider extends FileProvider {
    @Override
    public File getFile(String path) {
      return new File(path);
    }
  }
}
