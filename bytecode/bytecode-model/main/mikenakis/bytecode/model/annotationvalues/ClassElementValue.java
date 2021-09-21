package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.ElementValue;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;

/**
 * Represents a class {@link ElementValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassElementValue extends ElementValue
{
	public static final String NAME = "class";

	public static ClassElementValue of( String name )
	{
		Mutf8Constant classConstant = Mutf8Constant.of( name );
		return of( classConstant );
	}

	public static ClassElementValue of( Mutf8Constant nameConstant )
	{
		return new ClassElementValue( nameConstant );
	}

	private final Mutf8Constant nameConstant;

	private ClassElementValue( Mutf8Constant nameConstant )
	{
		super( Tag.Class );
		this.nameConstant = nameConstant;
	}

	public Mutf8Constant nameConstant() { return nameConstant; }
	public ClassDesc classDescriptor() { return ClassDesc.ofDescriptor( nameConstant.stringValue() ); }

	@Deprecated @Override public ClassElementValue asClassAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "class = " + nameConstant;
	}
}
