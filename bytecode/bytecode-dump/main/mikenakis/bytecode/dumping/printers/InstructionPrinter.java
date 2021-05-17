package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * {@link Instruction} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class InstructionPrinter extends Printer
{
	private final Instruction instruction;

	public InstructionPrinter( Instruction instruction )
	{
		this.instruction = instruction;
	}

	@Override @OverridingMethodsMustInvokeSuper
	public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		String prefix;
		if( renderingContext.style.raw )
			prefix = "@" + instruction.getPc() + ":";
		else
			prefix = "";
		builder.append( String.format( "  %-7s", prefix ) );
	}
}
