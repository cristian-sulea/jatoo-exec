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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that will exhaust a provided {@link InputStream} but dumping the contents to a specified stream.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.6, July 25, 2014
 */
public class InputStreamExhausterWithDumpStream implements Runnable {

  /** The logger. */
  private static final Log logger = LogFactory.getLog(InputStreamExhausterWithDumpStream.class);

  /** The stream to be exhausted. */
  private final InputStream processInputStream;

  /** The stream to collect the dumped content. */
  private final OutputStream dumpOutputStream;

  /** If <code>true</code> then the dump stream should be closed at the end. */
  private final boolean closeDumpOutputStream;

  /**
   * Constructs a new stream exhauster.
   * 
   * @param processInputStream
   *          the stream to be exhausted
   * @param dumpOutputStream
   *          the stream to collect the dumped content
   * @param closeDumpOutputStream
   *          <code>true</code> if the dump stream should be closed at the end, <code>false</code> otherwise
   */
  public InputStreamExhausterWithDumpStream(final InputStream processInputStream, final OutputStream dumpOutputStream, final boolean closeDumpOutputStream) {
    this.processInputStream = processInputStream;
    this.dumpOutputStream = dumpOutputStream;
    this.closeDumpOutputStream = closeDumpOutputStream;
  }

  @Override
  public final void run() {
    exhaust();
  }

  /**
   * Exhaust the provided input stream, dumping the contents to the specified stream.
   */
  private void exhaust() {

    BufferedReader processInputStreamReader = null;
    BufferedWriter dumpOutputStreamWriter = null;

    try {

      processInputStreamReader = new BufferedReader(new InputStreamReader(processInputStream, Charset.defaultCharset()));
      dumpOutputStreamWriter = new BufferedWriter(new OutputStreamWriter(dumpOutputStream, Charset.defaultCharset()));

      String line = null;
      while ((line = processInputStreamReader.readLine()) != null) {

        dumpOutputStreamWriter.write(line);
        dumpOutputStreamWriter.newLine();

        dumpOutputStreamWriter.flush();
      }
    }

    catch (IOException e) {
      logger.error("error exhausting the stream", e);
    }

    finally {
      if (closeDumpOutputStream && dumpOutputStreamWriter != null) {
        try {
          dumpOutputStreamWriter.close();
        } catch (IOException e) {
          logger.error("error closing the dump stream", e);
        }
      }
    }
  }

}
