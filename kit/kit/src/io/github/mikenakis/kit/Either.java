package io.github.mikenakis.kit;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Originally from <a href="https://stackoverflow.com/questions/26162407/is-there-an-equivalent-of-scalas-either-in-java-8">https://stackoverflow.com/questions/26162407/is-there-an-equivalent-of-scalas-either-in-java-8</a>
 */
public abstract class Either<L, R>
{
	public static <L, R> Either<L,R> left( L value )
	{
		return new Either<>()
		{
			@Override public <T> T map( Function<? super L,? extends T> lFunc, Function<? super R,? extends T> rFunc )
			{
				return lFunc.apply( value );
			}
		};
	}

	public static <L, R> Either<L,R> right( R value )
	{
		return new Either<>()
		{
			@Override public <T> T map( Function<? super L,? extends T> lFunc, Function<? super R,? extends T> rFunc )
			{
				return rFunc.apply( value );
			}
		};
	}

	private Either() { }

	public abstract <T> T map( Function<? super L,? extends T> lFunc, Function<? super R,? extends T> rFunc );

	public <T> Either<T,R> mapLeft( Function<? super L,? extends T> lFunc )
	{
		return map( t -> left( lFunc.apply( t ) ), t -> cast() );
	}

	public <T> Either<L,T> mapRight( Function<? super R,? extends T> lFunc )
	{
		return map( t -> cast(), t -> right( lFunc.apply( t ) ) );
	}

	private <LL,RR> Either<LL,RR> cast()
	{
		@SuppressWarnings( "unchecked" ) Either<LL,RR> result = (Either<LL,RR>)this;
		return result;
	}

	public void apply( Consumer<? super L> lFunc, Consumer<? super R> rFunc )
	{
		map( consume( lFunc ), consume( rFunc ) );
	}

	private <T> Function<T,Void> consume( Consumer<T> c )
	{
		return t -> {
			c.accept( t );
			return null;
		};
	}
}
