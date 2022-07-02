package io.github.mikenakis.live;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function0;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.kit.functional.Procedure1;

/**
 * {@link Mortal} Wrapper for non-{@link Mortal} interfaces.
 *
 * TODO: actually, use this everywhere and get rid of `Mortal` and `Lifeguard`!
 *
 * @author michael.gr
 */
public interface Live<T> extends Mortal
{
	static <T> Live<T> of( Coherence coherence, T object, Procedure0 close )
	{
		return new ConcreteLive<>( coherence, object, close );
	}

	static <T extends Coherent> Live<T> of( T object, Procedure0 close )
	{
		return of( object.coherence(), object, close );
	}

	static <T> void tryWith( Live<T> live, Procedure1<? super T> procedure )
	{
		Mortal.tryWith( live, o -> procedure.invoke( o.target() ) );
	}

	static <R, T> R tryGetWith( Live<T> live, Function1<R,? super T> function )
	{
		return Mortal.tryGetWith( live, o -> function.invoke( o.target() ) );
	}

	static <T> void tryWith( Live<T> live, Procedure0 procedure )
	{
		Mortal.tryWith( live, o -> procedure.invoke() );
	}

	static <R, T> R tryGetWith( Live<T> live, Function0<R> function )
	{
		return Mortal.tryGetWith( live, o -> function.invoke() );
	}

	T target();

	interface Defaults<T> extends Live<T>, Mortal.Defaults
	{
	}

	interface Decorator<T> extends Defaults<T>, Mortal.Decorator
	{
		Live<T> decoratedLive();

		@Override default Mortal getDecoratedMortal()
		{
			return decoratedLive();
		}

		@Override default T target()
		{
			return decoratedLive().target();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Canary class.
	 * <p>
	 * This is a concrete class to make sure that if there are problems with the interface making it impossible to inherit from, they will be caught by the compiler at the
	 * earliest point possible, and not when compiling some derived class.
	 */
	@ExcludeFromJacocoGeneratedReport
	@SuppressWarnings( "unused" )
	final class Canary<T> implements Decorator<T>
	{
		@Override public Live<T> decoratedLive()
		{
			return null;
		}
	}
}
