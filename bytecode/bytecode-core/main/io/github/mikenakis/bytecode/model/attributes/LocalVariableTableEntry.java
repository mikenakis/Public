package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.ByteCodeHelpers;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.model.descriptors.FieldPrototype;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the {@link LocalVariableTableAttribute}.
 *
 * @author michael.gr
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

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap )
	{
		int startLocation = locationMap.getLocation( startInstruction );
		int endLocation = locationMap.getLocation( endInstruction );
		bufferWriter.writeUnsignedShort( startLocation );
		bufferWriter.writeUnsignedShort( endLocation - startLocation );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( variableNameConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( variableTypeDescriptorStringConstant ) );
		bufferWriter.writeUnsignedShort( variableIndex );
	}
}
