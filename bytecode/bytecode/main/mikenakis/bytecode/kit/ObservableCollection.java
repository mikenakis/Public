package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * An observable {@link Collection}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ObservableCollection<E> extends AbstractCollection<E>
{
	final Collection<E> delegee;
	private final Runnable observer;

	public ObservableCollection( Collection<E> delegee, Runnable observer )
	{
		this.delegee = delegee;
		this.observer = observer;
	}

	@Override public Iterator<E> iterator()
	{
		return new ObservableIterator<>( delegee.iterator(), observer );
	}

	@Override public int size()
	{
		return delegee.size();
	}

	@Override public boolean add( E e )
	{
		if( !Kit.collection.tryAdd( delegee, e ) )
			return false;
		observer.run();
		return true;
	}

	@Override public boolean remove( Object o )
	{
		if( !Kit.collection.tryRemove( delegee, o ) )
			return false;
		observer.run();
		return true;
	}

	@SuppressWarnings( "deprecation" ) @Override public boolean contains( Object o )
	{
		return delegee.contains( o );
	}

	@Override public void clear()
	{
		if( isEmpty() )
			return;
		delegee.clear();
		observer.run();
	}

	@Override public String toString()
	{
		return delegee.toString();
	}

	@SuppressWarnings( "EqualsWhichDoesntCheckParameterClass" )
	@Override public boolean equals( Object o )
	{
		return delegee.equals( o );
	}

	@Override public int hashCode()
	{
		return delegee.hashCode();
	}
}
