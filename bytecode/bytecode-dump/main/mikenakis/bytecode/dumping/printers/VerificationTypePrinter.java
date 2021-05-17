package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * {@link VerificationType} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class VerificationTypePrinter extends Printer
{
	private final VerificationType verificationType;

	public VerificationTypePrinter( VerificationType verificationType )
	{
		this.verificationType = verificationType;
	}

	@Override @OverridingMethodsMustInvokeSuper
	public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( verificationType.kind.name );
	}
}
