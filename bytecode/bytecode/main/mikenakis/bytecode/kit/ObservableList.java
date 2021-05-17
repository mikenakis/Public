package mikenakis.bytecode.kit;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * An observable {@link Collection}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ObservableList<E> extends AbstractList<E>
{
	final List<E> delegee;
	private final Runnable observer;

	public ObservableList( List<E> delegee, Runnable observer )
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
		delegee.add( e );
		observer.run();
		return true;
	}

	@Override public void add( int index, E e )
	{
		delegee.add( index, e );
		observer.run();
	}

	@Override public E get( int index )
	{
		return delegee.get( index );
	}

	@Override public E set( int index, E element )
	{
		E oldElement = delegee.set( index, element );
		if( Objects.equals( oldElement, element ) )
			return oldElement;
		observer.run();
		return oldElement;
	}

	@Override public boolean remove( Object o )
	{
		if( !delegee.remove( o ) )
			return false;
		observer.run();
		return true;
	}

	@Override public boolean contains( Object o )
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

	@Override public int indexOf( Object o )
	{
		return delegee.indexOf( o );
	}

	@Override public int lastIndexOf( Object o )
	{
		return delegee.lastIndexOf( o );
	}

	@Override public ListIterator<E> listIterator()
	{
		throw new UnsupportedOperationException();
	}

	@Override public ListIterator<E> listIterator( int index )
	{
		throw new UnsupportedOperationException();
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
