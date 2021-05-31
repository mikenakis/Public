package mikenakis.multicast;

public interface Multicast<T>
{
	void add( T observer );
	void remove( T observer );
	boolean contains( T observer );

	void register( boolean register, T observer );

	interface Defaults<T> extends Multicast<T>
	{
		@Override default void register( boolean register, T observer )
		{
			if( register )
				add( observer );
			else
				remove( observer );
		}
	}
}
