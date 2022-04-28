package mikenakis.immutability.helpers;

import mikenakis.immutability.mykit.MyKit;

public interface Stringizer
{
	Stringizer defaultInstance = new Stringizer()
	{
		@Override public String stringize( Object object )
		{
			assert !(object instanceof Class<?>);
			return MyKit.identityString( object );
		}

		@Override public String stringize( Class<?> jvmClass )
		{
			return jvmClass.getName();
		}
	};

	String stringize( Object object );
	String stringize( Class<?> jvmClass );
}
