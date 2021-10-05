package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an array {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ArrayAnnotationValue extends AnnotationValue
{
	public static ArrayAnnotationValue read( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedShort();
		assert count > 0;
		List<AnnotationValue> annotationValues = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			annotationValues.add( AnnotationValue.read( attributeReader ) );
		return of( annotationValues );
	}

	public static final String NAME ="array";

	public static ArrayAnnotationValue of()
	{
		return of( new ArrayList<>() );
	}

	public static ArrayAnnotationValue of( List<AnnotationValue> annotationValues )
	{
		return new ArrayAnnotationValue( annotationValues );
	}

	public final List<AnnotationValue> annotationValues;

	private ArrayAnnotationValue( List<AnnotationValue> annotationValues )
	{
		super( tagArray );
		this.annotationValues = annotationValues;
	}

	@Deprecated @Override public ArrayAnnotationValue asArrayAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return annotationValues.size() + " elements"; }

	@Override public void intern( Interner interner )
	{
		for( AnnotationValue annotationValue : annotationValues )
			annotationValue.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeUnsignedShort( annotationValues.size() );
		for( AnnotationValue annotationValue : annotationValues )
			annotationValue.write( constantWriter );
	}
}
