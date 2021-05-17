package mikenakis.bytecode.kit;

import java.util.Collection;
import java.util.Set;

/**
 * An observable {@link Set}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ObservableSet<E> extends ObservableCollection<E> implements Set<E>
{
	public ObservableSet( Collection<E> delegee, Runnable observer )
	{
		super( delegee, observer );
	}
}
