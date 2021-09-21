package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "element_value_pair" of JVMS 4.7.16.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ElementValuePair
{
	public static ElementValuePair of( Mutf8Constant nameConstant, ElementValue elementValue )
	{
		return new ElementValuePair( nameConstant, elementValue );
	}

	private final Mutf8Constant nameConstant;
	private final ElementValue elementValue;

	private ElementValuePair( Mutf8Constant nameConstant, ElementValue elementValue )
	{
		this.nameConstant = nameConstant;
		this.elementValue = elementValue;
	}

	public Mutf8Constant nameConstant() { return nameConstant; }
	public ElementValue elementValue() { return elementValue; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", value = " + elementValue;
	}
}
