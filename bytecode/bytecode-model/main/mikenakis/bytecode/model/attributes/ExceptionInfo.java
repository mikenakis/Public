package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "exception table" of the "code" attribute of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionInfo
{
	public static ExceptionInfo of( Instruction startInstruction, Optional<Instruction> endInstruction, //
		Instruction handlerInstruction, Optional<ClassConstant> catchTypeConstant )
	{
		return new ExceptionInfo( startInstruction, endInstruction, handlerInstruction, catchTypeConstant );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	public final Instruction handlerInstruction;
	public final Optional<ClassConstant> catchTypeConstant;

	private ExceptionInfo( Instruction startInstruction, Optional<Instruction> endInstruction, //
		Instruction handlerInstruction, Optional<ClassConstant> catchTypeConstant )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.handlerInstruction = handlerInstruction;
		this.catchTypeConstant = catchTypeConstant;
	}

	public Optional<TerminalTypeDescriptor> catchType() { return catchTypeConstant.map( c -> c.terminalTypeDescriptor() ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "start = {" + startInstruction + "}, end = {" + endInstruction + "}, handler = {" + handlerInstruction + "}" + (catchTypeConstant.isPresent()? ", catchType = " + catchTypeConstant : ""); }
}
