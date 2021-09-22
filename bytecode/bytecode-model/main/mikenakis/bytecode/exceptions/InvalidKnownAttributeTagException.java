package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link KnownAttribute} Tag" exception.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvalidKnownAttributeTagException extends UncheckedException
{
	public final int knownAttributeTag;

	public InvalidKnownAttributeTagException( int knownAttributeTag )
	{
		this.knownAttributeTag = knownAttributeTag;
	}
}
