package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link IIncInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IIncInstructionPrinter extends InstructionPrinter
{
	private final IIncInstruction iIncInstruction;

	public IIncInstructionPrinter( IIncInstruction iIncInstruction )
	{
		super( iIncInstruction );
		this.iIncInstruction = iIncInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		if( iIncInstruction.isWide() )
			builder.append( "wide " );
		builder.append( OpCode.getOpCodeName( iIncInstruction.model.opCode ) );
		builder.append( ' ' ).append( iIncInstruction.localVariableIndex );
		builder.append( ' ' ).append( iIncInstruction.delta );
	}
}
