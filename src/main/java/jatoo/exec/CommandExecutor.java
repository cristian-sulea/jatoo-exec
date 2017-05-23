/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jatoo.exec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Handy class to ease the execution of operating system commands.
 * 
 * <strong>WARNING</strong>: not tested on Linux
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.6, May 23, 2017
 */
public class CommandExecutor {

  /** The command prefix (OS dependent). */
  private final List<String> commandPrefix;

  /** The OS is a Windows version. */
  private boolean isWindows = false;

  /**
   * Constructs a new command executor, trying to detect the OS.
   */
  public CommandExecutor() {

    String osName = String.valueOf(System.getProperty("os.name")).toLowerCase(Locale.ENGLISH);

    if (osName.contains("windows")) {

      isWindows = true;

      //
      // old Windows systems

      if (osName.equals("Windows 95") || osName.equals("Windows 98") || osName.equalsIgnoreCase("Windows ME")) {
        commandPrefix = Arrays.asList("command.com", "/C");
      }

      //
      // modern (others) Windows systems

      else {
        commandPrefix = Arrays.asList("cmd.exe", "/C");
      }
    }

    else {

      //
      // other systems

      commandPrefix = new ArrayList<String>(0);
    }
  }

  /**
   * Returns <code>true</code> if the OS is a Windows version;
   * 
   * @return <code>true</code> if the OS is a Windows version, <code>false</code> otherwise
   */
  public boolean isWindows() {
    return isWindows;
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} running in working folder of JVM with no dump
   * output stream.
   * 
   * @param command
   *          the command to be executed
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public final int exec(final String command) throws IOException, InterruptedException {
    return exec(command, null, null);
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} with no dump output stream.
   * 
   * @param command
   *          the command to be executed
   * @param folder
   *          the working folder
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public final int exec(final String command, final File folder) throws IOException, InterruptedException {
    return exec(command, folder, null);
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} running in working folder of JVM with specified
   * dump output stream (but no closing).
   * 
   * @param command
   *          the command to be executed
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public final int exec(final String command, final OutputStream dumpOutputStream) throws IOException, InterruptedException {
    return exec(command, null, dumpOutputStream);
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} with specified dump output stream (but no
   * closing).
   * 
   * @param command
   *          the command to be executed
   * @param folder
   *          the working folder
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public final int exec(final String command, final File folder, final OutputStream dumpOutputStream) throws IOException, InterruptedException {
    return exec(command, folder, dumpOutputStream, false);
  }

  /**
   * Executes the specified command.
   * 
   * @param command
   *          the command to be executed
   * @param folder
   *          the working folder
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * @param closeDumpOutputStream
   *          <code>true</code> if the dump stream should be closed when the execution ends, <code>false</code>
   *          otherwise
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public final int exec(final String command, final File folder, final OutputStream dumpOutputStream, final boolean closeDumpOutputStream) throws IOException, InterruptedException {

    //
    // add the prefix to the command

    List<String> commandList = new ArrayList<String>(commandPrefix.size() + 1);
    commandList.addAll(commandPrefix);
    commandList.add(command);

    //
    // create and start the process

    Process process = new ProcessBuilder(commandList).directory(folder).start();

    //
    // exhaust both the standard error stream and the standard output stream

    if (dumpOutputStream != null) {
      new Thread(new InputStreamExhausterWithDumpStream(process.getInputStream(), dumpOutputStream, closeDumpOutputStream)).start();
      new Thread(new InputStreamExhausterWithDumpStream(process.getErrorStream(), dumpOutputStream, closeDumpOutputStream)).start();
    } else {
      new Thread(new InputStreamExhauster(process.getInputStream())).start();
      new Thread(new InputStreamExhauster(process.getErrorStream())).start();
    }

    //
    // wait until the process has terminated

    return process.waitFor();
  }

}
