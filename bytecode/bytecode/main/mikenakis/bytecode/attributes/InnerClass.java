package mikenakis.bytecode.attributes;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.Optional;

/**
 * Represents an entry of {@link InnerClassesAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClass extends Printable
{
	//@formatter:off
	public static final int ACC_PUBLIC	    = 0x0001; // Marked or implicitly public in source.
	public static final int ACC_PRIVATE	    = 0x0002; // Marked private in source.
	public static final int ACC_PROTECTED   = 0x0004; // Marked protected in source.
	public static final int ACC_STATIC	    = 0x0008; // Marked or implicitly static in source.
	public static final int ACC_FINAL	    = 0x0010; // Marked final in source.
	public static final int ACC_INTERFACE   = 0x0200; // Was an interface in source.
	public static final int ACC_ABSTRACT    = 0x0400; // Marked or implicitly abstract in source.
	public static final int ACC_SYNTHETIC   = 0x1000; // Declared synthetic; not present in the source code.
	public static final int ACC_ANNOTATION  = 0x2000; // Declared as an annotation type.
	public static final int ACC_ENUM	    = 0x4000; // Declared as an enum type.
	//@formatter:on

	public final ClassConstant innerClassConstant;
	public final Optional<ClassConstant> outerClassConstant;
	public final Optional<Utf8Constant> innerNameConstant;
	public final int innerClassAccessFlags;

	public InnerClass( ConstantPool constantPool, ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Utf8Constant> innerNameConstant, int innerClassAccessFlags )
	{
		this.innerClassConstant = innerClassConstant;
		this.outerClassConstant = outerClassConstant;
		this.innerNameConstant = innerNameConstant;
		this.innerClassAccessFlags = innerClassAccessFlags;
	}

	public InnerClass( ConstantPool constantPool, BufferReader bufferReader )
	{
		innerClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		outerClassConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
		innerNameConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
		innerClassAccessFlags = bufferReader.readUnsignedShort();
	}

	public void intern( ConstantPool constantPool )
	{
		innerClassConstant.intern( constantPool );
		outerClassConstant.ifPresent( classConstant -> classConstant.intern( constantPool ) );
		innerNameConstant.ifPresent( utf8Constant -> utf8Constant.intern( constantPool ) );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		innerClassConstant.writeIndex( constantPool, bufferWriter );
		if( outerClassConstant.isEmpty() )
			bufferWriter.writeUnsignedShort( 0 );
		else
			outerClassConstant.get().writeIndex( constantPool, bufferWriter );
		if( innerNameConstant.isEmpty() )
			bufferWriter.writeUnsignedShort( 0 );
		else
			innerNameConstant.get().writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( innerClassAccessFlags );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "outerClass = " ).append( outerClassConstant );
		builder.append( " accessFlags = 0x" ).append( Integer.toHexString( innerClassAccessFlags ) );
		builder.append( " innerClass = " );
		innerClassConstant.toStringBuilder( builder );
		builder.append( " innerName = " ).append( innerNameConstant );
	}
}
