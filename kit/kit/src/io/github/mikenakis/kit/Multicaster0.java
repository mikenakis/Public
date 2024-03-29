package io.github.mikenakis.kit;

import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.kit.logging.Log;

public class Multicaster0
{
	private Procedure0[] procedures = new Procedure0[0];
	public final Multicast0 multicast = new Multicast0.Defaults()
	{
		@Override public void add( Procedure0 procedure )
		{
			procedures = Kit.array.add( procedures, procedure, equalityComparator() );
		}

		@Override public void remove( Procedure0 procedure )
		{
			procedures = Kit.array.remove( procedures, procedure, equalityComparator() );
		}

		@Override public boolean contains( Procedure0 procedure )
		{
			return Kit.array.contains( procedures, procedure, equalityComparator() );
		}
	};

	private static EqualityComparator<Procedure0> equalityComparator()
	{
		return EqualityComparator.byReference();
	}

	public Multicaster0()
	{
	}

	public void invoke()
	{
		for( Procedure0 procedure : procedures )
			Kit.tryCatch( procedure ) //
				.ifPresent( Log::error );
	}
}
