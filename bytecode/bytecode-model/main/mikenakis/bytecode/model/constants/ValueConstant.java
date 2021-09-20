package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;

import java.util.Comparator;

/**
 * Represents the JVMS::CONSTANT_Double_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ValueConstant<T extends Comparable<T>> extends Constant
{
	protected ValueConstant( Tag tag )
	{
		super( tag );
	}

	public abstract T value();

	@Deprecated @Override public final <TT extends Comparable<TT>> ValueConstant<TT> asValueConstant()
	{
		@SuppressWarnings( "unchecked" ) ValueConstant<TT> result = (ValueConstant<TT>)this;
		return result;
	}

	@Override protected Comparator<? extends Constant> comparator()
	{
		return comparator;
	}

	private static final Comparator<ValueConstant<?>> comparator = Comparator.comparing( ValueConstant::valueExtractor );

	private static <T extends Comparable<T>> T valueExtractor( ValueConstant<T> valueConstant )
	{
		return valueConstant.value();
	}

	@Override public abstract int hashCode();
}
