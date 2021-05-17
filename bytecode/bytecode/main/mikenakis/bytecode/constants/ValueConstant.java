package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Double_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ValueConstant<T extends Comparable<T>> extends Constant
{
	public abstract static class Kind<T extends Comparable<T>> extends ConstantKind
	{
		public final Class<T> javaClass;

		protected Kind( int tag, String name, Class<T> javaClass )
		{
			super( tag, name );
			this.javaClass = javaClass;
		}
	}

	protected ValueConstant( Kind<T> kind )
	{
		super( kind );
	}

	public abstract T getValue();

	@Deprecated @Override public final <TT extends Comparable<TT>> ValueConstant<TT> asValueConstant()
	{
		@SuppressWarnings( "unchecked" )
		ValueConstant<TT> result = (ValueConstant<TT>)this;
		return result;
	}

	@Override public final int hashCode()
	{
		return Objects.hash( kind, getValue() );
	}
}
