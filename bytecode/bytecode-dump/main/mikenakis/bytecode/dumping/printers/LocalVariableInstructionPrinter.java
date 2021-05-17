package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.kit.Helpers;

/**
 * {@link LocalVariableInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableInstructionPrinter extends InstructionPrinter
{
	private final LocalVariableInstruction localVariableInstruction;

	public LocalVariableInstructionPrinter( LocalVariableInstruction localVariableInstruction )
	{
		super( localVariableInstruction );
		this.localVariableInstruction = localVariableInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		int flavor = localVariableInstruction.localVariableIndex <= 3 && localVariableInstruction.model.opCode != OpCode.RET ? localVariableInstruction.localVariableIndex : -1;
		boolean wide = !Helpers.isUnsignedByte( localVariableInstruction.localVariableIndex );

		if( wide )
			builder.append( "wide " );
		builder.append( OpCode.getOpCodeName( localVariableInstruction.model.opCode ) );
		if( flavor == -1 )
			builder.append( ' ' ).append( localVariableInstruction.localVariableIndex );
	}
}
