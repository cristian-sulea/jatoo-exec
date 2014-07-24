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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that will exhaust a provided {@link InputStream}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.4, July 24, 2014
 */
public class InputStreamExhauster implements Runnable {

  private static final Log LOGGER = LogFactory.getLog(InputStreamExhauster.class);

  private final InputStream processInputStream;

  public InputStreamExhauster(InputStream processInputStream) {
    this.processInputStream = processInputStream;
  }

  public void exhaust() {
    try {
      while (processInputStream.read() != -1) {}
    } catch (IOException e) {
      LOGGER.error("error exhausting the stream", e);
    }
  }

  @Override
  public void run() {
    exhaust();
  }

}
