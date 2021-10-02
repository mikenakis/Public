package mikenakis.test.intertwine;

import mikenakis.test.intertwine.rig.Alpha;
import mikenakis.test.intertwine.rig.Beta;
import mikenakis.test.intertwine.rig.FooInterface;

class ClientHelpers
{
	private ClientHelpers()
	{
	}

	static void runHappyPath( FooInterface fooInterface )
	{
		Alpha alpha1 = new Alpha( "alpha1", new Beta( "beta1" ) );
		fooInterface.setAlpha( 1, alpha1 );
		assert fooInterface.getAlpha( 1 ).equals( alpha1 );
		Alpha alpha2 = new Alpha( "alpha2", new Beta( "beta2" ) );
		fooInterface.setAlpha( 2, alpha2 );
		assert fooInterface.getAlpha( 1 ).equals( alpha1 );
		assert fooInterface.getAlpha( 2 ).equals( alpha2 );
	}
}
