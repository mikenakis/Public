package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;

/**
 * Represents the JVMS::CONSTANT_Double_info structure.
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

	@Override public int compareTo( Constant other )
	{
		int d = super.compareTo( other );
		if( d != 0 )
			return d;
		return value().compareTo( other.<T>asValueConstant().value() );
	}

	@Override public abstract int hashCode();
}
