package mikenakis.io.sync.text.writing;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.nio.charset.StandardCharsets;

public class CloseableTextStreamWriterOnBinaryStreamWriter extends Mutable implements CloseableTextStreamWriter.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Procedure0 onClose;
	private final BinaryStreamWriter binaryStreamWriter;

	public CloseableTextStreamWriterOnBinaryStreamWriter( MutationContext mutationContext, BinaryStreamWriter binaryStreamWriter, Procedure0 onClose )
	{
		super( mutationContext );
		assert binaryStreamWriter != null;
		assert onClose != null;
		this.onClose = onClose;
		this.binaryStreamWriter = binaryStreamWriter;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( true );
		return true;
	}

	@Override public void close()
	{
		lifeGuard.close();
		onClose.invoke();
	}

	@Override public void write( String text )
	{
		assert !text.isEmpty();
		binaryStreamWriter.writeBytes( text.getBytes( StandardCharsets.UTF_8 ) );
	}
}
