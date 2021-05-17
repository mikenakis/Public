package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link TypeAnnotationsAttribute.LocalVariableTarget.Entry} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributeLocalVariableTargetEntryPrinter extends Printer
{
	private final TypeAnnotationsAttribute.LocalVariableTarget.Entry localVariableTargetEntry;

	public TypeAnnotationsAttributeLocalVariableTargetEntryPrinter( TypeAnnotationsAttribute.LocalVariableTarget.Entry localVariableTargetEntry )
	{
		this.localVariableTargetEntry = localVariableTargetEntry;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		localVariableTargetEntry.toStringBuilder( builder );
	}
}
