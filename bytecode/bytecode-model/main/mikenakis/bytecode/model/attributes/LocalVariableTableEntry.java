package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.util.Optional;

/**
 * Represents an entry of the {@link LocalVariableTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTableEntry
{
	public static LocalVariableTableEntry of( Instruction startInstruction, Optional<Instruction> endInstruction, String name, String descriptor, int index )
	{
		Mutf8Constant nameConstant = Mutf8Constant.of( name );
		Mutf8Constant descriptorConstant = Mutf8Constant.of( descriptor );
		return of( startInstruction, endInstruction, nameConstant, descriptorConstant, index );
	}

	public static LocalVariableTableEntry of( Instruction startInstruction, Optional<Instruction> endInstruction, //
		Mutf8Constant nameConstant, Mutf8Constant descriptorConstant, int index )
	{
		return new LocalVariableTableEntry( startInstruction, endInstruction, nameConstant, descriptorConstant, index );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	public final Mutf8Constant nameConstant;
	public final Mutf8Constant descriptorConstant;
	public final int index;

	private LocalVariableTableEntry( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8Constant nameConstant, //
		Mutf8Constant descriptorConstant, int index )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.nameConstant = nameConstant;
		this.descriptorConstant = descriptorConstant;
		this.index = index;
	}

	public String name() { return nameConstant.stringValue(); }
	public ClassDesc descriptor() {	return ClassDesc.ofDescriptor( descriptorConstant.stringValue() ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + index + ", startInstruction = {" + startInstruction + "}, endInstruction = {" + endInstruction + "}" +
			", name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}
}
