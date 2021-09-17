package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an array {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ArrayAnnotationValue extends AnnotationValue
{
	public static final String NAME ="array";

	public static ArrayAnnotationValue of()
	{
		return of( new ArrayList<>() );
	}

	public static ArrayAnnotationValue of( List<AnnotationValue> annotationValues )
	{
		return new ArrayAnnotationValue( annotationValues );
	}

	private final List<AnnotationValue> annotationValues;

	private ArrayAnnotationValue( List<AnnotationValue> annotationValues )
	{
		super( ArrayTag );
		this.annotationValues = annotationValues;
	}

	public List<AnnotationValue> annotationValues()
	{
		return Collections.unmodifiableList( annotationValues );
	}

	@Deprecated @Override public ArrayAnnotationValue asArrayAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return annotationValues.size() + " elements";
	}
}