package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.kit.Kit;

/**
 * {@link LookupSwitchInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LookupSwitchInstructionPrinter extends InstructionPrinter
{
	private final LookupSwitchInstruction lookupSwitchInstruction;

	public LookupSwitchInstructionPrinter( LookupSwitchInstruction lookupSwitchInstruction )
	{
		super( lookupSwitchInstruction );
		this.lookupSwitchInstruction = lookupSwitchInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( lookupSwitchInstruction.model.opCode ) );
		builder.append( " default: " );
		renderingContext.newPrinter( lookupSwitchInstruction.defaultInstructionReference ).appendTo( renderingContext, builder );
		builder.append( " value-offset-pairs: [" );
		boolean first = true;
		for( LookupSwitchInstruction.Entry entry : lookupSwitchInstruction.entries )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			renderingContext.newPrinter( entry ).appendTo( renderingContext, builder );
		}
		builder.append( ']' );
	}
}
