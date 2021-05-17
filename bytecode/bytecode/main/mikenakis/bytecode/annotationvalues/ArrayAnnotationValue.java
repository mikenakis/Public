package mikenakis.bytecode.annotationvalues;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents an array {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ArrayAnnotationValue extends AnnotationValue
{
	public static final Kind KIND = new Kind( '[', "array" )
	{
		@Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			return new ArrayAnnotationValue( observer, constantPool, bufferReader );
		}
	};

	public final List<AnnotationValue> annotationValues;

	@SuppressWarnings( "unused" ) public ArrayAnnotationValue( Runnable observer )
	{
		super( observer, KIND );
		annotationValues = newList( 0 );
	}

	public ArrayAnnotationValue( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, KIND );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		annotationValues = newList( count );
		for( int i = 0; i < count; i++ )
			annotationValues.add( AnnotationValue.parse( observer, constantPool, bufferReader ) );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( AnnotationValue annotationValue : annotationValues )
			annotationValue.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( annotationValues.size() );
		for( AnnotationValue annotationValue : annotationValues )
			annotationValue.write( constantPool, bufferWriter );
	}

	@Override public Optional<ArrayAnnotationValue> tryAsArrayAnnotationValue()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( annotationValues.size() ).append( " elements" );
	}

	private <E> List<E> newList( int capacity )
	{
		List<E> list = new ArrayList<>( capacity );
		return new ObservableList<>( list, this::markAsDirty );
	}
}
