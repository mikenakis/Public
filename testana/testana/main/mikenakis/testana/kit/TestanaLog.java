package mikenakis.testana.kit;

public final class TestanaLog
{
	private TestanaLog()
	{
	}

	public static void report( String message )
	{
		System.out.println( "Testana: " + message );
	}
}
