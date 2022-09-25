package io.github.mikenakis.intertwine.predefined;

import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.MethodKey;
import io.github.mikenakis.java_type_model.MethodDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.kit.functional.Procedure2;

import java.util.List;

/**
 * Predefined {@link Intertwine} for interface {@link Procedure0}.
 *
 * @author michael.gr
 */
public class Procedure2Intertwine implements Intertwine<Procedure2<Object,Object>>
{
	private final class Entwiner
	{
		final Anycall<Procedure2<Object,Object>> exitPoint;
		final Procedure2<Object,Object> entryPoint = new Procedure2<>()
		{
			@Override public void invoke( Object parameter1, Object parameter2 )
			{
				exitPoint.anycall( key, new Object[] { parameter1, parameter2 } );
			}
		};

		Entwiner( Anycall<Procedure2<Object,Object>> exitPoint )
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

		private final Anycall<Procedure2<Object,Object>> anycall = new Anycall<>()
		{
			@Override public Object anycall( MethodKey<Procedure2<Object,Object>> key0, Object[] arguments )
			{
				assert key0 == key;
				assert arguments.length == 2;
				exitPoint.invoke( arguments[0], arguments[1] );
				return null;
			}
		};
	}

	private static final MethodPrototype invokeMethodPrototype = MethodPrototype.of( "invoke", MethodDescriptor.of( void.class, Object.class, Object.class ) );
	private final List<MethodKey<Procedure2<Object,Object>>> keys;
	private final MethodKey<Procedure2<Object,Object>> key = new MethodKey<>()
	{
		@Override public MethodPrototype methodPrototype()
		{
			return invokeMethodPrototype;
		}

		@Override public int methodIndex()
		{
			return 0;
		}

		@Override public Intertwine<Procedure2<Object,Object>> intertwine()
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

	@Override public List<MethodKey<Procedure2<Object,Object>>> keys()
	{
		return keys;
	}

	@Override public MethodKey<Procedure2<Object,Object>> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		assert methodPrototype.equals( invokeMethodPrototype ) : new MethodNotFoundException( this, methodPrototype );
		return key;
	}

	@Override public Procedure2<Object,Object> newEntwiner( Anycall<Procedure2<Object,Object>> exitPoint )
	{
		return new Entwiner( exitPoint ).entryPoint;
	}

	@Override public Anycall<Procedure2<Object,Object>> newUntwiner( Procedure2<Object,Object> exitPoint )
	{
		return new Untwiner( exitPoint ).anycall;
	}
}
