package mikenakis.io.sync.text.writing;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.io.Writer;

public class JdkWriterOnTextStreamWriter extends Writer implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final TextStreamWriter textStreamWriter;
	private final Procedure0 onClose;

	public JdkWriterOnTextStreamWriter( TextStreamWriter textStreamWriter, Procedure0 onClose )
	{
		this.textStreamWriter = textStreamWriter;
		this.onClose = onClose;
	}

	@Override public void write( char[] chars, int off, int len )
	{
		if( len == 0 )
			return;
		textStreamWriter.write( new String( chars, off, len ) );
	}

	@Override public void flush()
	{
		/* nothing to do */
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
