package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClassesAttribute extends KnownAttribute
{
	public static InnerClassesAttribute read( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedShort();
		assert count > 0;
		List<InnerClass> innerClasses = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant innerClassConstant = attributeReader.readIndexAndGetConstant().asClassConstant();
			Optional<ClassConstant> outerClassConstant = Kit.upCast( attributeReader.tryReadIndexAndGetConstant() );
			Optional<Mutf8ValueConstant> innerNameConstant = Kit.upCast( attributeReader.tryReadIndexAndGetConstant() );
			FlagSet<InnerClass.InnerClassModifier> modifiers = InnerClass.innerClassModifierFlagsEnum.fromBits( attributeReader.readUnsignedShort() );
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

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( innerClasses.size() );
		for( InnerClass innerClass : innerClasses )
			innerClass.write( constantWriter );
	}
}
