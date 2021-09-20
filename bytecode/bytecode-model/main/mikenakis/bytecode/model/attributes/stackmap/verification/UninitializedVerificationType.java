package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.model.attributes.code.Instruction;

import java.util.Optional;

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

	public static final int tag = 8;
	public static final String tagName = "Uninitialized";

	public final Instruction instruction;

	private UninitializedVerificationType( Instruction instruction )
	{
		super( tag );
		this.instruction = instruction;
	}

	@Deprecated @Override public Optional<UninitializedVerificationType> tryAsUninitializedVerificationType() { return Optional.of( this ); }
}
