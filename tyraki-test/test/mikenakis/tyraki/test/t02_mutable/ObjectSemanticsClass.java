package mikenakis.tyraki.test.t02_mutable;

/**
 * An element with object semantics.
 *
 * @author michael.gr
 */
public final class ObjectSemanticsClass
{
	/**
	 * PEARL: The 'Variables' view of the IntellijIdea debugger will not invoke toString() on an object which does not override the default Object.toString()
	 */
	@Override public String toString()
	{
		return "{" + getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "}";
	}
}
