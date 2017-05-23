/*
 * Copyright (C) 2014 Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.exec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handy class to ease the execution of operating system commands.
 * 
 * TODO: WARNING: not tested on Linux
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.5, July 25, 2014
 */
public class CommandExecutor {

  private final List<String> commandPrefix;

  public CommandExecutor() {

    String osName = System.getProperty("os.name");

    //
    // Linux systems (WARNING: not tested)

    if (osName.equals("Linux")) {
      commandPrefix = new ArrayList<String>(0);
    }

    //
    // old Windows systems

    else if (osName.equals("Windows 95") || osName.equals("Windows 98") || osName.equalsIgnoreCase("Windows ME")) {
      commandPrefix = Arrays.asList("command.com", "/C");
    }

    //
    // modern (others) Windows systems

    else {
      commandPrefix = Arrays.asList("cmd.exe", "/C");
    }
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} running
   * in working folder of JVM with no dump output stream.
   * 
   * @param command
   *          the command to be executed
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt()
   *           interrupted} by another thread while it is waiting
   */
  public final int exec(final String command) throws IOException, InterruptedException {
    return exec(command, null, null, false);
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} with no
   * dump output stream.
   * 
   * @param command
   *          the command to be executed
   * @param folder
   *          the working folder
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt()
   *           interrupted} by another thread while it is waiting
   */
  public final int exec(final String command, final File folder) throws IOException, InterruptedException {
    return exec(command, folder, null, false);
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} running
   * in working folder of JVM with specified dump output stream (but no
   * closing).
   * 
   * @param command
   *          the command to be executed
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt()
   *           interrupted} by another thread while it is waiting
   */
  public final int exec(final String command, final OutputStream dumpOutputStream) throws IOException, InterruptedException {
    return exec(command, null, dumpOutputStream, false);
  }

  /**
   * Handy method for {@link #exec(String, File, OutputStream, boolean)} with
   * specified dump output stream (but no closing).
   * 
   * @param command
   *          the command to be executed
   * @param folder
   *          the working folder
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt()
   *           interrupted} by another thread while it is waiting
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
   *          <code>true</code> if the dump stream should be closed when the
   *          execution ends, <code>false</code> otherwise
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws InterruptedException
   *           if the current thread is {@linkplain Thread#interrupt()
   *           interrupted} by another thread while it is waiting
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
