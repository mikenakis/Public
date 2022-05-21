package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.PartiallyConvertingEqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

class KeyConvertingMap<TK, SK, V> extends AbstractMap<TK,V>
{
	private class MyBinding implements Binding<TK,V>
	{
		final Binding<SK,V> sourceBinding;

		MyBinding( Binding<SK,V> sourceBinding )
		{
			this.sourceBinding = sourceBinding;
		}

		@Override public TK getKey()
		{
			SK sourceKey = sourceBinding.getKey();
			return converter.invoke( sourceKey );
		}

		@Override public V getValue()
		{
			return sourceBinding.getValue();
		}
	}

	private final class MyEntriesCollection extends AbstractMapEntriesCollection<TK,V>
	{
		MyEntriesCollection( EqualityComparator<? super TK> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( KeyConvertingMap.this, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			return KeyConvertingMap.this.mustBeImmutableAssertion();
		}

		@Override public int getModificationCount()
		{
			return map.entries().getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<TK,V>> newUnmodifiableEnumerator()
		{
			return map.entries().newUnmodifiableEnumerator().map( sourceBinding -> new MyBinding( sourceBinding ) );
		}
	}

	private final UnmodifiableMap<SK,V> map;
	private final Function1<? extends TK,? super SK> converter;
	private final Function1<Optional<? extends SK>,? super TK> reverter;
	private final MyEntriesCollection entries;

	KeyConvertingMap( UnmodifiableMap<SK,V> map, Function1<? extends TK,? super SK> converter, Function1<Optional<? extends SK>,? super TK> reverter )
	{
		assert converter != null;
		assert reverter != null;
		this.map = map;
		this.converter = converter;
		this.reverter = reverter;
		EqualityComparator<TK> keyEqualityComparator = new PartiallyConvertingEqualityComparator<>( reverter );
		EqualityComparator<? super V> valueEqualityComparator = map.values().getEqualityComparator();
		entries = new MyEntriesCollection( keyEqualityComparator, valueEqualityComparator );
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return map.mustBeImmutableAssertion();
	}

	@Override public UnmodifiableCollection<Binding<TK,V>> entries()
	{
		return entries;
	}

	@Override public final int size()
	{
		return map.size();
	}

	@Override public final Optional<Binding<TK,V>> tryGetBindingByKey( TK tk )
	{
		assert tk != null;
		Optional<? extends SK> sk = reverter.invoke( tk );
		if( sk.isEmpty() )
			return Optional.empty();
		Optional<Binding<SK,V>> binding = map.tryGetBindingByKey( sk.get() );
		if( binding.isEmpty() )
			return Optional.empty();
		TK key = converter.invoke( binding.get().getKey() );
		return Optional.of( MapEntry.of( key, binding.get().getValue() ) );
	}

	@Override public final UnmodifiableCollection<TK> keys()
	{
		return map.keys().map( converter, reverter );
	}

	@Override public final UnmodifiableCollection<V> values()
	{
		return map.values();
	}
}
