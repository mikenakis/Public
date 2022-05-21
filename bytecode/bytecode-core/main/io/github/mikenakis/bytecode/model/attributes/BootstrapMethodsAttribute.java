package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.constants.MethodHandleConstant;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "BootstrapMethods" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author michael.gr
 */
public final class BootstrapMethodsAttribute extends KnownAttribute
{
	public static BootstrapMethodsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int bootstrapMethodCount = bufferReader.readUnsignedShort();
		assert bootstrapMethodCount > 0;
		List<BootstrapMethod> entries = new ArrayList<>( bootstrapMethodCount );
		for( int i = 0; i < bootstrapMethodCount; i++ )
		{
			MethodHandleConstant methodHandleConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMethodHandleConstant();
			int argumentConstantCount = bufferReader.readUnsignedShort();
			//assert argumentConstantCount > 0; has been observed to be zero in the case of a 'typeSwitch' that contained only a default clause.
			List<Constant> argumentConstants = new ArrayList<>( argumentConstantCount );
			for( int j = 0; j < argumentConstantCount; j++ )
			{
				Constant argumentConstant = constantPool.getConstant( bufferReader.readUnsignedShort() );
				argumentConstants.add( argumentConstant );
			}
			BootstrapMethod entry = BootstrapMethod.of( methodHandleConstant, argumentConstants );
			entries.add( entry );
		}
		return of( entries );
	}

	public static BootstrapMethodsAttribute of()
	{
		List<BootstrapMethod> entries = new ArrayList<>();
		return new BootstrapMethodsAttribute( entries );
	}

	public static BootstrapMethodsAttribute of( List<BootstrapMethod> entries )
	{
		return new BootstrapMethodsAttribute( entries );
	}

	public final List<BootstrapMethod> bootstrapMethods;

	private BootstrapMethodsAttribute( List<BootstrapMethod> bootstrapMethods )
	{
		super( tag_BootstrapMethods );
		this.bootstrapMethods = bootstrapMethods;
	}

	public int getIndexOfBootstrapMethod( BootstrapMethod entry )
	{
		assert entry != null;
		int index = bootstrapMethods.indexOf( entry );
		if( index == -1 )
		{
			index = bootstrapMethods.size();
			bootstrapMethods.add( entry );
		}
		return index;
	}

	public BootstrapMethod getBootstrapMethodByIndex( int index ) { return bootstrapMethods.get( index ); }
	@Deprecated @Override public BootstrapMethodsAttribute asBootstrapMethodsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return bootstrapMethods.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( BootstrapMethod bootstrapMethod : bootstrapMethods )
			bootstrapMethod.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( bootstrapMethods.size() );
		for( BootstrapMethod bootstrapMethod : bootstrapMethods )
			bootstrapMethod.write( bufferWriter, constantPool );
	}
}
