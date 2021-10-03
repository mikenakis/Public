package mikenakis.intertwine.predefined;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.util.Collection;
import java.util.List;

/**
 * Predefined {@link Intertwine} for interface {@link Procedure0}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Procedure0Intertwine implements Intertwine<Procedure0>
{
	private final class Entwiner
	{
		final AnyCall<Procedure0> exitPoint;
		final Procedure0 entryPoint = new Procedure0()
		{
			@Override public void invoke()
			{
				exitPoint.anyCall( key, Kit.ARRAY_OF_ZERO_OBJECTS );
			}
		};

		Entwiner( AnyCall<Procedure0> exitPoint )
		{
			this.exitPoint = exitPoint;
		}
	}

	private final class Untwiner
	{
		final Procedure0 exitPoint;

		Untwiner( Procedure0 exitPoint )
		{
			this.exitPoint = exitPoint;
		}

		private final AnyCall<Procedure0> anyCall = new AnyCall<>()
		{
			@Override public Object anyCall( Key<Procedure0> key0, Object[] arguments )
			{
				assert key0 == key;
				assert arguments.length == 0;
				exitPoint.invoke();
				return null;
			}
		};
	}

	private static final String INVOKE_METHOD_NAME = "invoke";
	private final List<Key<Procedure0>> keys;
	private final Key<Procedure0> key = new Key<>()
	{
		@Override public String getPrototypeString()
		{
			return INVOKE_METHOD_NAME;
		}

		@Override public int getIndex()
		{
			return 0;
		}

		@Override public Intertwine<Procedure0> getIntertwine()
		{
			return Procedure0Intertwine.this;
		}
	};

	public Procedure0Intertwine()
	{
		keys = List.of( key );
	}

	@Override public Class<Procedure0> interfaceType()
	{
		return Procedure0.class;
	}

	@Override public Collection<Key<Procedure0>> keys()
	{
		return keys;
	}

	@Override public Key<Procedure0> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public Key<Procedure0> keyByPrototypeString( String prototypeString )
	{
		assert prototypeString.equals( INVOKE_METHOD_NAME ) : new KeyNotFoundException( prototypeString );
		return key;
	}

	@Override public Procedure0 newEntwiner( AnyCall<Procedure0> exitPoint )
	{
		return new Entwiner( exitPoint ).entryPoint;
	}

	@Override public AnyCall<Procedure0> newUntwiner( Procedure0 exitPoint )
	{
		return new Untwiner( exitPoint ).anyCall;
	}
}
