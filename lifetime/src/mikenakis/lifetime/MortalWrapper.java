package mikenakis.lifetime;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;

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
}
