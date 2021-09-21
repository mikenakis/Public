package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.ElementValue;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an array {@link ElementValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ArrayElementValue extends ElementValue
{
	public static final String NAME ="array";

	public static ArrayElementValue of()
	{
		return of( new ArrayList<>() );
	}

	public static ArrayElementValue of( List<ElementValue> annotationValues )
	{
		return new ArrayElementValue( annotationValues );
	}

	private final List<ElementValue> annotationValues;

	private ArrayElementValue( List<ElementValue> annotationValues )
	{
		super( Tag.Array );
		this.annotationValues = annotationValues;
	}

	public List<ElementValue> annotationValues()
	{
		return Collections.unmodifiableList( annotationValues );
	}

	@Deprecated @Override public ArrayElementValue asArrayAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return annotationValues.size() + " elements";
	}
}
