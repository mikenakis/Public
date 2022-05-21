package io.github.mikenakis.lifetime;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure1;

/**
 * {@link Mortal} Wrapper for non-{@link Mortal} interfaces.
 *
 * @author michael.gr
 */
public interface MortalWrapper<T> extends Mortal.Defaults
{
	static <C> void tryWith( MortalWrapper<C> mortalWrapper, Procedure1<? super C> procedure )
	{
		Mortal.tryWith( mortalWrapper, wrapper -> procedure.invoke( wrapper.getTarget() ) );
	}

	static <R, C> R tryGetWith( MortalWrapper<C> mortalWrapper, Function1<R,? super C> function )
	{
		return Mortal.tryGetWith( mortalWrapper, wrapper -> function.invoke( wrapper.getTarget() ) );
	}

	T getTarget();

	interface Defaults<T> extends MortalWrapper<T>
	{
	}

	interface Decorator<T> extends Defaults<T>, Mortal.Decorator
	{
		MortalWrapper<T> decoratedMortalWrapper();

		@Override default Mortal getDecoratedMortal()
		{
			return decoratedMortalWrapper();
		}

		@Override default T getTarget()
		{
			return decoratedMortalWrapper().getTarget();
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
		@Override public MortalWrapper<T> decoratedMortalWrapper()
		{
			return null;
		}
	}
}
