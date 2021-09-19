package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an entry of the "local_variable_type_table[]" of a {@link LocalVariableTypeTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableType
{
	public static LocalVariableType of( AbsoluteInstructionReference startInstructionReference, AbsoluteInstructionReference endInstructionReference, //
		Mutf8Constant nameConstant, Mutf8Constant signatureConstant, int index )
	{
		return new LocalVariableType( startInstructionReference, endInstructionReference, nameConstant, signatureConstant, index );
	}

	public final AbsoluteInstructionReference startInstructionReference;
	public final AbsoluteInstructionReference endInstructionReference;
	public final Mutf8Constant nameConstant;
	public final Mutf8Constant signatureConstant; //this is a field type signature
	public final int index;

	private LocalVariableType( AbsoluteInstructionReference startInstructionReference, AbsoluteInstructionReference endInstructionReference, //
		Mutf8Constant nameConstant, Mutf8Constant signatureConstant, int index )
	{
		this.startInstructionReference = startInstructionReference;
		this.endInstructionReference = endInstructionReference;
		this.nameConstant = nameConstant;
		this.signatureConstant = signatureConstant;
		this.index = index;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "index = " + index + ", startInstruction = {" + startInstructionReference + "}, endInstruction = {" + endInstructionReference + "}" +
			", name = " + nameConstant + ", signature = " + signatureConstant;
	}
}
