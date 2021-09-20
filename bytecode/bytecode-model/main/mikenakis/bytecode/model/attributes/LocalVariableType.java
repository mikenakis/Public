package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "local_variable_type_table[]" of a {@link LocalVariableTypeTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableType
{
	public static LocalVariableType of( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8Constant nameConstant, //
		Mutf8Constant signatureConstant, int index )
	{
		return new LocalVariableType( startInstruction, endInstruction, nameConstant, signatureConstant, index );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	public final Mutf8Constant nameConstant;
	public final Mutf8Constant signatureConstant; //this is a field type signature
	public final int index;

	private LocalVariableType( Instruction startInstruction, Optional<Instruction> endInstruction, Mutf8Constant nameConstant, //
		Mutf8Constant signatureConstant, int index )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.nameConstant = nameConstant;
		this.signatureConstant = signatureConstant;
		this.index = index;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + index + ", startInstruction = {" + startInstruction + "}, endInstruction = {" + endInstruction + "}" +
			", name = " + nameConstant + ", signature = " + signatureConstant;
	}
}
