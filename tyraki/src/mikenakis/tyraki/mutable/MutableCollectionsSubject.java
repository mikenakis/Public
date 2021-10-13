package mikenakis.tyraki.mutable;

/**
 * A subject of {@link MutableCollections}.
 *
 * @author michael.gr
 */
public abstract class MutableCollectionsSubject
{
	private final MutableCollections mutableCollections;

	protected MutableCollectionsSubject( MutableCollections mutableCollections )
	{
		assert mutableCollections != null;
		this.mutableCollections = mutableCollections;
	}

	public final MutableCollections getMutableCollections()
	{
		return mutableCollections;
	}
}
