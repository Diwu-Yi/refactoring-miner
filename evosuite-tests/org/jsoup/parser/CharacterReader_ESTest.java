/*
 * This file was automatically generated by EvoSuite
 * Tue Mar 07 13:30:43 GMT 2023
 */

package org.jsoup.parser;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import java.io.StringReader;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.jsoup.parser.CharacterReader;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class CharacterReader_ESTest extends CharacterReader_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      StringReader stringReader0 = new StringReader("Array must not contain any null objects");
      CharacterReader characterReader0 = new CharacterReader(stringReader0);
      // EXCEPTION DIFF:
      // The modified version did not exhibit this exception:
      //     org.jsoup.UncheckedIOException : org.evosuite.runtime.mock.java.lang.MockThrowable: Mark invalid
      // Undeclared exception!
      try { 
        characterReader0.rewindToMark();
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // org.evosuite.runtime.mock.java.lang.MockThrowable: Mark invalid
         //
         verifyException("org.jsoup.parser.CharacterReader", e);
         assertTrue(e.getMessage().equals("org.evosuite.runtime.mock.java.lang.MockThrowable: Mark invalid"));   
      }
  }
}