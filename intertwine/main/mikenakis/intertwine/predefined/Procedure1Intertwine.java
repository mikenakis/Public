package mikenakis.intertwine.predefined;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;

import java.util.Collection;
import java.util.List;

/**
 * Predefined {@link Intertwine} for interface {@link Procedure0}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Procedure1Intertwine implements Intertwine<Procedure1<Object>>
{
	private final class Entwiner
	{
		final AnyCall<Procedure1<Object>> exitPoint;

		final Procedure1<Object> entryPoint = new Procedure1<>()
		{
			@Override public void invoke( Object parameter )
			{
				exitPoint.anyCall( key, new Object[] { parameter } );
			}
		};

		Entwiner( AnyCall<Procedure1<Object>> exitPoint )
		{
			this.exitPoint = exitPoint;
		}
	}

	private final class Untwiner
	{
		final Procedure1<Object> exitPoint;

		final AnyCall<Procedure1<Object>> anyCall = new AnyCall<>()
		{
			@Override public Object anyCall( MethodKey<Procedure1<Object>> key0, Object[] arguments )
			{
				assert key0 == key;
				assert arguments.length == 1;
				exitPoint.invoke( arguments[0] );
				return null;
			}
		};

		Untwiner( Procedure1<Object> exitPoint )
		{
			this.exitPoint = exitPoint;
		}
	}

	private static final MethodPrototype invokeMethodPrototype = MethodPrototype.of( "invoke", MethodDescriptor.of( void.class, Object.class ) );
	private final List<MethodKey<Procedure1<Object>>> keys;
	private final MethodKey<Procedure1<Object>> key = new MethodKey<>()
	{
		@Override public MethodPrototype methodPrototype()
		{
			return invokeMethodPrototype;
		}

		@Override public int methodIndex()
		{
			return 0;
		}

		@Override public Intertwine<Procedure1<Object>> getIntertwine()
		{
			return Procedure1Intertwine.this;
		}
	};

	public Procedure1Intertwine()
	{
		keys = List.of( key );
	}

	@Override public Class<Procedure1<Object>> interfaceType()
	{
		return Kit.uncheckedClassCast( Procedure1.class );
	}

	@Override public Collection<MethodKey<Procedure1<Object>>> keys()
	{
		return keys;
	}

	@Override public MethodKey<Procedure1<Object>> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public MethodKey<Procedure1<Object>> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		assert methodPrototype.equals( invokeMethodPrototype ) : new MethodNotFoundException( this, methodPrototype );
		return key;
	}

	@Override public Procedure1<Object> newEntwiner( AnyCall<Procedure1<Object>> exitPoint )
	{
		return new Entwiner( exitPoint ).entryPoint;
	}

	@Override public AnyCall<Procedure1<Object>> newUntwiner( Procedure1<Object> exitPoint )
	{
		return new Untwiner( exitPoint ).anyCall;
	}
}
