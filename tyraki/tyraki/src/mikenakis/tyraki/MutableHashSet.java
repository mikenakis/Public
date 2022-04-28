package mikenakis.tyraki;

/**
 * Mutable Hash Set.
 *
 * @author michael.gr
 */
public interface MutableHashSet<T> extends MutableCollection<T>, UnmodifiableHashSet<T>
{
	interface Defaults<T> extends MutableHashSet<T>, UnmodifiableHashSet.Defaults<T>, MutableCollection.Defaults<T>
	{
	}
}
