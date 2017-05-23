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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that will exhaust a provided {@link InputStream} but dumping the
 * contents to a specified stream..
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.6, July 25, 2014
 */
public class InputStreamExhausterWithDumpStream implements Runnable {

  private static final Log LOGGER = LogFactory.getLog(InputStreamExhausterWithDumpStream.class);

  private final InputStream processInputStream;
  private final OutputStream dumpOutputStream;
  private final boolean closeDumpOutputStream;

  public InputStreamExhausterWithDumpStream(final InputStream processInputStream, final OutputStream dumpOutputStream, final boolean closeDumpOutputStream) {
    this.processInputStream = processInputStream;
    this.dumpOutputStream = dumpOutputStream;
    this.closeDumpOutputStream = closeDumpOutputStream;
  }

  public final void exhaust() {

    BufferedReader processInputStreamReader = null;
    BufferedWriter dumpOutputStreamWriter = null;

    try {

      processInputStreamReader = new BufferedReader(new InputStreamReader(processInputStream));
      dumpOutputStreamWriter = new BufferedWriter(new OutputStreamWriter(dumpOutputStream));

      String line = null;
      while ((line = processInputStreamReader.readLine()) != null) {

        dumpOutputStreamWriter.write(line);
        dumpOutputStreamWriter.newLine();

        dumpOutputStreamWriter.flush();
      }
    }

    catch (IOException e) {
      LOGGER.error("error exhausting the stream", e);
    }

    finally {
      if (closeDumpOutputStream && dumpOutputStreamWriter != null) {
        try {
          dumpOutputStreamWriter.close();
        } catch (IOException e) {
          LOGGER.error("error closing the dump stream", e);
        }
      }
    }
  }

  @Override
  public final void run() {
    exhaust();
  }

}
