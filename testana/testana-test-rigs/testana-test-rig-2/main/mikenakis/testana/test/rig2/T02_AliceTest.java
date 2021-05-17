package mikenakis.testana.test.rig2;

import mikenakis.testana.test.rig1.Alice;
import org.junit.Test;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T02_AliceTest
{
	@Test
	public void ZAliceTest()
	{
		Alice claire = new Alice( " World!" );
		String s = claire.doYourThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}
}
