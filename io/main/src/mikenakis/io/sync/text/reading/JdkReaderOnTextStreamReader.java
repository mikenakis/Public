package mikenakis.io.sync.text.reading;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.io.Reader;
import java.util.Optional;

public class JdkReaderOnTextStreamReader extends Reader implements Closeable.Defaults
{
	private final LifeGuard lifeGuard;
	private final TextStreamReader textStreamReader;
	private final Procedure0 onClose;

	public JdkReaderOnTextStreamReader( TextStreamReader textStreamReader, Procedure0 onClose )
	{
		assert textStreamReader != null;
		assert onClose != null;
		lifeGuard = LifeGuard.create( this );
		this.textStreamReader = textStreamReader;
		this.onClose = onClose;
	}

	@Override public int read( char[] chars, int off, int len )
	{
		//Log.debug( Integer.toHexString( System.identityHashCode( this ) ) + " read: " + len );
		Optional<String> optionalString = textStreamReader.tryRead( len );
		if( optionalString.isEmpty() )
		{
			//Log.debug( " -> " + 0 );
			return -1;
		}
		int n = optionalString.get().length();
		optionalString.get().getChars( 0, n, chars, off );
		//Log.debug( " -> " + n );
		return n;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( value );
		return true;
	}

	@Override public void close()
	{
		lifeGuard.close();
		onClose.invoke();
	}
}
