package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link NewPrimitiveArrayInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NewPrimitiveArrayInstructionPrinter extends InstructionPrinter
{
	private final NewPrimitiveArrayInstruction newPrimitiveArrayInstruction;

	public NewPrimitiveArrayInstructionPrinter( NewPrimitiveArrayInstruction newPrimitiveArrayInstruction )
	{
		super( newPrimitiveArrayInstruction );
		this.newPrimitiveArrayInstruction = newPrimitiveArrayInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( newPrimitiveArrayInstruction.model.opCode ) );
		builder.append( ' ' ).append( NewPrimitiveArrayInstruction.Type.fromNumber( newPrimitiveArrayInstruction.type ).name() );
	}
}
