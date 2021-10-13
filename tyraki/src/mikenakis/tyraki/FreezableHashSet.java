package mikenakis.tyraki;

/**
 * Freezable {@link MutableHashSet}.
 *
 * @author michael.gr
 */
public interface FreezableHashSet<T> extends MutableHashSet<T>, FreezableCollection<T>
{
	@Override UnmodifiableHashSet<T> frozen();

	interface Defaults<T> extends FreezableHashSet<T>, MutableHashSet.Defaults<T>, FreezableCollection.Defaults<T>
	{
	}
}
