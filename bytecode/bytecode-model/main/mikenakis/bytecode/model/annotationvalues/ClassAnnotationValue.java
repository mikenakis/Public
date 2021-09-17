package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a class {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassAnnotationValue extends AnnotationValue
{
	public static final String NAME = "class";

	public static ClassAnnotationValue of( String name )
	{
		Utf8Constant classConstant = Utf8Constant.of( name );
		return of( classConstant );
	}

	public static ClassAnnotationValue of( Utf8Constant classConstant )
	{
		return new ClassAnnotationValue( classConstant );
	}

	private final Utf8Constant classConstant;

	private ClassAnnotationValue( Utf8Constant classConstant )
	{
		super( ClassTag );
		this.classConstant = classConstant;
	}

	public Utf8Constant utf8Constant()
	{
		return classConstant;
	}

	@Deprecated @Override public ClassAnnotationValue asClassAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "class = " + classConstant;
	}
}
