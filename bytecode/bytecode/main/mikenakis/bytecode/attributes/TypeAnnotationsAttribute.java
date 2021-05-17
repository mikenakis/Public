package mikenakis.bytecode.attributes;

import mikenakis.bytecode.AnnotationParameter;
import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Common base class for the "RuntimeVisibleTypeAnnotations" or "RuntimeInvisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType} {@link ByteCodeMethod} {@link ByteCodeField} {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class TypeAnnotationsAttribute extends Attribute
{
	public final List<Entry> entries;

	protected TypeAnnotationsAttribute( Runnable observer, String name )
	{
		super( observer, name );
		entries = new ArrayList<>();
	}

	protected TypeAnnotationsAttribute( Runnable observer, ConstantPool constantPool, String name, BufferReader bufferReader )
	{
		super( observer, name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Entry entry = new Entry( this::markAsDirty, constantPool, bufferReader );
			entries.add( entry );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( Entry entry : entries )
			entry.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( entries.size() );
		for( Entry entry : entries )
			entry.write( constantPool, bufferWriter );
	}

	@Override public Optional<TypeAnnotationsAttribute> tryAsTypeAnnotationsAttribute()
	{
		return Optional.of( this );
	}

	@Override public final void toStringBuilder( StringBuilder builder )
	{
		builder.append( name ).append( ' ' ).append( entries.size() ).append( " entries" );
	}

	public static final class Entry extends Printable // "type_annotation" in jvms-4.7.20
	{
		public interface TargetParser
		{
			Target parse( ConstantPool constantPool, BufferReader bufferReader );
		}

		private static final Map<Integer,TargetParser> INTEGER_TARGET_PARSER_MAP = Map.ofEntries(
			Map.entry( 0x00, TypeParameterTarget::new ),
			Map.entry( 0x01, TypeParameterTarget::new ),
			Map.entry( 0x10, SupertypeTarget::new ),
			Map.entry( 0x11, TypeParameterBoundTarget::new ),
			Map.entry( 0x12, TypeParameterBoundTarget::new ),
			Map.entry( 0x13, EmptyTarget::new ),
			Map.entry( 0x14, EmptyTarget::new ),
			Map.entry( 0x15, EmptyTarget::new ),
			Map.entry( 0x16, FormalParameterTarget::new ),
			Map.entry( 0x17, ThrowsTarget::new ),
			Map.entry( 0x40, LocalVariableTarget::new ),
			Map.entry( 0x41, LocalVariableTarget::new ),
			Map.entry( 0x42, CatchTarget::new ),
			Map.entry( 0x43, OffsetTarget::new ),
			Map.entry( 0x44, OffsetTarget::new ),
			Map.entry( 0x45, OffsetTarget::new ),
			Map.entry( 0x46, OffsetTarget::new ),
			Map.entry( 0x47, TypeArgumentTarget::new ),
			Map.entry( 0x48, TypeArgumentTarget::new ),
			Map.entry( 0x49, TypeArgumentTarget::new ),
			Map.entry( 0x4a, TypeArgumentTarget::new ),
			Map.entry( 0x4b, TypeArgumentTarget::new )
		);
		private final Runnable observer;
		public final int targetType;
		public final Target target;
		public final TypePath targetPath;
		public final int typeIndex;
		public final List<ElementValuePair> elementValuePairs;

		public Entry( Runnable observer, int targetType, Target target, TypePath targetPath, int typeIndex, List<ElementValuePair> elementValuePairs )
		{
			this.observer = observer;
			this.targetType = targetType;
			this.target = target;
			this.targetPath = targetPath;
			this.typeIndex = typeIndex;
			this.elementValuePairs = elementValuePairs;
		}

		public Entry( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			this.observer = observer;
			targetType = bufferReader.readUnsignedByte();
			TargetParser targetParser = Kit.map.get( INTEGER_TARGET_PARSER_MAP, targetType );
			target = targetParser.parse( constantPool, bufferReader );
			targetPath = new TypePath( constantPool, bufferReader );
			typeIndex = bufferReader.readUnsignedShort();
			int count = bufferReader.readUnsignedShort();
			elementValuePairs = new ArrayList<>( count );
			for( int i = 0; i < count; i++ )
			{
				ElementValuePair elementValuePair = new ElementValuePair( this::markAsDirty, constantPool, bufferReader );
				elementValuePairs.add( elementValuePair );
			}
		}

		public void intern( ConstantPool constantPool )
		{
			for( ElementValuePair elementValuePair : elementValuePairs )
				elementValuePair.intern( constantPool );
		}

		public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedByte( targetType );
			target.write( constantPool, bufferWriter );
			targetPath.write( constantPool, bufferWriter );
			bufferWriter.writeUnsignedShort( typeIndex );
			bufferWriter.writeUnsignedShort( elementValuePairs.size() );
			for( ElementValuePair elementValuePair : elementValuePairs )
				elementValuePair.write( constantPool, bufferWriter );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "targetType = " ).append( String.format( "0x%02x ", targetType ) );
			builder.append( "targetPath = " );
			targetPath.toStringBuilder( builder );
			builder.append( "typeIndex = " ).append( typeIndex );
			builder.append( ", " ).append( elementValuePairs.size() ).append( " elementValuePairs" );
		}

		private void markAsDirty()
		{
			observer.run();
		}
	}

	public abstract static class Target extends Printable // "target_info" in jvms-4.7.20.1
	{
		public abstract void write( ConstantPool constantPool, BufferWriter bufferWriter );
	}

	public static final class TypeParameterTarget extends Target // "type_parameter_target" in jvms-4.7.20.1
	{
		public final int typeParameterIndex;

		TypeParameterTarget( int typeParameterIndex )
		{
			this.typeParameterIndex = typeParameterIndex;
		}

		TypeParameterTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			typeParameterIndex = bufferReader.readUnsignedByte();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedByte( typeParameterIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "typeParameterIndex = " ).append( typeParameterIndex );
		}
	}

	public static final class SupertypeTarget extends Target // "supertype_target" in jvms-4.7.20.1
	{
		public final int supertypeIndex;

		SupertypeTarget( int supertypeIndex )
		{
			this.supertypeIndex = supertypeIndex;
		}

		SupertypeTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			supertypeIndex = bufferReader.readUnsignedShort();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedByte( supertypeIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "superTypeIndex = " ).append( supertypeIndex );
		}
	}

	public static final class TypeParameterBoundTarget extends Target // "type_parameter_bound_target" in jvms-4.7.20.1
	{
		public final int typeParameterIndex;
		public final int boundIndex;

		TypeParameterBoundTarget( int typeParameterIndex, int boundIndex )
		{
			this.typeParameterIndex = typeParameterIndex;
			this.boundIndex = boundIndex;
		}

		TypeParameterBoundTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			typeParameterIndex = bufferReader.readUnsignedByte();
			boundIndex = bufferReader.readUnsignedByte();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedByte( typeParameterIndex );
			bufferWriter.writeUnsignedByte( boundIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "typeParameterIndex = " ).append( typeParameterIndex );
			builder.append( ", boundIndex = " ).append( boundIndex );
		}
	}

	public static final class EmptyTarget extends Target // "empty_target" in jvms-4.7.20.1
	{
		EmptyTarget()
		{
		}

		EmptyTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "(empty)" );
		}
	}

	public static final class FormalParameterTarget extends Target // "formal_parameter_target" in jvms-4.7.20.1
	{
		public final int formalParameterIndex;

		FormalParameterTarget( int formalParameterIndex )
		{
			this.formalParameterIndex = formalParameterIndex;
		}

		FormalParameterTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			formalParameterIndex = bufferReader.readUnsignedByte();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedByte( formalParameterIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "formalParameterIndex = " ).append( formalParameterIndex );
		}
	}

	public static final class ThrowsTarget extends Target // "throws_target" in jvms-4.7.20.1
	{
		public final int throwsTypeIndex;

		ThrowsTarget( int throwsTypeIndex )
		{
			this.throwsTypeIndex = throwsTypeIndex;
		}

		ThrowsTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			throwsTypeIndex = bufferReader.readUnsignedShort();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( throwsTypeIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "throwsTypeIndex = " ).append( throwsTypeIndex );
		}
	}

	public static final class LocalVariableTarget extends Target // "localvar_target" in jvms-4.7.20.1
	{
		public static final class Entry extends Printable
		{
			final int startPc;
			final int length;
			final int index;

			public Entry( int startPc, int length, int index )
			{
				this.startPc = startPc;
				this.length = length;
				this.index = index;
			}

			public Entry( ConstantPool constantPool, BufferReader bufferReader )
			{
				startPc = bufferReader.readUnsignedShort();
				length = bufferReader.readUnsignedShort();
				index = bufferReader.readUnsignedShort();
			}

			public void write( ConstantPool constantPool, BufferWriter bufferWriter )
			{
				bufferWriter.writeUnsignedShort( startPc );
				bufferWriter.writeUnsignedShort( length );
				bufferWriter.writeUnsignedShort( index );
			}

			@Override public void toStringBuilder( StringBuilder builder )
			{
				builder.append( "startPc = " ).append( startPc ).append( ", " );
				builder.append( "length = " ).append( length ).append( ", " );
				builder.append( "index = " ).append( index );
			}
		}

		public final List<Entry> entries;

		public LocalVariableTarget( List<Entry> entries )
		{
			this.entries = entries;
		}

		public LocalVariableTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			int count = bufferReader.readUnsignedShort();
			assert count > 0;
			entries = new ArrayList<>( count );
			for( int i = 0; i < count; i++ )
			{
				Entry entry = new Entry( constantPool, bufferReader );
				entries.add( entry );
			}
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( entries.size() );
			for( Entry entry : entries )
				entry.write( constantPool, bufferWriter );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( entries.size() ).append( " entries" );
		}
	}

	public static final class CatchTarget extends Target // "catch_target" in jvms-4.7.20.1
	{
		public final int exceptionTableIndex;

		CatchTarget( int exceptionTableIndex )
		{
			this.exceptionTableIndex = exceptionTableIndex;
		}

		CatchTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			exceptionTableIndex = bufferReader.readUnsignedShort();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( exceptionTableIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "exceptionTableIndex = " ).append( exceptionTableIndex );
		}
	}

	public static final class OffsetTarget extends Target
	{
		public final int offset;

		OffsetTarget( int offset )
		{
			this.offset = offset;
		}

		OffsetTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			offset = bufferReader.readUnsignedShort();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( offset );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "offset = " ).append( offset );
		}
	}

	public static final class TypeArgumentTarget extends Target // "type_argument_target" in jvms-4.7.20.1
	{
		public final int offset;
		public final int typeArgumentIndex;

		TypeArgumentTarget( int offset, int typeArgumentIndex )
		{
			this.offset = offset;
			this.typeArgumentIndex = typeArgumentIndex;
		}

		TypeArgumentTarget( ConstantPool constantPool, BufferReader bufferReader )
		{
			offset = bufferReader.readUnsignedShort();
			typeArgumentIndex = bufferReader.readUnsignedByte();
		}

		@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( offset );
			bufferWriter.writeUnsignedByte( typeArgumentIndex );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "offset = " ).append( offset );
			builder.append( ", typeArgumentIndex = " ).append( typeArgumentIndex );
		}
	}

	public static final class TypePath extends Printable // "type_path" in jvms-4.7.20.2
	{
		public static final class Entry extends Printable
		{
			public final int pathKind;
			public final int argumentIndex;

			public Entry( int pathKind, int argumentIndex )
			{
				this.pathKind = pathKind;
				this.argumentIndex = argumentIndex;
			}

			public Entry( ConstantPool constantPool, BufferReader bufferReader )
			{
				pathKind = bufferReader.readUnsignedByte();
				argumentIndex = bufferReader.readUnsignedByte();
			}

			public void write( ConstantPool constantPool, BufferWriter bufferWriter )
			{
				bufferWriter.writeUnsignedByte( pathKind );
				bufferWriter.writeUnsignedByte( argumentIndex );
			}

			@Override public void toStringBuilder( StringBuilder builder )
			{
				builder.append( "pathKind = " ).append( pathKind ).append( ", " );
				builder.append( "argumentIndex = " ).append( argumentIndex );
			}
		}

		public final List<Entry> entries;

		public TypePath( List<Entry> entries )
		{
			this.entries = entries;
		}

		public TypePath( ConstantPool constantPool, BufferReader bufferReader )
		{
			int count = bufferReader.readUnsignedByte();
			entries = new ArrayList<>( count );
			for( int i = 0; i < count; i++ )
			{
				Entry entry = new Entry( constantPool, bufferReader );
				entries.add( entry );
			}
		}

		public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedByte( entries.size() );
			for( Entry entry : entries )
				entry.write( constantPool, bufferWriter );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( entries.size() ).append( " entries" );
		}
	}

	public static final class ElementValuePair extends Printable
	{
		private final Runnable observer;
		final int elementNameIndex;
		final AnnotationParameter elementValue;

		public ElementValuePair( Runnable observer, int elementNameIndex, AnnotationParameter elementValue )
		{
			this.observer = observer;
			this.elementNameIndex = elementNameIndex;
			this.elementValue = elementValue;
		}

		public ElementValuePair( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			this.observer = observer;
			elementNameIndex = bufferReader.readUnsignedShort();
			elementValue = new AnnotationParameter( this::markAsDirty, constantPool, bufferReader );
		}

		public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( elementNameIndex );
			elementValue.write( constantPool, bufferWriter );
		}

		public void intern( ConstantPool constantPool )
		{
			elementValue.intern( constantPool );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( "elementNameIndex = " ).append( elementNameIndex );
			builder.append( ", elementValue = " );
			elementValue.toStringBuilder( builder );
		}

		private void markAsDirty()
		{
			observer.run();
		}
	}
}
