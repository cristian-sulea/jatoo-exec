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
 * @version 1.0-SNAPSHOT, May 25, 2017
 */
public class Command {

  /** This is a Windows system. */
  public static final boolean OS_IS_WINDOWS;

  /** The command prefix (OS dependent). */
  private static final List<String> PREFIX;

  static {

    String osName = String.valueOf(System.getProperty("os.name")).toLowerCase(Locale.ENGLISH);

    //
    // OS: Windows

    if (osName.contains("windows")) {

      OS_IS_WINDOWS = true;

      //
      // old Windows systems

      if (osName.equals("Windows 95") || osName.equals("Windows 98") || osName.equalsIgnoreCase("Windows ME")) {
        PREFIX = Arrays.asList("command.com", "/C");
      }

      //
      // modern (others) Windows systems

      else {
        PREFIX = Arrays.asList("cmd.exe", "/C");
      }
    }

    //
    // OS: other systems

    else {

      OS_IS_WINDOWS = false;

      PREFIX = new ArrayList<String>(0);
    }
  }

  /** The program to be executed. */
  private final String program;

  /** The working folder (directory). */
  private File folder = null;

  /**
   * Constructs a new command for the specified program.
   * 
   * @param program
   *          the program to be executed
   */
  public Command(final String program) {
    this.program = program;
  }

  public final String getProgram() {
    return program;
  }

  public final void setFolder(final File folder) {
    this.folder = folder;
  }

  public final File getFolder() {
    return folder;
  }

  /**
   * Executes this command with the specified arguments.
   * 
   * @param arguments
   *          the arguments to be used for this execution
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public int exec(final String... arguments) throws IOException, InterruptedException {
    return exec(null, arguments);
  }

  /**
   * Executes this command with the specified arguments.
   * 
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * @param arguments
   *          the arguments to be used for this execution
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public int exec(final OutputStream dumpOutputStream, final String... arguments) throws IOException, InterruptedException {
    return exec(dumpOutputStream, false, arguments);
  }

  /**
   * Executes this command with the specified arguments.
   * 
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * @param closeDumpOutputStream
   *          <code>true</code> if the dump stream should be closed when the execution ends, <code>false</code>
   *          otherwise
   * @param arguments
   *          the arguments to be used for this execution
   * 
   * @return the exit value of the process (by convention, the value <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is
   *           waiting
   */
  public int exec(final OutputStream dumpOutputStream, final boolean closeDumpOutputStream, final String... arguments) throws IOException, InterruptedException {

    //
    // complete the command

    StringBuilder command = new StringBuilder();
    command.append(program);
    for (int i = 0; i < arguments.length; i++) {
      command.append(' ').append(arguments[i]);
    }

    List<String> processCommand = new ArrayList<String>(PREFIX.size() + 1);
    processCommand.addAll(PREFIX);
    processCommand.add(command.toString());

    //
    // create and start the process

    ProcessBuilder processBuilder = new ProcessBuilder(processCommand);

    if (folder != null) {
      processBuilder.directory(folder);
    }

    Process process = processBuilder.start();

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
