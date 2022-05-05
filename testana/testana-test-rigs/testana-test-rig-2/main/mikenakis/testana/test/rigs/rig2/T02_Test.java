package mikenakis.testana.test.rigs.rig2;

import mikenakis.testana.test.rigs.classes_under_test.Alice;
import org.junit.Test;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T02_Test
{
	public static String testMethodName = T02_Test.class.getSimpleName() + ".ZAliceTest()";

	@Test public void ZAliceTest()
	{
		Alice claire = new Alice( " World!" );
		String s = claire.doYourThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}
}
