package mikenakis.immutability.internal.type;

public interface Decomposer<T,E>
{
	Iterable<E> decompose( T object );
}
