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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that will exhaust a provided {@link InputStream}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.5, May 23, 2017
 */
public class InputStreamExhauster implements Runnable {

  /** The logger. */
  private static final Log logger = LogFactory.getLog(InputStreamExhauster.class);

  /** The stream to be exhausted. */
  private final InputStream processInputStream;

  /**
   * Constructs a new stream exhauster.
   * 
   * @param processInputStream
   *          the stream to be exhausted
   */
  public InputStreamExhauster(final InputStream processInputStream) {
    this.processInputStream = processInputStream;
  }

  @Override
  public final void run() {
    exhaust();
  }

  /**
   * Exhaust the provided input stream, reading until EOF has been encountered.
   */
  private void exhaust() {

    final byte[] buffer = new byte[1024];

    try {
      // CHECKSTYLE:OFF
      while (processInputStream.read(buffer) >= 0) {} // NOPMD
      // CHECKSTYLE:ON
    }

    catch (IOException e) {
      logger.error("error exhausting the stream", e);
    }
  }

}
