package xyz.d1snin.corby;

public class CorbyException extends RuntimeException {
  public CorbyException(String msg, int exitCode) {
    super(msg);
    System.exit(exitCode);
  }
}
