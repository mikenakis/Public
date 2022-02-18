package mikenakis.testana.test.rigs.rig2;

import mikenakis.testana.test.rigs.classes_under_test.Claire;
import org.junit.Test;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T01 extends T02
{
	public static String testMethodName = T01.class.getSimpleName() + ".ClaireTest()";

	@Test public void ClaireTest()
	{
		Claire claire = new Claire( " World!" );
		String s = claire.doThatThing( "Hello," );
		assert s.equals( "Hello, World!" );
	}
}