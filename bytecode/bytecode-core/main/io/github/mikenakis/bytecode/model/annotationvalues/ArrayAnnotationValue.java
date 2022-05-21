package io.github.mikenakis.bytecode.model.annotationvalues;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an array {@link AnnotationValue}.
 *
 * @author michael.gr
 */
public final class ArrayAnnotationValue extends AnnotationValue
{
	public static ArrayAnnotationValue read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<AnnotationValue> annotationValues = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			annotationValues.add( AnnotationValue.read( bufferReader, constantPool ) );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( annotationValues.size() );
		for( AnnotationValue annotationValue : annotationValues )
			annotationValue.write( bufferWriter, constantPool );
	}
}
