package mikenakis.lifetime;

import mikenakis.coherence.Coherent;
import mikenakis.debug.Debug;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;

/**
 * An object which is aware of its own lifetime.
 * <p>
 * This is essentially an {@link AutoCloseable} which:
 * <p>
 * - Has a {@link #close()} method which does not throw checked exceptions.
 * <p>
 * - Is capable of asserting that it is still 'alive' (not closed.)
 * <p>
 * Normally this should be called {@code LifetimeAware}, but that's too long, so we just call it {@code Mortal}.
 * <p>
 * See <a href="https://blog.michael.gr/2021/01/object-lifetime-awareness.html">michael.gr - Object Lifetime Awareness</a>
 * <p>
 * PEARL: the {@code try-with-resources} clause of Java has several problems:
 * <p>
 * - It prevents exceptions thrown within the {@code try{...}} block from being considered as uncaught. So, if the debugger is configured to stop on any
 * uncaught exception, (as it should,) and an exception is thrown within the {@code try{...}} block, the debugger will not stop at the throwing statement;
 * instead, the debugger will stop at the closing curly brace of the {@code try{...}} block, which is entirely useless, counter-productive, and annoying.
 * <p>
 * - It forces you to use curly braces even when the code block consists of a single-statement.
 * <p>
 * - It forces you to declare a variable, complete with its type, for the {@link Mortal} object, even if you have no use for the {@link Mortal} object inside
 * the {@code try{...}} block.
 *
 * @author michael.gr
 */
public interface Mortal extends AutoCloseable, Coherent
{
	/**
	 * Performs a {@code try-with-resources} that returns a result.
	 * <p>
	 * Provides the following benefits over Java's {@code try-with-resources} statement:
	 * <p>
	 * - If an uncaught exception occurs within the {@code try{...}} block, the debugger will stop at the throwing statement instead of the closing curly brace
	 * of the {@code try{...}} block. (Duh!)
	 * <p>
	 * - You do not have to use curly braces if your block consists of a single-statement. (By supplying your code in a lambda, you get to decide whether to use
	 * curly braces or not.)
	 * <p>
	 * - You do not have to declare the type of the {@link Mortal}, since the type of the lambda parameter is inferred.  (Alternatively, if you have no use for
	 * the {@link Mortal} within the lambda, you can use the other form of this method which accepts a parameterless lambda.)
	 *
	 * @param mortal      the {@link Mortal} to close when done.
	 * @param tryFunction a function which receives the {@link Mortal} object and produces a result.
	 * @param <C>         the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	static <C extends Mortal, R> R tryGetWith( C mortal, Function1<R,? super C> tryFunction )
	{
		try( mortal )
		{
			return Debug.boundary( () -> //
				tryFunction.invoke( mortal ) );
		}
	}

	/**
	 * Same as {@link #tryGetWith(C, Function1)} but with a {@link Function0}, for situations where we want to create a {@link Mortal}, execute some code, and
	 * then destroy the {@link Mortal} but the code does not actually need to use the {@link Mortal}.
	 * <p>
	 * @param mortal      the {@link Mortal} to close when done.
	 * @param tryFunction a function which produces a result.
	 * @param <C>         the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	static <C extends Mortal, R> R tryGetWith( C mortal, Function0<R> tryFunction )
	{
		try( mortal )
		{
			return Debug.boundary( () -> tryFunction.invoke() );
		}
	}

	/**
	 * <p>Performs a debugger-friendly {@code try-with-resources} that does not return a result.</p>
	 *
	 * @param mortal       the {@link Mortal} to close when done.
	 * @param tryProcedure a {@link Procedure1} which receives the {@link Mortal} object and does something with it.
	 * @param <C>          the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 */
	static <C extends Mortal> void tryWith( C mortal, Procedure1<? super C> tryProcedure )
	{
		try( mortal )
		{
			Debug.boundary( () -> //
				tryProcedure.invoke( mortal ) );
		}
	}

	/**
	 * Same as {@link #tryWith(C, Procedure1)} but with a {@link Procedure0}, for situations where we want to create a {@link Mortal}, execute some code, and
	 * then destroy the {@link Mortal} but the code does not actually need to use the {@link Mortal}.
	 *
	 * @param mortal       the {@link Mortal} to close when done.
	 * @param tryProcedure the {@link Procedure0} to execute.
	 * @param <C>          the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 */
	static <C extends Mortal> void tryWith( C mortal, Procedure0 tryProcedure )
	{
		try( mortal )
		{
			Debug.boundary( () -> //
				tryProcedure.invoke() );
		}
	}

	boolean mustBeAliveAssertion();

	@Override void close(); //overriding the close() method without any checked exceptions.

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends Mortal, Coherent.Defaults
	{ }

	interface Decorator extends Defaults, Coherent.Decorator
	{
		Mortal getDecoratedMortal();

		@Override default Coherent decoratedCoherent()
		{
			return getDecoratedMortal();
		}

		@Override default boolean mustBeAliveAssertion()
		{
			return getDecoratedMortal().mustBeAliveAssertion();
		}

		@Override default void close()
		{
			getDecoratedMortal().close();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Canary class.
	 * <p>
	 * This is a concrete class to make sure that if there are problems with the interface making it impossible to inherit from, they will be caught by the
	 * compiler at the earliest point possible, and not when compiling some derived class.
	 */
	@ExcludeFromJacocoGeneratedReport
	@SuppressWarnings( "unused" )
	final class Canary<K, V> implements Decorator
	{
		@Override public Mortal getDecoratedMortal()
		{
			return this;
		}
	}
}
