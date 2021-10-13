package mikenakis.tyraki;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link UnmodifiableList} representing a series of {@link Integer}.
 *
 * @author michael.gr
 */
public final class IntegerSeriesList implements UnmodifiableList.Defaults<Integer>
{
	private final int length;

	/**
	 * Initializes a new instance of {@link IntegerSeriesList}.
	 *
	 * @param length the length of the list.
	 */
	public IntegerSeriesList( int length )
	{
		this.length = length;
	}

	@Override public boolean isFrozen()
	{
		return true;
	}

	@Override public int getModificationCount()
	{
		return 0;
	}

	@Override public EqualityComparator<? super Integer> getEqualityComparator()
	{
		return DefaultEqualityComparator.getInstance();
	}

	@Override public int size()
	{
		return length;
	}

	@Override public Optional<Integer> tryGet( Integer element )
	{
		assert element != null;
		if( element >= 0 && element < length )
			return Optional.of( element );
		return Optional.empty();
	}

	@Override public Integer get( int index )
	{
		return index;
	}

	@Override public int linearSearch( Integer element, int start, int end )
	{
		assert element != null;
		assert start >= 0 : new IndexOutOfBoundsException();
		assert start < length : new IndexOutOfBoundsException();
		assert end >= 0 : new IndexOutOfBoundsException();
		assert end < length : new IndexOutOfBoundsException();
		if( element < start || element >= end )
			return -1;
		return element;
	}

	@Override public UnmodifiableEnumerator<Integer> newUnmodifiableEnumerator()
	{
		return new UnmodifiableEnumerator.Defaults<>()
		{
			private int index;

			@Override public boolean isFinished()
			{
				return index >= length;
			}

			@Override public Integer getCurrent()
			{
				assert !isFinished() : new NoSuchElementException();
				return index;
			}

			@Override public UnmodifiableEnumerator<Integer> moveNext()
			{
				assert !isFinished() : new NoSuchElementException();
				index++;
				return this;
			}

			@ExcludeFromJacocoGeneratedReport @Override public String toString()
			{
				var builder = new StringBuilder();
				unmodifiableEnumeratorToStringBuilder( builder );
				return builder.toString();
			}
		};
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof UnmodifiableList )
			return equalsUnmodifiableList( Kit.upCast( other ) );
		if( other instanceof UnmodifiableEnumerable )
			return equalsUnmodifiableEnumerable( Kit.upCast( other ) );
		assert false; //does this ever happen?
		return false;
	}

	@Override public int hashCode()
	{
		Object[] values = toArrayOfObject();
		return Objects.hash( values );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return length + " elements";
	}
}
