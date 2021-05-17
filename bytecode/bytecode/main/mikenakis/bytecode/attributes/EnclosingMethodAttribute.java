package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.NameAndTypeConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.kit.Kit;

import java.util.Optional;

/**
 * Represents the "EnclosingMethod" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnclosingMethodAttribute extends Attribute
{
	public static final String NAME = "EnclosingMethod";

	public final ClassConstant classConstant;
	public final Optional<NameAndTypeConstant> methodNameAndTypeConstant;

	public EnclosingMethodAttribute( Runnable observer, ClassConstant classConstant, Optional<NameAndTypeConstant> methodNameAndTypeConstant )
	{
		super( observer, NAME );
		this.classConstant = classConstant;
		this.methodNameAndTypeConstant = methodNameAndTypeConstant;
	}

	public EnclosingMethodAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		methodNameAndTypeConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		classConstant.intern( constantPool );
		methodNameAndTypeConstant.ifPresent( nameAndTypeConstant -> nameAndTypeConstant.intern( constantPool ) );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		classConstant.writeIndex( constantPool, bufferWriter );
		if( methodNameAndTypeConstant.isEmpty() )
			bufferWriter.writeUnsignedShort( 0 );
		else
			methodNameAndTypeConstant.get().writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<EnclosingMethodAttribute> tryAsEnclosingMethodAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "class = " ).append( classConstant.getClassName() );
		builder.append( ", methodNameAndType = { " ).append( methodNameAndTypeConstant ).append( " }" );
	}
}
