package io.github.mikenakis.bytecode.model.annotationvalues;

import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.model.ByteCodeHelpers;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an enum {@link AnnotationValue}.
 *
 * @author michael.gr
 */
public final class EnumAnnotationValue extends AnnotationValue
{
	public static EnumAnnotationValue read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Mutf8ValueConstant typeNameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		Mutf8ValueConstant valueNameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		return of( typeNameConstant, valueNameConstant );
	}

	public static final String NAME = "enum";

	public static EnumAnnotationValue of( Mutf8ValueConstant enumClassDescriptorStringConstant, Mutf8ValueConstant enumValueNameConstant )
	{
		return new EnumAnnotationValue( enumClassDescriptorStringConstant, enumValueNameConstant );
	}

	private final Mutf8ValueConstant enumClassDescriptorStringConstant;
	private final Mutf8ValueConstant enumValueNameConstant;

	private EnumAnnotationValue( Mutf8ValueConstant enumClassDescriptorStringConstant, Mutf8ValueConstant enumValueNameConstant )
	{
		super( tagEnum );
		this.enumClassDescriptorStringConstant = enumClassDescriptorStringConstant;
		this.enumValueNameConstant = enumValueNameConstant;
	}

	public TerminalTypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( enumClassDescriptorStringConstant ).asTerminalTypeDescriptor(); }
	public String valueName() { return enumValueNameConstant.stringValue(); }
	@Deprecated @Override public EnumAnnotationValue asEnumAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + enumClassDescriptorStringConstant + ", value = " + enumValueNameConstant; }

	@Override public void intern( Interner interner )
	{
		enumClassDescriptorStringConstant.intern( interner );
		enumValueNameConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( enumClassDescriptorStringConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( enumValueNameConstant ) );
	}
}
