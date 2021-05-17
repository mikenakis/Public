package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link InvokeInterfaceInstruction} {@link InstructionPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvokeInterfaceInstructionPrinter extends InstructionPrinter
{
	private final InvokeInterfaceInstruction invokeInterfaceInstruction;

	public InvokeInterfaceInstructionPrinter( InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		super( invokeInterfaceInstruction );
		this.invokeInterfaceInstruction = invokeInterfaceInstruction;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( OpCode.getOpCodeName( invokeInterfaceInstruction.model.opCode ) );
		builder.append( ' ' );
		renderingContext.newPrinter( invokeInterfaceInstruction.constant ).appendIndexTo( renderingContext, builder );
		builder.append( ' ' ).append( invokeInterfaceInstruction.argumentCount ).append( " arguments" );
	}
}
