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
	public static String foxtrotName = T01_Test.class.getSimpleName() + "." + foxtrotShortName;
	public static String echoName = T01_Test.class.getSimpleName() + "." + echoShortName;

	public static String alphaShortName = "alpha()";
	public static String alphaName = T01_Test.class.getSimpleName() + "." + alphaShortName;
	@Test public void alpha()
	{
	}

	public static String charlieShortName = "charlie()";
	public static String charlieName = T01_Test.class.getSimpleName() + "." + charlieShortName;
	@Test public void charlie()
	{
		Claire claire = new Claire( " World!" );
		String s = claire.doThatThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}

	public static String bravoShortName = "bravo()";
	public static String bravoName = T01_Test.class.getSimpleName() + "." + bravoShortName;
	@Test public void bravo()
	{
	}
}
