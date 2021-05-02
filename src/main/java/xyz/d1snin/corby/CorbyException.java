/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby;

public class CorbyException extends RuntimeException {
  public CorbyException(String msg, int exitCode) {
    super(msg);
    System.exit(exitCode);
  }
}
