package mikenakis.tyraki;

/**
 * Freezable {@link MutableArrayHashSet}.
 *
 * @author michael.gr
 */
public interface FreezableArrayHashSet<E> extends FreezableArraySet<E>, FreezableHashSet<E>, MutableArrayHashSet<E>
{
	@Override UnmodifiableArrayHashSet<E> frozen();

	interface Defaults<E> extends FreezableArrayHashSet<E>, FreezableHashSet.Defaults<E>, FreezableArraySet.Defaults<E>, MutableArrayHashSet.Defaults<E>
	{
	}
}
