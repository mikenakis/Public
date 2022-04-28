package mikenakis.immutability.type;

public interface Decomposer<T,E>
{
	Iterable<E> decompose( T object );
}
