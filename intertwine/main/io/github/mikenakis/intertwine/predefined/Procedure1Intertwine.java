package io.github.mikenakis.intertwine.predefined;

import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.java_type_model.MethodDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.kit.functional.Procedure1;

import java.util.Collection;
import java.util.List;

/**
 * Predefined {@link Intertwine} for interface {@link Procedure0}.
 *
 * @author michael.gr
 */
public class Procedure1Intertwine implements Intertwine<Procedure1<Object>>
{
	private final class Entwiner
	{
		final Anycall<Procedure1<Object>> exitPoint;

		final Procedure1<Object> entryPoint = new Procedure1<>()
		{
			@Override public void invoke( Object parameter )
			{
				exitPoint.anycall( key, new Object[] { parameter } );
			}
		};

		Entwiner( Anycall<Procedure1<Object>> exitPoint )
		{
			this.exitPoint = exitPoint;
		}
	}

	private final class Untwiner
	{
		final Procedure1<Object> exitPoint;

		final Anycall<Procedure1<Object>> anycall = new Anycall<>()
		{
			@Override public Object anycall( MethodKey<Procedure1<Object>> key0, Object[] arguments )
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

		@Override public Intertwine<Procedure1<Object>> intertwine()
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

	@Override public Procedure1<Object> newEntwiner( Anycall<Procedure1<Object>> exitPoint )
	{
		return new Entwiner( exitPoint ).entryPoint;
	}

	@Override public Anycall<Procedure1<Object>> newUntwiner( Procedure1<Object> exitPoint )
	{
		return new Untwiner( exitPoint ).anycall;
	}
}
