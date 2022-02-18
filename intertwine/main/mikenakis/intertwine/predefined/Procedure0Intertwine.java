package mikenakis.intertwine.predefined;

import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.MethodKey;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.util.Collection;
import java.util.List;

/**
 * Predefined {@link Intertwine} for interface {@link Procedure0}.
 *
 * @author michael.gr
 */
public class Procedure0Intertwine implements Intertwine<Procedure0>
{
	private final class Entwiner
	{
		final Anycall<Procedure0> exitPoint;
		final Procedure0 entryPoint = new Procedure0()
		{
			@Override public void invoke()
			{
				exitPoint.anycall( key, Kit.ARRAY_OF_ZERO_OBJECTS );
			}
		};

		Entwiner( Anycall<Procedure0> exitPoint )
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

		private final Anycall<Procedure0> anycall = new Anycall<>()
		{
			@Override public Object anycall( MethodKey<Procedure0> key0, Object[] arguments )
			{
				assert key0 == key;
				assert arguments.length == 0;
				exitPoint.invoke();
				return null;
			}
		};
	}

	private static final MethodPrototype invokeMethodPrototype = MethodPrototype.of( "invoke", MethodDescriptor.of( void.class ) );
	private final List<MethodKey<Procedure0>> keys;
	private final MethodKey<Procedure0> key = new MethodKey<>()
	{
		@Override public MethodPrototype methodPrototype()
		{
			return invokeMethodPrototype;
		}

		@Override public int methodIndex()
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

	@Override public Collection<MethodKey<Procedure0>> keys()
	{
		return keys;
	}

	@Override public MethodKey<Procedure0> keyByIndex( int index )
	{
		return keys.get( index );
	}

	@Override public MethodKey<Procedure0> keyByMethodPrototype( MethodPrototype methodPrototype )
	{
		assert methodPrototype.equals( invokeMethodPrototype ) : new MethodNotFoundException( this, methodPrototype );
		return key;
	}

	@Override public Procedure0 newEntwiner( Anycall<Procedure0> exitPoint )
	{
		return new Entwiner( exitPoint ).entryPoint;
	}

	@Override public Anycall<Procedure0> newUntwiner( Procedure0 exitPoint )
	{
		return new Untwiner( exitPoint ).anycall;
	}
}
