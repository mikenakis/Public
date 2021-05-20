package mikenakis.multicast;

public interface Multicast<T>
{
	void add( T procedure );
	void remove( T procedure );
	boolean contains( T procedure );

	void register( boolean register, T procedure );

	interface Defaults<T> extends Multicast<T>
	{
		@Override default void register( boolean register, T procedure )
		{
			if( register )
				add( procedure );
			else
				remove( procedure );
		}
	}
}
