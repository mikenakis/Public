package mikenakis.kit.spooling2.codec;

public abstract class JavaCodec<T> extends Codec<T>
{
	public final Class<T> javaClass;

	protected JavaCodec( Class<T> javaClass )
	{
		this.javaClass = javaClass;
	}

	@Override public final boolean isInstance( Object instance )
	{
		return javaClass.isInstance( instance );
	}

	@Override public final boolean instancesEqual( T left, T right )
	{
		assert isInstance( left );
		assert isInstance( right );
		return left.equals( right );
	}
}
