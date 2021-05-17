package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.verification.SimpleVerificationType;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link SimpleVerificationType} {@link VerificationTypePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationTypePrinter extends VerificationTypePrinter
{
	public final SimpleVerificationType simpleVerificationType;

	public SimpleVerificationTypePrinter( SimpleVerificationType simpleVerificationType )
	{
		super( simpleVerificationType );
		this.simpleVerificationType = simpleVerificationType;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
	}
}
