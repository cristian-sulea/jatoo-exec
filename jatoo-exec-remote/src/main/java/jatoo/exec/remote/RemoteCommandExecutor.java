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

package jatoo.exec.remote;

import jatoo.exec.InputStreamExhauster;
import jatoo.exec.InputStreamExhausterWithDumpStream;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Handy class to ease the remote execution of commands.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 1.2 December 10, 2013
 */
public class RemoteCommandExecutor {

  private static final Log LOGGER = LogFactory.getLog(RemoteCommandExecutor.class);

  private Session session;

  public RemoteCommandExecutor() {}

  public void connect(String host, int port, String username, String password) throws JSchException {

    if (isConnected()) {
      disconnect();
    }

    JSch jsch = new JSch();

    session = jsch.getSession(username, host, port);
    session.setUserInfo(new RemoteCommandExecutorJCraftUserInfo(password));
    session.connect();
  }

  public boolean isConnected() {
    return session != null && session.isConnected();
  }

  public void disconnect() {

    if (session != null) {
      session.disconnect();
      session = null;
    } else {
      LOGGER.info("session == null (probably never connected)");
    }
  }

  /**
   * Handy method for {@link #exec(String, OutputStream, boolean)} with no dump
   * output stream.
   */
  public int exec(String command) throws JSchException, IOException {
    return exec(command, null, false);
  }

  /**
   * Handy method for {@link #exec(String, OutputStream, boolean)} with
   * specified dump output stream (but no closing).
   */
  public int exec(String command, OutputStream dumpOutputStream) throws JSchException, IOException {
    return exec(command, dumpOutputStream, false);
  }

  /**
   * Executes the specified command.
   * 
   * @param command
   * @param dumpOutputStream
   * @param closeDumpOutputStream
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination | take care that
   *         {@link JSch} is also returning <code>-1</code>)
   * 
   * @throws IOException
   * @throws JSchException
   */
  public int exec(String command, OutputStream dumpOutputStream, boolean closeDumpOutputStream) throws IOException, JSchException {
    return exec(command, dumpOutputStream, closeDumpOutputStream, null);
  }

  /**
   * Executes the specified command.
   * 
   * @param command
   * @param dumpOutputStream
   * @param closeDumpOutputStream
   * @param callback
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination | take care that
   *         {@link JSch} is also returning <code>-1</code>)
   * 
   * @throws IOException
   * @throws JSchException
   */
  public int exec(String command, OutputStream dumpOutputStream, boolean closeDumpOutputStream, RemoteCommandExecutorCallback callback) throws IOException, JSchException {

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);

    if (callback != null) {
      callback.setChannel(channel);
    }

    channel.setInputStream(null);
    channel.setErrStream(dumpOutputStream, true);

    channel.connect();

    if (dumpOutputStream != null) {
      new InputStreamExhausterWithDumpStream(channel.getInputStream(), dumpOutputStream, closeDumpOutputStream).exhaust();
    } else {
      new InputStreamExhauster(channel.getInputStream()).exhaust();
    }

    channel.disconnect();

    return channel.getExitStatus();
  }

}
