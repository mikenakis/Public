package test.mikenakis_rumination_test.kit;

/**
 * Indispensable utility methods.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TestKit
{
	private TestKit()
	{
	}

	public static String identityString( Object object )
	{
		if( object == null )
			return "null";
		int hashCode = System.identityHashCode( object );  //note: Object.toString() is different from System.identityHashCode() !
		return object.getClass().getName() + "@" + Integer.toHexString( hashCode );
	}
}
