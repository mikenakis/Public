package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the {@link LocalVariableTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTableEntry
{
	public static LocalVariableTableEntry of( Instruction startInstruction, Optional<Instruction> endInstruction, FieldPrototype variablePrototype, int variableIndex )
	{
		Mutf8Constant nameConstant = Mutf8Constant.of( variablePrototype.fieldName );
		Mutf8Constant descriptorConstant = Mutf8Constant.of( ByteCodeHelpers.descriptorStringFromTypeDescriptor( variablePrototype.descriptor.typeDescriptor ) );
		return of( startInstruction, endInstruction, nameConstant, descriptorConstant, variableIndex );
	}

	public static LocalVariableTableEntry of( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8Constant variableNameConstant, //
		Mutf8Constant variableTypeDescriptorStringConstant, int variableIndex )
	{
		return new LocalVariableTableEntry( startInstruction, endInstruction, variableNameConstant, variableTypeDescriptorStringConstant, variableIndex );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	public final Mutf8Constant variableNameConstant;
	public final Mutf8Constant variableTypeDescriptorStringConstant;
	public final int variableIndex;

	private LocalVariableTableEntry( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8Constant variableNameConstant, //
		Mutf8Constant variableTypeDescriptorStringConstant, int variableIndex )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.variableNameConstant = variableNameConstant;
		this.variableTypeDescriptorStringConstant = variableTypeDescriptorStringConstant;
		this.variableIndex = variableIndex;
	}

	public FieldPrototype prototype() { return FieldPrototype.of( variableNameConstant.stringValue(), ByteCodeHelpers.typeDescriptorFromDescriptorString( variableTypeDescriptorStringConstant.stringValue() ) ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + variableIndex + ", startInstruction = {" + startInstruction + "}, endInstruction = {" + endInstruction + "}" +
			", name = " + variableNameConstant + ", descriptor = " + variableTypeDescriptorStringConstant;
	}
}
