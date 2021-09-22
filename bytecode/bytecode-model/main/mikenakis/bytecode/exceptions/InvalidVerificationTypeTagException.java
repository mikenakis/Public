package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link VerificationType} Tag" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvalidVerificationTypeTagException extends UncheckedException
{
	public final int verificationTypeTag;

	public InvalidVerificationTypeTagException( int verificationTypeTag )
	{
		this.verificationTypeTag = verificationTypeTag;
	}
}
