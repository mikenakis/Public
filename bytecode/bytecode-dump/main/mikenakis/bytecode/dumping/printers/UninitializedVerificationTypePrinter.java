package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.verification.UninitializedVerificationType;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link UninitializedVerificationType} {@link VerificationTypePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UninitializedVerificationTypePrinter extends VerificationTypePrinter
{
	public final UninitializedVerificationType uninitializedVerificationType;

	public UninitializedVerificationTypePrinter( UninitializedVerificationType uninitializedVerificationType )
	{
		super( uninitializedVerificationType );
		this.uninitializedVerificationType = uninitializedVerificationType;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( ' ' );
		renderingContext.newPrinter( uninitializedVerificationType.instructionReference ).appendTo( renderingContext, builder );
	}
}
