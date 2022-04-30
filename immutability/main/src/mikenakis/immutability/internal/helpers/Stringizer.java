package mikenakis.immutability.internal.helpers;

import mikenakis.immutability.internal.mykit.MyKit;

import java.lang.reflect.Field;

public abstract class Stringizer
{
	public static final Stringizer defaultInstance = new Stringizer()
	{
		@Override public String stringizeObjectIdentity( Object object )
		{
			assert !(object instanceof Class<?>);
			return MyKit.identityString( object );
		}

		@Override public String stringizeClassName0( Class<?> jvmClass )
		{
			return MyKit.getClassName( jvmClass );
		}
	};

	public abstract String stringizeObjectIdentity( Object object );

	public final String stringizeClassName( Class<?> jvmClass )
	{
		return "'" + stringizeClassName0( jvmClass ) + "'";
	}

	public static String stringizeFieldName( Field field )
	{
		return "'" + field.getName() + "'";
	}

	protected abstract String stringizeClassName0( Class<?> jvmClass );
}