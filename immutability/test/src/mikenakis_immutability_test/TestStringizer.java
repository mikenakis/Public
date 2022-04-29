package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.MyKit;

public final class TestStringizer extends Stringizer
{
	private static final String testPackageName = TestStringizer.class.getPackageName();
	private static final String immutabilityPackageName = Assessment.class.getPackageName();

	public static Stringizer instance = new TestStringizer();

	private TestStringizer()
	{
	}

	@Override public String stringizeObjectIdentity( Object object )
	{
		if( object == null )
			return "null";
		assert !(object instanceof Class<?>);
		return testIdentityString( object );
	}

	private String testIdentityString( Object object )
	{
		if( object == null )
			return "null";
		int identityHashCode = System.identityHashCode( object );
		return stringizeClassName0( object.getClass() ) + "@" + Integer.toHexString( identityHashCode );
	}

	@Override public String stringizeClassName0( Class<?> jvmClass )
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
