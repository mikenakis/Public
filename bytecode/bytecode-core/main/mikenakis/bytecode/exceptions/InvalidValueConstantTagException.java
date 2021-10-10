package mikenakis.bytecode.exceptions;

import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.UncheckedException;

/**
 * "Invalid {@link ValueConstant} Tag" exception.
 *
 * @author michael.gr
 */
public final class InvalidValueConstantTagException extends UncheckedException
{
	public final int valueConstantTag;

	public InvalidValueConstantTagException( int valueConstantTag )
	{
		this.valueConstantTag = valueConstantTag;
	}
}
