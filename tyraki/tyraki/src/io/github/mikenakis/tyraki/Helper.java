package io.github.mikenakis.tyraki;

import io.github.mikenakis.coherence.implementation.ConcreteFreezableCoherence;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure1;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.tyraki.mutable.MutableCollections;

class Helper
{
	private Helper()
	{
	}

	public static <C, M extends C> M populate( Function1<M,MutableCollections> factory, Procedure1<M> populator )
	{
		return Mortal.tryGetWith( ConcreteFreezableCoherence.create(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			M map = factory.invoke( mutableCollections );
			populator.invoke( map );
			return map;
		} );
	}
}
