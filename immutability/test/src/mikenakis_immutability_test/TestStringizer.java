package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;

public final class TestStringizer implements Stringizer
{
	private static final String testPackageName = TestStringizer.class.getPackageName();
	private static final String immutabilityPackageName = Assessment.class.getPackageName();

	public static Stringizer instance = new TestStringizer();

	private TestStringizer()
	{
	}

	@Override public String stringize( Object object )
	{
		if( object == null )
			return "null";
		assert !(object instanceof Class<?>);
		int identityHashCode = System.identityHashCode( object );
		return stringize( object.getClass() ) + "@" + Integer.toHexString( identityHashCode );
	}

	@Override public String stringize( Class<?> jvmClass )
	{
		String simpleName = jvmClass.getSimpleName();
		String packageName = jvmClass.getPackageName();
		if( packageName.startsWith( "java." ) || packageName.startsWith( "javax." ) )
			return "java:" + simpleName;
		if( packageName.startsWith( testPackageName ) )
			return "test:" + simpleName;
		if( packageName.startsWith( immutabilityPackageName ) )
			return "immutability:" + simpleName;
		assert false;
		return simpleName;
	}
}
