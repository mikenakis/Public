package mikenakis.testana.test.rig2;

import mikenakis.testana.test.rig1.Claire;
import org.junit.Test;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T01_ClaireTest extends T02_AliceTest
{
	@Test
	public void ClaireTest()
	{
		Claire claire = new Claire( " World!" );
		String s = claire.doThatThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}
}
