package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.collections.FlagSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "MethodParameters" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author michael.gr
 */
public final class MethodParametersAttribute extends KnownAttribute
{
	public static MethodParametersAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedByte();
		assert count > 0;
		List<MethodParameter> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8ValueConstant nameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
			FlagSet<MethodParameter.Modifier> modifiers = MethodParameter.modifierEnum.fromBits( bufferReader.readUnsignedShort() );
			MethodParameter entry = MethodParameter.of( nameConstant, modifiers );
			entries.add( entry );
		}
		return of( entries );
	}

	public static MethodParametersAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static MethodParametersAttribute of( List<MethodParameter> methodParameters )
	{
		return new MethodParametersAttribute( methodParameters );
	}

	public final List<MethodParameter> methodParameters;

	private MethodParametersAttribute( List<MethodParameter> methodParameters )
	{
		super( tag_MethodParameters );
		this.methodParameters = methodParameters;
	}

	@Deprecated @Override public MethodParametersAttribute asMethodParametersAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return methodParameters.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( MethodParameter methodParameter : methodParameters )
			methodParameter.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedByte( methodParameters.size() );
		for( MethodParameter methodParameter : methodParameters )
			methodParameter.write( bufferWriter, constantPool );
	}
}
