package mikenakis.agentclaire;

/**
 * The AgentClaire interface.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface AgentClaire
{
	void registerInterceptor( boolean register, Interceptor interceptor );

	default void addInterceptor( Interceptor interceptor )
	{
		registerInterceptor( true, interceptor );
	}

	@SuppressWarnings( "unused" ) default void removeInterceptor( Interceptor interceptor )
	{
		registerInterceptor( false, interceptor );
	}
}
