package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
		Mutf8ValueConstant nameConstant = Mutf8ValueConstant.of( variablePrototype.fieldName );
		Mutf8ValueConstant descriptorConstant = Mutf8ValueConstant.of( ByteCodeHelpers.descriptorStringFromTypeDescriptor( variablePrototype.descriptor.typeDescriptor ) );
		return of( startInstruction, endInstruction, nameConstant, descriptorConstant, variableIndex );
	}

	public static LocalVariableTableEntry of( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8ValueConstant variableNameConstant, //
		Mutf8ValueConstant variableTypeDescriptorStringConstant, int variableIndex )
	{
		return new LocalVariableTableEntry( startInstruction, endInstruction, variableNameConstant, variableTypeDescriptorStringConstant, variableIndex );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	private final Mutf8ValueConstant variableNameConstant;
	private final Mutf8ValueConstant variableTypeDescriptorStringConstant;
	public final int variableIndex;

	private LocalVariableTableEntry( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8ValueConstant variableNameConstant, //
		Mutf8ValueConstant variableTypeDescriptorStringConstant, int variableIndex )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.variableNameConstant = variableNameConstant;
		this.variableTypeDescriptorStringConstant = variableTypeDescriptorStringConstant;
		this.variableIndex = variableIndex;
	}

	public FieldPrototype prototype() { return FieldPrototype.of( variableNameConstant.stringValue(), ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( variableTypeDescriptorStringConstant ) ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + variableIndex + ", startInstruction = {" + startInstruction + "}, endInstruction = {" + endInstruction + "}" +
			", name = " + variableNameConstant + ", descriptor = " + variableTypeDescriptorStringConstant;
	}

	public void intern( Interner interner )
	{
		variableNameConstant.intern( interner );
		variableTypeDescriptorStringConstant.intern( interner );
	}

	public void write( CodeConstantWriter codeConstantWriter )
	{
		int startLocation = codeConstantWriter.getLocation( startInstruction );
		int endLocation = codeConstantWriter.getLocation( endInstruction );
		codeConstantWriter.writeUnsignedShort( startLocation );
		codeConstantWriter.writeUnsignedShort( endLocation - startLocation );
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getConstantIndex( variableNameConstant ) );
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getConstantIndex( variableTypeDescriptorStringConstant ) );
		codeConstantWriter.writeUnsignedShort( variableIndex );
	}
}
