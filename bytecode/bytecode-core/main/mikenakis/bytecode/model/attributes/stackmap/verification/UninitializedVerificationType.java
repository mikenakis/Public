package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;

/**
 * 'Uninitialized' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UninitializedVerificationType extends VerificationType
{
	public static UninitializedVerificationType read( CodeAttributeReader codeAttributeReader )
	{
		Instruction instruction = codeAttributeReader.readAbsoluteInstruction().orElseThrow();
		return of( instruction );
	}

	public static UninitializedVerificationType of( Instruction instruction )
	{
		return new UninitializedVerificationType( instruction );
	}

	public final Instruction instruction;

	private UninitializedVerificationType( Instruction instruction )
	{
		super( tag_Uninitialized );
		this.instruction = instruction;
	}

	@Deprecated @Override public UninitializedVerificationType asUninitializedVerificationType() { return this; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( CodeConstantWriter codeConstantWriter )
	{
		codeConstantWriter.writeUnsignedByte( tag );
		int targetLocation = codeConstantWriter.getLocation( instruction );
		codeConstantWriter.writeUnsignedShort( targetLocation );
	}
}
