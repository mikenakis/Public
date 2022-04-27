package mikenakis.kit.lifetime;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherent;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;

/**
 * An object which is aware of its own lifetime.
 * See https://blog.michael.gr/2021/01/object-lifetime-awareness.html
 * In other words, this is an {@link AutoCloseable} which does not throw checked exceptions, and is capable of asserting its own 'alive' state.
 * Normally this should be called 'LifetimeAware', but that's too long.
 *
 * @author michael.gr
 */
public interface Mortal extends AutoCloseable, Coherent
{
	/**
	 * <p>Performs a debugger-friendly {@code try-with-resources} that returns a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-finally} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-function, passing it the {@link Mortal} object, and returns its result.</p></li>
	 *             <li><p>In the {@code finally} part, it invokes {@link Mortal#close()} on the {@link Mortal} object.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-function, passing it the {@link Mortal} object, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *             <li><p>If no exception is thrown:
	 *                  <ul>
	 *                      <li><p>It invokes {@link Mortal#close()} on the {@link Mortal} object.</p></li>
	 *                      <li><p>It returns the result of invoking try-function.</p></li>
	 *                  </ul></p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids the deplorable dumbfuckery of Java's {@code try-with-resources} where:
	 *         <ul>
	 *             <li><p>it forces you to use curly braces even when the code block consists of a single-statement
	 *              (By supplying your code in a lambda, you get to decide whether to use curly braces or not.)</p></li>
	 *             <li><p>it forces you to declare a variable, complete with its type, for the {@link Mortal} object
	 *              (You only have to declare the parameter to the lambda, without the type.)</p></li>
	 *        </ul></p></li>
	 * </ul>
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
		assert mortal != null;
		if( Kit.debugging() )
		{
			R result = tryFunction.invoke( mortal );
			mortal.close();
			return result;
		}
		else
		{
			try( mortal )
			{
				return tryFunction.invoke( mortal );
			}
		}
	}
	/**
	 * Same as {@link #tryGetWith(C, Function1)} but with a {@link Function0}, for situations where we want to create a {@link Mortal}, execute some code,
	 * and then destroy the {@link Mortal} but the code does not actually need to use the {@link Mortal}.
	 * <p>
	 * As an added bonus, avoids Java's deplorable dumbfuckery of forcing you to declare the type of the variable for the mortal.
	 *
	 * @param mortal      the {@link Mortal} to close when done.
	 * @param tryFunction a function which produces a result.
	 * @param <C>         the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 * @param <R>         the type of the result.
	 *
	 * @return the result of the try-function.
	 */
	static <C extends Mortal, R> R tryGetWith( C mortal, Function0<R> tryFunction )
	{
		assert mortal != null;
		if( Kit.debugging() )
		{
			R result = tryFunction.invoke();
			mortal.close();
			return result;
		}
		else
		{
			try( mortal )
			{
				return tryFunction.invoke();
			}
		}
	}
	/**
	 * <p>Performs a debugger-friendly {@code try-with-resources} that does not return a result.</p>
	 * <ul>
	 *     <li><p>If assertions <b>are not</b> enabled:
	 *         <ul>
	 *             <li><p>It opens a {@code try-finally} block.</p></li>
	 *             <li><p>In the {@code try} part, it invokes the try-procedure, passing it the {@link Mortal} object.</p></li>
	 *             <li><p>In the {@code finally} part, it invokes {@link Mortal#close()} on the {@link Mortal} object.</p></li>
	 *         </ul></p></li>
	 *     <li><p>If assertions <b>are</b> enabled:
	 *         <ul>
	 *             <li><p>It invokes the try-procedure, passing it the {@link Mortal} object, but it does so without using a {@code try-catch} block,
	 *             so that if an exception occurs, the debugger will stop at the throwing statement.</p></li>
	 *             <li><p>If no exception is thrown:
	 *                  <ul>
	 *                      <li><p>It invokes {@link Mortal#close()} on the {@link Mortal} object.</p></li>
	 *                  </ul></p></li>
	 *         </ul></p></li>
	 *     <li><p>As a bonus, it also avoids the deplorable dumbfuckery of Java's {@code try-with-resources} where:
	 *         <ul>
	 *             <li><p>it forces you to use curly braces even when the code block consists of a single-statement
	 *              (By supplying your code in a lambda, you get to decide whether to use curly braces or not.)</p></li>
	 *             <li><p>it forces you to declare a variable, complete with its type, for the {@link Mortal} object
	 *              (You only have to declare the parameter to the lambda, without the type.)</p></li>
	 *        </ul></p></li>
	 * </ul>
	 *
	 * @param mortal       the {@link Mortal} to close when done.
	 * @param tryProcedure a {@link Procedure1} which receives the {@link Mortal} object and does something with it.
	 * @param <C>          the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 */
	static <C extends Mortal> void tryWith( C mortal, Procedure1<? super C> tryProcedure )
	{
		if( Kit.debugging() )
		{
			tryProcedure.invoke( mortal );
			mortal.close();
		}
		else
		{
			try( mortal )
			{
				tryProcedure.invoke( mortal );
			}
		}
	}
	/**
	 * Same as {@link #tryWith(C, Procedure1)} but with a {@link Procedure0}, for situations where we want to create a {@link Mortal}, execute some code,
	 * and then destroy the {@link Mortal} but the code does not actually need to use the {@link Mortal}.
	 * <p>
	 * As an added bonus, avoids Java's deplorable dumbfuckery of forcing you to declare the type of the variable for the mortal.
	 *
	 * @param mortal       the {@link Mortal} to close when done.
	 * @param tryProcedure the {@link Procedure0} to execute.
	 * @param <C>          the type of the {@link Mortal}. (Must extend {@link Mortal}.)
	 */
	static <C extends Mortal> void tryWith( C mortal, Procedure0 tryProcedure )
	{
		if( Kit.debugging() )
		{
			tryProcedure.invoke();
			mortal.close();
		}
		else
		{
			try( mortal )
			{
				tryProcedure.invoke();
			}
		}
	}
	boolean mustBeAliveAssertion();

	@Override void close(); //overriding the close() method without any checked exceptions.

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends Mortal
	{
	}
}
