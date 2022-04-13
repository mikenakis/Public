package mikenakis.kit;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiCloser implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final List<Closeable> closeables = new ArrayList<>();

	public MultiCloser( Closeable... closeables )
	{
		this.closeables.addAll( Arrays.asList( closeables ) );
	}

	public <T extends Closeable> T add( T closeable )
	{
		closeables.add( closeable );
		return closeable;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		lifeGuard.close();
		for( int i = closeables.size() - 1;  i >= 0;  i-- )
			closeables.get( i ).close();
	}
}
