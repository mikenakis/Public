package mikenakis.kit;

/**
 * Computes hash codes.
 *
 * @author michael.gr
 */
public interface Hasher<T>
{
	int getHashCode( T item );
}
