package mikenakis.tyraki.test.t02_mutable;

import mikenakis.kit.Kit;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableList;
import mikenakis.tyraki.conversion.ConversionCollections;
import org.junit.Test;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T802_ChainingUnmodifiableSet
{
	public T802_ChainingUnmodifiableSet()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void ChainingUnmodifiableSet()
	{
		UnmodifiableCollection<String> u = UnmodifiableList.of();
		UnmodifiableCollection<String> v = UnmodifiableList.of();
		UnmodifiableCollection<String> w = UnmodifiableList.of( "a", "b" );
		UnmodifiableCollection<String> x = UnmodifiableList.of();
		UnmodifiableCollection<String> y = UnmodifiableList.of( "b", "c" );
		UnmodifiableCollection<String> z = UnmodifiableList.of();
		UnmodifiableCollection<String> chain = ConversionCollections.newChainingSetOf( u, v, w, x, y, z );
		assert chain.equalsCollection( UnmodifiableCollection.of( "a", "b", "c" ) );
	}
}
