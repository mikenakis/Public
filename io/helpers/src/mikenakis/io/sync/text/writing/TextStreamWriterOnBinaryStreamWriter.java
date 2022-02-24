package mikenakis.io.sync.text.writing;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.nio.charset.StandardCharsets;

public final class TextStreamWriterOnBinaryStreamWriter extends Mutable implements CloseableWrapper<TextStreamWriter>, TextStreamWriter.Defaults
{
	public static CloseableWrapper<TextStreamWriter> of( MutationContext mutationContext, BinaryStreamWriter binaryStreamWriter, Procedure0 onClose )
	{
		return new TextStreamWriterOnBinaryStreamWriter( mutationContext, binaryStreamWriter, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Procedure0 onClose;
	private final BinaryStreamWriter binaryStreamWriter;

	private TextStreamWriterOnBinaryStreamWriter( MutationContext mutationContext, BinaryStreamWriter binaryStreamWriter, Procedure0 onClose )
	{
		super( mutationContext );
		assert binaryStreamWriter != null;
		assert onClose != null;
		this.onClose = onClose;
		this.binaryStreamWriter = binaryStreamWriter;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
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

	@Override public TextStreamWriter getTarget()
	{
		return this;
	}
}
