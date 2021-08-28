package mikenakis.bytecode.attributes.stackmap.verification;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

/**
 * 'Object' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ObjectVerificationType extends VerificationType
{
	public static final Kind KIND = new Kind( 7, "Object" )
	{
		@Override public VerificationType newVerificationType( CodeAttribute codeAttribute, BufferReader bufferReader )
		{
			return new ObjectVerificationType( codeAttribute.constantPool, bufferReader );
		}
	};

	public final ClassConstant classConstant;

	public ObjectVerificationType( String className )
	{
		super( KIND );
		classConstant = new ClassConstant( className );
	}

	public ObjectVerificationType( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		classConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		classConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		super.toStringBuilder( builder );
		builder.append( "classConstant = " );
		classConstant.toStringBuilder( builder );
	}
}
