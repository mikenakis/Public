package mikenakis_kit_test;

import mikenakis.kit.Kit;
import org.junit.Test;

/**
 * Test.
 */
public class T020_String
{
	public T020_String()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void ReplaceAll_Works()
	{
		String s = "a---b";
		String r = Kit.string.replaceAll( s, "--", "-" );
		assert r.equals( "a-b" );
	}
}
