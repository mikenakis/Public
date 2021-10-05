package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;

/**
 * Common base class for value constants.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ValueConstant<T extends Comparable<T>> extends Constant
{
	protected ValueConstant( int tag )
	{
		super( tag );
	}

	public abstract T value();

	@Deprecated @Override public final <TT extends Comparable<TT>> ValueConstant<TT> asValueConstant()
	{
		@SuppressWarnings( "unchecked" ) ValueConstant<TT> result = (ValueConstant<TT>)this;
		return result;
	}

	@Override public abstract int hashCode();
}
