package mikenakis.kit.spooling2.codec;

import mikenakis.kit.Kit;

public final class JavaEnumCodec<T extends Enum<T>> extends EnumCodec<T>
{
	public final Class<T> javaEnumClass;
	private final T[] values;

	public JavaEnumCodec( Class<T> javaEnumClass )
	{
		assert javaEnumClass.isEnum();
		this.javaEnumClass = javaEnumClass;
		values = javaEnumClass.getEnumConstants();
		assert values != null;
	}

	@Override public int getInstanceCount()
	{
		return values.length;
	}

	@Override public T getInstanceFromOrdinal( int ordinal )
	{
		return values[ordinal];
	}

	@Override public int getOrdinalFromInstance( T value  )
	{
		return value.ordinal();
	}

	@Override public T instanceFromString( String content )
	{
		assert content != null;
		return Enum.valueOf( javaEnumClass, content );
	}

	@Override public boolean isInstance( Object instance )
	{
		assert instance != null;
		@SuppressWarnings( "unchecked" ) T value = (T)instance;
		boolean result = Kit.array.indexOf( values, value ) != -1;
		assert result == (value.getDeclaringClass() == javaEnumClass);
		return result;
	}

	@Override public String stringFromInstance( T value )
	{
		assert value != null;
		return value.toString();
	}
}
