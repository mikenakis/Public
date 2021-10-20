package mikenakis.kit;

import mikenakis.kit.functional.Procedure0;

public interface Multicast0
{
	void add( Procedure0 procedure );
	void remove( Procedure0 procedure );
	boolean contains( Procedure0 procedure );

	void register( boolean register, Procedure0 procedure );

	interface Defaults extends Multicast0
	{
		@Override default void register( boolean register, Procedure0 procedure )
		{
			if( register )
				add( procedure );
			else
				remove( procedure );
		}
	}
}
