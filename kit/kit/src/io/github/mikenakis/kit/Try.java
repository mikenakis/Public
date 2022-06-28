package io.github.mikenakis.kit;

import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure1;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Originally from <a href="https://github.com/jasongoodwin/better-java-monads">https://github.com/jasongoodwin/better-java-monads</a>
 * Another implementation can be found at <a href="https://github.com/lambdista/try/blob/master/src/main/java/com/lambdista/util/Try.java">https://github.com/lambdista/try/blob/master/src/main/java/com/lambdista/util/Try.java</a>
 */
public abstract class Try<T>
{
	public interface FailableFunction0<R>
	{
		R get() throws Throwable;
	}

	public interface FailableProcedure1<T, E extends Throwable>
	{
		void accept( T t ) throws E;
	}

	public static <U> Try<U> success( U x )
	{
		return new Success<>( x );
	}

	public static <U> Try<U> failure( Throwable e )
	{
		return new Failure<>( e );
	}

	public static <U> Try<U> ofFailable( FailableFunction0<U> f )
	{
		try
		{
			return success( f.get() );
		}
		catch( Throwable t )
		{
			return failure( t );
		}
	}

	protected Try()
	{
	}

	public abstract <U> Try<U> map( Function1<? extends U,? super T> f );
	public abstract <U> Try<U> flatMap( Function1<Try<U>,? super T> f );
	public abstract T recover( Function<? super Throwable,T> f );
	public abstract Try<T> recoverWith( Function1<Try<T>,? super Throwable> f );
	public abstract T orElse( T value );
	public abstract Try<T> orElseTry( FailableFunction0<T> f );
	public abstract <X extends Throwable> T orElseThrow( Supplier<? extends X> exceptionSupplier ) throws X;
	public abstract T orElseThrow();
	public abstract T get();
	public abstract boolean isSuccess();
	public boolean isFailure() { return !isSuccess(); }
	public abstract Try<T> onSuccess( Procedure1<T> action );
	public abstract Try<T> onFailure( Procedure1<Throwable> action );
	public abstract Try<T> filter( Predicate<T> predicate );
	public abstract Optional<T> toOptional();
	public abstract Throwable throwable();

	private static class Success<T> extends Try<T>
	{
		private final T value;

		Success( T value )
		{
			this.value = value;
		}

		@Override public <U> Try<U> flatMap( Function1<Try<U>,? super T> f )
		{
			try
			{
				return f.invoke( value );
			}
			catch( Throwable t )
			{
				return Try.failure( t );
			}
		}

		@Override public T recover( Function<? super Throwable,T> f )
		{
			assert f != null;
			return value;
		}

		@Override public Try<T> recoverWith( Function1<Try<T>,? super Throwable> f )
		{
			assert f != null;
			return this;
		}

		@Override public T orElse( T value )
		{
			return this.value;
		}

		@Override public Try<T> orElseTry( FailableFunction0<T> f )
		{
			assert f != null;
			return this;
		}

		@Override public <X extends Throwable> T orElseThrow( Supplier<? extends X> exceptionSupplier ) throws X
		{
			return value;
		}

		@Override public T orElseThrow()
		{
			return value;
		}

		@Override public T get()
		{
			return value;
		}

		@Override public <U> Try<U> map( Function1<? extends U,? super T> f )
		{
			try
			{
				return new Success<>( f.invoke( value ) );
			}
			catch( Throwable t )
			{
				return Try.failure( t );
			}
		}

		@Override public boolean isSuccess()
		{
			return true;
		}

		@Override public Try<T> onSuccess( Procedure1<T> action )
		{
			action.invoke( value );
			return this;
		}

		@Override public Try<T> filter( Predicate<T> predicate )
		{
			return predicate.test( value ) ? this : Try.failure( new NoSuchElementException( "Predicate does not match for " + value ) );
		}

		@Override public Optional<T> toOptional()
		{
			return Optional.ofNullable( value );
		}

		@Override public Try<T> onFailure( Procedure1<Throwable> action )
		{
			return this;
		}

		@Override public Throwable throwable()
		{
			throw new AssertionError();
		}
	}

	private static class Failure<T> extends Try<T>
	{
		private final Throwable e;

		Failure( Throwable e )
		{
			assert e != null;
			this.e = e;
		}

		@Override public <U> Try<U> map( Function1<? extends U,? super T> f )
		{
			return Try.failure( e );
		}

		@Override public <U> Try<U> flatMap( Function1<Try<U>,? super T> f )
		{
			return Try.failure( e );
		}

		@Override public T recover( Function<? super Throwable,T> f )
		{
			return f.apply( e );
		}

		@Override public Try<T> recoverWith( Function1<Try<T>,? super Throwable> f )
		{
			try
			{
				return f.invoke( e );
			}
			catch( Throwable t )
			{
				return Try.failure( t );
			}
		}

		@Override public T orElse( T value )
		{
			return value;
		}

		@Override public Try<T> orElseTry( FailableFunction0<T> f )
		{
			return Try.ofFailable( f );
		}

		@Override public <X extends Throwable> T orElseThrow( Supplier<? extends X> exceptionSupplier ) throws X
		{
			throw exceptionSupplier.get();
		}

		@Override public T orElseThrow()
		{
			throw Kit.sneakyException( e );
		}

		@Override public T get()
		{
			throw new UncheckedException( e );
		}

		@Override public boolean isSuccess()
		{
			return false;
		}

		@Override public Try<T> onSuccess( Procedure1<T> action )
		{
			return this;
		}

		@Override public Try<T> filter( Predicate<T> predicate )
		{
			return this;
		}

		@Override public Optional<T> toOptional()
		{
			return Optional.empty();
		}

		@Override public Try<T> onFailure( Procedure1<Throwable> action )
		{
			action.invoke( e );
			return this;
		}

		@Override public Throwable throwable()
		{
			return e;
		}
	}
}
