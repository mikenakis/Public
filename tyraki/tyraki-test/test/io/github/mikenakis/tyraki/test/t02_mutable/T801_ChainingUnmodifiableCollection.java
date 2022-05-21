package io.github.mikenakis.tyraki.test.t02_mutable;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableList;
import io.github.mikenakis.tyraki.conversion.ConversionCollections;
import org.junit.Test;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T801_ChainingUnmodifiableCollection
{
	public T801_ChainingUnmodifiableCollection()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void ChainingUnmodifiableCollection()
	{
		UnmodifiableCollection<String> u = UnmodifiableList.of();
		UnmodifiableCollection<String> v = UnmodifiableList.of();
		UnmodifiableCollection<String> w = UnmodifiableList.of( "a", "b" );
		UnmodifiableCollection<String> x = UnmodifiableList.of();
		UnmodifiableCollection<String> y = UnmodifiableList.of( "b", "c" );
		UnmodifiableCollection<String> z = UnmodifiableList.of();
		UnmodifiableCollection<String> chain = ConversionCollections.newChainingCollectionOf( u, v, w, x, y, z );
		assert chain.equalsCollection( UnmodifiableCollection.of( "a", "b", "b", "c" ) );
	}
}
