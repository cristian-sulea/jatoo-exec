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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link CommandExecutor}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.0, May 23, 2017
 */
public class CommandExecutorTest {

  private final CommandExecutor executor = new CommandExecutor();

  @Before
  public void initialize() throws Throwable {
    new File("target\\CommandExecutorTest.java").delete();
    new File("target\\CommandExecutorTest.txt").delete();
  }

  @After
  public void cleanup() throws Throwable {
    new File("target\\CommandExecutorTest.java").delete();
    new File("target\\CommandExecutorTest.txt").delete();
  }

  @Test
  public void test1() throws Throwable {

    if (!executor.isWindows()) {
      return;
    }

    Assert.assertFalse(new File("target\\CommandExecutorTest.txt").exists());
    executor.exec("dir /s src > target\\CommandExecutorTest.txt", System.out);
    Assert.assertTrue(new File("target\\CommandExecutorTest.txt").exists());
  }

  @Test
  public void test2() throws Throwable {

    if (!executor.isWindows()) {
      return;
    }

    Assert.assertFalse(new File("target\\CommandExecutorTest.java").exists());
    executor.exec("copy src\\test\\java\\jatoo\\exec\\CommandExecutorTest.java target", System.out);
    Assert.assertTrue(new File("target\\CommandExecutorTest.java").exists());

    executor.exec("mvn", new File("target"), System.out);
  }

}
