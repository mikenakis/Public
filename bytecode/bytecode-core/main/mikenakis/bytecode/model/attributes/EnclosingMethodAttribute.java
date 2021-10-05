package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "EnclosingMethod" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnclosingMethodAttribute extends KnownAttribute
{
	public static EnclosingMethodAttribute read( AttributeReader attributeReader )
	{
		ClassConstant classConstant = attributeReader.readIndexAndGetConstant().asClassConstant();
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = Kit.upCast( attributeReader.tryReadIndexAndGetConstant() );
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

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( enclosingClassConstant ) );
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = enclosingMethodNameAndDescriptorConstant;
		constantWriter.writeUnsignedShort( methodNameAndDescriptorConstant.map( c -> constantWriter.getConstantIndex( c ) ).orElse( 0 ) );
	}
}
