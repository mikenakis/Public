package mikenakis.bytecode;

import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeAnnotation extends Printable
{
	public interface Factory
	{
		ByteCodeAnnotation create( Runnable observer );
	}

	private final Runnable observer;
	public final Utf8Constant nameConstant;
	private final LinkedHashMap<String,AnnotationParameter> annotationParameterFromNameMap = new LinkedHashMap<>();

	public ByteCodeAnnotation( Runnable observer, String name )
	{
		this.observer = observer;
		String descriptorName = ByteCodeHelpers.getDescriptorTypeNameFromJavaTypeName( name );
		nameConstant = new Utf8Constant( descriptorName );
	}

	public ByteCodeAnnotation( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		this.observer = observer;
		nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		int count = bufferReader.readUnsignedShort();
		for( int i = 0; i < count; i++ )
		{
			AnnotationParameter annotationParameter = new AnnotationParameter( this::markAsDirty, constantPool, bufferReader );
			Kit.map.add( annotationParameterFromNameMap, annotationParameter.nameConstant.getStringValue(), annotationParameter );
		}
	}

	private void markAsDirty()
	{
		observer.run();
	}

	public void intern( ConstantPool constantPool )
	{
		nameConstant.intern( constantPool );
		for( AnnotationParameter annotationParameter : annotationParameterFromNameMap.values() )
			annotationParameter.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		nameConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( annotationParameterFromNameMap.size() );
		for( AnnotationParameter annotationParameter : annotationParameterFromNameMap.values() )
			annotationParameter.write( constantPool, bufferWriter );
	}

	public String getName()
	{
		return ByteCodeHelpers.getJavaTypeNameFromDescriptorTypeName( nameConstant.getStringValue() );
	}

	public Collection<AnnotationParameter> getAnnotationParameters()
	{
		return annotationParameterFromNameMap.values();
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "name = " ).append( nameConstant.getStringValue() );
		builder.append( ", " ).append( annotationParameterFromNameMap.size() ).append( " entries" );
	}

	public Optional<AnnotationParameter> tryGetParameter( String name )
	{
		return Optional.ofNullable( Kit.map.tryGet( annotationParameterFromNameMap, name ) );
	}

	public AnnotationParameter putParameter( AnnotationParameter.Factory annotationParameterFactory )
	{
		AnnotationParameter annotationParameter = annotationParameterFactory.create( this::markAsDirty );
		Kit.map.addOrReplace( annotationParameterFromNameMap, annotationParameter.nameConstant.getStringValue(), annotationParameter );
		return annotationParameter;
	}
}
