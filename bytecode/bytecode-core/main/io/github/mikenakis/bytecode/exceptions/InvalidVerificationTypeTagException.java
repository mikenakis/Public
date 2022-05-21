package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import io.github.mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link VerificationType} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidVerificationTypeTagException extends UncheckedException
{
	public final int verificationTypeTag;

	public InvalidVerificationTypeTagException( int verificationTypeTag )
	{
		this.verificationTypeTag = verificationTypeTag;
	}
}
