package mikenakis.io.sync.text.writing;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A {@link TextStreamWriter} which accumulates text in a supplied {@link StringBuilder}.
 *
 * @author michael.gr
 */
public final class InMemoryTextStreamWriter extends Mutable implements CloseableWrapper<TextStreamWriter>, TextStreamWriter.Defaults
{
	public static CloseableWrapper<TextStreamWriter> of( MutationContext mutationContext, StringBuilder stringBuilder, Procedure0 onClose )
	{
		return new InMemoryTextStreamWriter( mutationContext, stringBuilder, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Procedure0 onClose;
	private final StringBuilder stringBuilder;

	private InMemoryTextStreamWriter( MutationContext mutationContext, StringBuilder stringBuilder, Procedure0 onClose )
	{
		super( mutationContext );
		assert stringBuilder != null;
		assert onClose != null;
		this.stringBuilder = stringBuilder;
		this.onClose = onClose;
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

	@Override public void write( String str )
	{
		stringBuilder.append( str );
	}

	@Override public String toString()
	{
		return stringBuilder.toString();
	}

	@Override public TextStreamWriter getTarget()
	{
		return this;
	}
}
