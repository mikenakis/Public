package mikenakis.tyraki;

/**
 * Freezable {@link MutableArraySet}.
 *
 * @author michael.gr
 */
public interface FreezableArraySet<E> extends MutableArraySet<E>, FreezableCollection<E>
{
	interface Defaults<E> extends FreezableArraySet<E>, MutableArraySet.Defaults<E>, FreezableCollection.Defaults<E>
	{
	}
}
