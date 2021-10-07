package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "local_variable_type_table[]" of a {@link LocalVariableTypeTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypeTableEntry
{
	public static LocalVariableTypeTableEntry read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap )
	{
		Instruction startInstruction = locationMap.getInstruction( bufferReader.readUnsignedShort() ).orElseThrow();
		int length = bufferReader.readUnsignedShort();
		int endLocation = locationMap.getLocation( startInstruction ) + length;
		Optional<Instruction> endInstruction = locationMap.getInstruction( endLocation );
		Mutf8ValueConstant nameConstant1 = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		Mutf8ValueConstant signatureConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		int index = bufferReader.readUnsignedShort();
		return of( startInstruction, endInstruction, nameConstant1, signatureConstant, index );
	}

	public static LocalVariableTypeTableEntry of( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8ValueConstant nameConstant, //
		Mutf8ValueConstant signatureConstant, int index )
	{
		return new LocalVariableTypeTableEntry( startInstruction, endInstruction, nameConstant, signatureConstant, index );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	private final Mutf8ValueConstant nameConstant;
	private final Mutf8ValueConstant signatureConstant;
	public final int index;

	private LocalVariableTypeTableEntry( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8ValueConstant nameConstant, //
		Mutf8ValueConstant signatureConstant, int index )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.nameConstant = nameConstant;
		this.signatureConstant = signatureConstant;
		this.index = index;
	}

	public String variableName() { return nameConstant.stringValue(); }
	public String signatureString() { return signatureConstant.stringValue(); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + index + ", startInstruction = {" + startInstruction + "}, endInstruction = {" + endInstruction + "}" +
			", name = " + nameConstant + ", signature = " + signatureConstant;
	}

	public void intern( Interner interner )
	{
		nameConstant.intern( interner );
		signatureConstant.intern( interner );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap )
	{
		int startLocation = locationMap.getLocation( startInstruction );
		int endLocation = locationMap.getLocation( endInstruction );
		bufferWriter.writeUnsignedShort( startLocation );
		bufferWriter.writeUnsignedShort( endLocation - startLocation );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( nameConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( signatureConstant ) );
		bufferWriter.writeUnsignedShort( index );
	}
}
