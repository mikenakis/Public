package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "InnerClasses" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author michael.gr
 */
public final class InnerClassesAttribute extends KnownAttribute
{
	public static InnerClassesAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<InnerClass> innerClasses = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant innerClassConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
			Optional<ClassConstant> outerClassConstant = Kit.upCast( constantPool.tryGetConstant( bufferReader.readUnsignedShort() ) );
			Optional<Mutf8ValueConstant> innerNameConstant = Kit.upCast( constantPool.tryGetConstant( bufferReader.readUnsignedShort() ) );
			FlagSet<InnerClass.InnerClassModifier> modifiers = InnerClass.innerClassModifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			InnerClass innerClass = InnerClass.of( innerClassConstant, outerClassConstant, innerNameConstant, modifiers );
			innerClasses.add( innerClass );
		}
		return of( innerClasses );
	}

	public static InnerClassesAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static InnerClassesAttribute of( List<InnerClass> innerClasses )
	{
		return new InnerClassesAttribute( innerClasses );
	}

	public final List<InnerClass> innerClasses;

	private InnerClassesAttribute( List<InnerClass> innerClasses )
	{
		super( tag_InnerClasses );
		this.innerClasses = innerClasses;
	}

	@Deprecated @Override public InnerClassesAttribute asInnerClassesAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return innerClasses.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( InnerClass innerClass : innerClasses )
			innerClass.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( innerClasses.size() );
		for( InnerClass innerClass : innerClasses )
			innerClass.write( bufferWriter, constantPool );
	}
}
