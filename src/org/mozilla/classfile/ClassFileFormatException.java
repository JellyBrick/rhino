package org.mozilla.classfile;

/**
 * Thrown for cases where the error in generating the class file is due to a program size
 * constraints rather than a likely bug in the compiler.
 */
public class ClassFileFormatException extends RuntimeException {

  private static final long serialVersionUID = 1263998431033790599L;

  ClassFileFormatException(String message) {
    super(message);
  }
}
