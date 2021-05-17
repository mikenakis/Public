package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.verification.ObjectVerificationType;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ObjectVerificationType} {@link VerificationTypePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ObjectVerificationTypePrinter extends VerificationTypePrinter
{
	private final ObjectVerificationType objectVerificationType;

	public ObjectVerificationTypePrinter( ObjectVerificationType objectVerificationType )
	{
		super( objectVerificationType );
		this.objectVerificationType = objectVerificationType;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( ' ' );
		renderingContext.newPrinter( objectVerificationType.classConstant ).appendIndexTo( renderingContext, builder );
	}
}
