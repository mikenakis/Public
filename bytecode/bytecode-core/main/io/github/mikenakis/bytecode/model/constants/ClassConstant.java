package io.github.mikenakis.bytecode.model.constants;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.ByteCodeHelpers;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingBootstrapPool;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.java_type_model.ArrayTypeDescriptor;
import io.github.mikenakis.java_type_model.PrimitiveTypeDescriptor;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Class_info structure. See JVMS §4.4.1.
 *
 * @author michael.gr
 */
public final class ClassConstant extends Constant
{
	public static ClassConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int constantTag )
	{
		assert constantTag == tag_Class;
		ClassConstant classConstant = new ClassConstant();
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> classConstant.setInternalNameOrDescriptorStringConstant( c.asMutf8ValueConstant() ) );
		return classConstant;
	}

	public static ClassConstant of( TypeDescriptor typeDescriptor )
	{
		if( typeDescriptor.isPrimitive() )
			return of( typeDescriptor.asPrimitiveTypeDescriptor() );
		if( typeDescriptor.isArray() )
			return of( typeDescriptor.asArrayTypeDescriptor() );
		assert typeDescriptor.isTerminal();
		return of( typeDescriptor.asTerminalTypeDescriptor() );
	}

	public static ClassConstant of( PrimitiveTypeDescriptor primitiveTypeDescriptor )
	{
		return ofInternalNameOrDescriptorString( ByteCodeHelpers.descriptorStringFromPrimitiveTypeDescriptor( primitiveTypeDescriptor ) );
	}

	public static ClassConstant of( ArrayTypeDescriptor arrayTypeDescriptor )
	{
		return ofInternalNameOrDescriptorString( ByteCodeHelpers.descriptorStringFromArrayTypeDescriptor( arrayTypeDescriptor ) );
	}

	public static ClassConstant of( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		return ofInternalNameOrDescriptorString( ByteCodeHelpers.internalNameFromTerminalTypeDescriptor( terminalTypeDescriptor ) );
	}

	private static ClassConstant ofInternalNameOrDescriptorString( String internalName )
	{
		ClassConstant classConstant = new ClassConstant();
		classConstant.setInternalNameOrDescriptorStringConstant( Mutf8ValueConstant.of( internalName ) );
		return classConstant;
	}

	// According to §4.4.1 this is supposed to be a "binary class or interface name encoded in internal form."
	// PEARL: the term "binary name" here is a red herring, because binary names and internal names are different things.
	//        in class java.lang.constants.ConstantUtils there exist methods binaryToInternal() and internalToBinary().
	private Mutf8ValueConstant internalNameOrDescriptorStringConstant;

	private ClassConstant()
	{
		super( tag_Class );
	}

	public void setInternalNameOrDescriptorStringConstant( Mutf8ValueConstant internalNameOrDescriptorStringConstant )
	{
		assert this.internalNameOrDescriptorStringConstant == null;
		assert internalNameOrDescriptorStringConstant != null;
		assert isValidInternalNameOrDescriptorString( internalNameOrDescriptorStringConstant.stringValue() );
		this.internalNameOrDescriptorStringConstant = internalNameOrDescriptorStringConstant;
	}

	public TerminalTypeDescriptor terminalTypeDescriptor() { return ByteCodeHelpers.terminalTypeDescriptorFromInternalName( internalNameOrDescriptorStringConstant.stringValue() ); }
	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromInternalNameOrDescriptorString( internalNameOrDescriptorStringConstant.stringValue() ); }
	public ArrayTypeDescriptor arrayTypeDescriptor() { return ByteCodeHelpers.arrayTypeDescriptorFromDescriptorString( internalNameOrDescriptorStringConstant.stringValue() ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return internalNameOrDescriptorStringConstant == null? "(uninitialized)" : internalNameOrDescriptorStringConstant.stringValue(); }
	@Deprecated @Override public ClassConstant asClassConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof ClassConstant kin && equals( kin ); }
	public boolean equals( ClassConstant other ) { return internalNameOrDescriptorStringConstant.equals( other.internalNameOrDescriptorStringConstant ); }
	@Override public int hashCode() { return Objects.hash( tag, internalNameOrDescriptorStringConstant ); }
	public String internalNameOrDescriptorString() { return internalNameOrDescriptorStringConstant.stringValue(); }

	// PEARL: the vast majority of ClassConstant instances will contain strings of the form `java/lang/String`, but every once in a while you will find a string
	//        of the form `Ljava/lang/String;`.
	//        There is a cryptic sentence in §4.4.1 which kind of points to this, by saying: "Because arrays are objects, the opcodes anewarray and
	//        multianewarray - but not the opcode new - can reference array "classes" via CONSTANT_Class_info structures in the constant_pool table. For such
	//        array classes, the name of the class is the descriptor of the array type (§4.3.2)."
	//        However, even if we concede any meaningfulness to this sentence, it still does not cover all cases.
	//        An array-descriptor-style class constant can be encountered in other contexts.
	//        For example, an invokevirtual instruction has a method reference constant which has a class constant that specifies the declaring type, which is
	//        going to be an array type if the invokevirtual instruction is invoking `array.clone()`.
	private static boolean isValidInternalNameOrDescriptorString( String name )
	{
		if( name.charAt( 0 ) == '[' )
			return ByteCodeHelpers.isValidDescriptorString( name );
		return ByteCodeHelpers.isValidInternalName( name );
	}

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
		internalNameOrDescriptorStringConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		assert internalNameOrDescriptorStringConstant != null;
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( internalNameOrDescriptorStringConstant ) );
	}
}
