package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "local_variable_type_table[]" of a {@link LocalVariableTypeTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypeTableEntry
{
	public static LocalVariableTypeTableEntry read( CodeAttributeReader codeAttributeReader )
	{
		Instruction startInstruction = codeAttributeReader.readAbsoluteInstruction().orElseThrow();
		int length = codeAttributeReader.readUnsignedShort();
		int endLocation = codeAttributeReader.locationMap.getLocation( startInstruction ) + length;
		Optional<Instruction> endInstruction = codeAttributeReader.locationMap.getInstruction( endLocation );
		Mutf8ValueConstant nameConstant1 = codeAttributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		Mutf8ValueConstant signatureConstant = codeAttributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		int index = codeAttributeReader.readUnsignedShort();
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

	public void write( CodeConstantWriter codeConstantWriter )
	{
		int startLocation = codeConstantWriter.getLocation( startInstruction );
		int endLocation = codeConstantWriter.getLocation( endInstruction );
		codeConstantWriter.writeUnsignedShort( startLocation );
		codeConstantWriter.writeUnsignedShort( endLocation - startLocation );
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getConstantIndex( nameConstant ) );
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getConstantIndex( signatureConstant ) );
		codeConstantWriter.writeUnsignedShort( index );
	}
}
