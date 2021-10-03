package mikenakis.intertwine.predefined;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure2;

import java.util.Collection;
import java.util.List;

/**
 * Predefined {@link Intertwine} for interface {@link Procedure0}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Procedure2Intertwine implements Intertwine<Procedure2<Object,Object>>
{
	private final class Entwiner
	{
		final AnyCall<Procedure2<Object,Object>> exitPoint;
		final Procedure2<Object,Object> entryPoint = new Procedure2<>()
		{
			@Override public void invoke( Object parameter1, Object parameter2 )
			{
				exitPoint.anyCall( key, new Object[] { parameter1, parameter2 } );
			}
		};

		Entwiner( AnyCall<Procedure2<Object,Object>> exitPoint )
		{
			this.exitPoint = exitPoint;
		}
	}

	private final class Untwiner
	{
		final Procedure2<Object,Object> exitPoint;

		Untwiner( Procedure2<Object,Object> exitPoint )
		{
			this.exitPoint = exitPoint;
		}

		private final AnyCall<Procedure2<Object,Object>> anyCall = new AnyCall<>()
		{
			@Override public Object anyCall( Key<Procedure2<Object,Object>> key0, Object[] arguments )
			{
				assert key0 == key;
				assert arguments.length == 2;
				exitPoint.invoke( arguments[0], arguments[1] );
				return null;
			}
		};
	}

	private static final String INVOKE_METHOD_NAME = "invoke";
	private final List<Key<Procedure2<Object,Object>>> keys;
	private final Key<Procedure2<Object,Object>> key = new Key<>()
	{
		@Override public String getPrototypeString()
		{
			return INVOKE_METHOD_NAME;
		}

		@Override public int getIndex()
		{
			return 0;
		}

		@Override public Intertwine<Procedure2<Object,Object>> getIntertwine()
		{
			return Procedure2Intertwine.this;
		}
	};

	public Procedure2Intertwine()
	{
		keys = List.of( key );
	}

	@Override public Class<Procedure2<Object,Object>> interfaceType()
	{
		return Kit.uncheckedClassCast( Procedure2.class );
	}

	@Override public Collection<Key<Procedure2<Object,Object>>> keys()
	{
		return keys;
	}

	@Override public Key<Procedure2<Object,Object>> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public Key<Procedure2<Object,Object>> keyByPrototypeString( String prototypeString )
	{
		assert prototypeString.equals( INVOKE_METHOD_NAME ) : new KeyNotFoundException( prototypeString );
		return key;
	}

	@Override public Procedure2<Object,Object> newEntwiner( AnyCall<Procedure2<Object,Object>> exitPoint )
	{
		return new Entwiner( exitPoint ).entryPoint;
	}

	@Override public AnyCall<Procedure2<Object,Object>> newUntwiner( Procedure2<Object,Object> exitPoint )
	{
		return new Untwiner( exitPoint ).anyCall;
	}
}
