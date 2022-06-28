package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.bytecode.model.attributes.KnownAttribute;
import io.github.mikenakis.kit.exceptions.UncheckedException;

/**
 * "Invalid {@link KnownAttribute} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidKnownAttributeTagException extends UncheckedException
{
	public final int knownAttributeTag;

	public InvalidKnownAttributeTagException( int knownAttributeTag )
	{
		this.knownAttributeTag = knownAttributeTag;
	}
}
