package mikenakis.tyraki;

/**
 * Mutable Array Set.
 *
 * @author michael.gr
 */
public interface MutableArraySet<E> extends MutableList<E>, UnmodifiableArraySet<E>
{
	interface Defaults<E> extends MutableArraySet<E>, MutableList.Defaults<E>, UnmodifiableArraySet.Defaults<E>
	{
	}
}
