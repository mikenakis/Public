package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.model.attributes.code.Instruction;

/**
 * 'Uninitialized' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UninitializedVerificationType extends VerificationType
{
	public static UninitializedVerificationType of( Instruction instruction )
	{
		return new UninitializedVerificationType( instruction );
	}

	public final Instruction instruction;

	private UninitializedVerificationType( Instruction instruction )
	{
		super( Tag.Uninitialized );
		this.instruction = instruction;
	}

	@Deprecated @Override protected UninitializedVerificationType asUninitializedVerificationType() { return this; }
}
