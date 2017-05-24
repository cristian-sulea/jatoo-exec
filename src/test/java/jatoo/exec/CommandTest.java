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
 * JUnit tests for {@link Command}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.0, May 23, 2017
 */
public class CommandTest {

  @Before
  public void initialize() throws Throwable {
    new File("target\\CommandTest.java").delete();
    new File("target\\CommandTest.txt").delete();
  }

  @After
  public void cleanup() throws Throwable {
    new File("target\\CommandTest.java").delete();
    new File("target\\CommandTest.txt").delete();
  }

  @Test
  public void test1() throws Throwable {

    if (!Command.OS_IS_WINDOWS) {
      return;
    }

    Command command = new Command("dir");
    command.setDumpOutputStream(System.out);
    command.setFolder(new File("src"));

    Assert.assertEquals(0, command.exec("/S", "/Q"));
    Assert.assertEquals(1, command.exec("/\""));
  }

  @Test
  public void test2() throws Throwable {

    if (!Command.OS_IS_WINDOWS) {
      return;
    }

    Command command = new Command("dir");
    command.setDumpOutputStream(System.out);

    Assert.assertFalse(new File("target\\CommandTest.txt").exists());
    command.exec("/s", "src > target\\CommandTest.txt");
    Assert.assertTrue(new File("target\\CommandTest.txt").exists());
  }

  @Test
  public void test3() throws Throwable {

    if (!Command.OS_IS_WINDOWS) {
      return;
    }

    Command command = new Command("copy");
    command.setDumpOutputStream(System.out);

    Assert.assertFalse(new File("target\\CommandTest.java").exists());
    command.exec("src\\test\\java\\jatoo\\exec\\CommandTest.java", "target");
    Assert.assertTrue(new File("target\\CommandTest.java").exists());
  }

}
