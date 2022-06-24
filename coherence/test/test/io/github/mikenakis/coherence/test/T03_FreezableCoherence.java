package io.github.mikenakis.coherence.test;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.ConcreteFreezableCoherence;
import io.github.mikenakis.coherence.implementation.ThreadLocalCoherence;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.testkit.TestKit;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.mutable.MutableCollections;
import org.junit.Test;

/**
 * Test.
 */
public class T03_FreezableCoherence
{
	public T03_FreezableCoherence()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void test_FreezableCoherence()
	{
		Coherence parentCoherence = ThreadLocalCoherence.instance();
		TestClass testObject = Mortal.tryGetWith( ConcreteFreezableCoherence.create( parentCoherence ), coherence -> //
		{
			TestClass t = new TestClass( coherence );
			t.readOperation();
			t.writeOperation();
			return t;
		} );
		testObject.readOperation();
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.coherence.getClass() == ConcreteFreezableCoherence.class;

		MutableList<String> testList = Mortal.tryGetWith( ConcreteFreezableCoherence.create( parentCoherence ), coherence -> //
		{
			MutableList<String> t = MutableCollections.of( coherence ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList.size();
		var exception4 = TestKit.expect( MustBeWritableException.class, () -> testList.add( "b" ) );
		assert exception4.coherence.getClass() == ConcreteFreezableCoherence.class;

		MutableList<String> testList2 = Mortal.tryGetWith( ConcreteFreezableCoherence.create( parentCoherence ), coherence -> //
		{
			MutableList<String> t = MutableCollections.of( coherence ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList2.size();
		var exception5 = TestKit.expect( MustBeWritableException.class, () -> testList2.add( "b" ) );
		assert exception5.coherence.getClass() == ConcreteFreezableCoherence.class;
	}
}
