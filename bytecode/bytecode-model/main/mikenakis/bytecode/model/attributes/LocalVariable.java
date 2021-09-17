package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the {@link LocalVariableTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariable
{
	public static LocalVariable of( Instruction startInstruction, Optional<Instruction> endInstruction, String name, String descriptor, int index )
	{
		AbsoluteInstructionReference startInstructionReference = AbsoluteInstructionReference.of( Optional.of( startInstruction ) );
		AbsoluteInstructionReference endInstructionReference = AbsoluteInstructionReference.of( endInstruction );
		Utf8Constant nameConstant = Utf8Constant.of( name );
		Utf8Constant descriptorConstant = Utf8Constant.of( descriptor );
		return of( startInstructionReference, endInstructionReference, nameConstant, descriptorConstant, index );
	}

	public static LocalVariable of( AbsoluteInstructionReference startInstructionReference, AbsoluteInstructionReference endInstructionReference, //
		Utf8Constant nameConstant, Utf8Constant descriptorConstant, int index )
	{
		return new LocalVariable( startInstructionReference, endInstructionReference, nameConstant, descriptorConstant, index );
	}

	public final AbsoluteInstructionReference startInstructionReference;
	public final AbsoluteInstructionReference endInstructionReference;
	public final Utf8Constant nameConstant;
	public final Utf8Constant descriptorConstant;
	public final int index;

	private LocalVariable( AbsoluteInstructionReference startInstructionReference, AbsoluteInstructionReference endInstructionReference, //
		Utf8Constant nameConstant, Utf8Constant descriptorConstant, int index )
	{
		this.startInstructionReference = startInstructionReference;
		this.endInstructionReference = endInstructionReference;
		this.nameConstant = nameConstant;
		this.descriptorConstant = descriptorConstant;
		this.index = index;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + index + ", startInstruction = {" + startInstructionReference + "}, endInstruction = {" + endInstructionReference + "}" +
			", name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}
}
