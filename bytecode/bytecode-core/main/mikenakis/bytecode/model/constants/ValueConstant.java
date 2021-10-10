package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;

/**
 * Common base class for value constants.
 *
 * @author michael.gr
 */
public abstract class ValueConstant extends Constant
{
	protected ValueConstant( int tag )
	{
		super( tag );
	}

	@Deprecated @Override public final ValueConstant asValueConstant() { return this; }
}
