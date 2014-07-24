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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link CommandExecutor}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.6, July 24, 2014
 */
public class CommandExecutorTest {

  private static final boolean DUMP_TO_SYSTEM_OUT = true;

  private static final File DIRECTORY_TESTS = new File(new File("target"), "tests");
  private static final File DIRECTORY_TESTS_TARGET = new File(DIRECTORY_TESTS, "target");

  private final CommandExecutor commandExecutor = new CommandExecutor();

  @Before
  public void initialize() throws Exception {

    FileUtils.deleteDirectory(DIRECTORY_TESTS);
    Assert.assertFalse(DIRECTORY_TESTS.exists());

    DIRECTORY_TESTS.mkdirs();
    Assert.assertTrue(DIRECTORY_TESTS.exists());

    FileUtils.copyFileToDirectory(new File("src/test/resources/pom.xml"), DIRECTORY_TESTS);
  }

  @After
  public void cleanup() throws Exception {}

  @Test
  public void test() throws Exception {

    DIRECTORY_TESTS_TARGET.mkdirs();
    Assert.assertTrue(DIRECTORY_TESTS_TARGET.exists());

    commandExecutor.exec("mvn clean", DIRECTORY_TESTS, DUMP_TO_SYSTEM_OUT ? System.out : null);

    Assert.assertFalse(DIRECTORY_TESTS_TARGET.exists());
  }

}
