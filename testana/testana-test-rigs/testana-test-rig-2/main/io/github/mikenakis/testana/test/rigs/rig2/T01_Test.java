package io.github.mikenakis.testana.test.rigs.rig2;

import io.github.mikenakis.testana.test.rigs.classes_under_test.Claire;
import org.junit.Test;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T01_Test extends T02_Test
{
	public static String testMethodName = T01_Test.class.getSimpleName() + ".ClaireTest()";

	@Test public void ClaireTest()
	{
		Claire claire = new Claire( " World!" );
		String s = claire.doThatThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}
}
