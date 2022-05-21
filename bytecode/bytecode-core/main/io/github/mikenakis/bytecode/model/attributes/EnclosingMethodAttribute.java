package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.ByteCodeHelpers;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "EnclosingMethod" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author michael.gr
 */
public final class EnclosingMethodAttribute extends KnownAttribute
{
	public static EnclosingMethodAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		ClassConstant classConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = Kit.upCast( constantPool.tryGetConstant( bufferReader.readUnsignedShort() ) );
		return of( classConstant, methodNameAndDescriptorConstant );
	}

	public static EnclosingMethodAttribute of( ClassConstant enclosingClassConstant, Optional<NameAndDescriptorConstant> enclosingMethodNameAndDescriptorConstant )
	{
		return new EnclosingMethodAttribute( enclosingClassConstant, enclosingMethodNameAndDescriptorConstant );
	}

	private final ClassConstant enclosingClassConstant;
	private final Optional<NameAndDescriptorConstant> enclosingMethodNameAndDescriptorConstant;

	private EnclosingMethodAttribute( ClassConstant enclosingClassConstant, Optional<NameAndDescriptorConstant> enclosingMethodNameAndDescriptorConstant )
	{
		super( tag_EnclosingMethod );
		this.enclosingClassConstant = enclosingClassConstant;
		this.enclosingMethodNameAndDescriptorConstant = enclosingMethodNameAndDescriptorConstant;
	}

	public TerminalTypeDescriptor enclosingClassTypeDescriptor() { return enclosingClassConstant.terminalTypeDescriptor(); }
	public Optional<MethodPrototype> enclosingMethodPrototype() { return enclosingMethodNameAndDescriptorConstant.map( c -> ByteCodeHelpers.methodPrototypeFromNameAndDescriptorConstant( c ) ); }
	@Deprecated @Override public EnclosingMethodAttribute asEnclosingMethodAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "class = " + enclosingClassConstant + ", methodNameAndDescriptor = { " + enclosingMethodNameAndDescriptorConstant + " }"; }

	@Override public void intern( Interner interner )
	{
		enclosingClassConstant.intern( interner );
		enclosingMethodNameAndDescriptorConstant.ifPresent( c -> c.intern( interner ) );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( enclosingClassConstant ) );
		bufferWriter.writeUnsignedShort( enclosingMethodNameAndDescriptorConstant.map( c -> constantPool.getConstantIndex( c ) ).orElse( 0 ) );
	}
}
