package io.github.mikenakis.testana.test.rigs.rig2;

import io.github.mikenakis.testana.test.rigs.classes_under_test.Alice;
import org.junit.Test;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T02_Test
{
	public static String foxtrotShortName = "foxtrot()";
	public static String foxtrotName = T02_Test.class.getSimpleName() + "." + foxtrotShortName;
	@Test public void foxtrot()
	{
		Alice claire = new Alice( " World!" );
		String s = claire.doYourThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}

	public static String echoShortName = "echo()";
	public static String echoName = T02_Test.class.getSimpleName() + "." + echoShortName;
	@Test public void echo()
	{
	}
}
