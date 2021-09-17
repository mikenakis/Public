package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;

import java.util.Optional;

/**
 * 'Uninitialized' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UninitializedVerificationType extends VerificationType
{
	public static UninitializedVerificationType of( AbsoluteInstructionReference instructionReference )
	{
		return new UninitializedVerificationType( instructionReference );
	}

	public static final int tag = 8;
	public static final String tagName = "Uninitialized";

	public final AbsoluteInstructionReference instructionReference;

	private UninitializedVerificationType( AbsoluteInstructionReference instructionReference )
	{
		super( tag );
		this.instructionReference = instructionReference;
	}

	@Deprecated @Override public Optional<UninitializedVerificationType> tryAsUninitializedVerificationType() { return Optional.of( this ); }
}
