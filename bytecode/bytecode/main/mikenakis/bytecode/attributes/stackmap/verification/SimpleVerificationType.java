package mikenakis.bytecode.attributes.stackmap.verification;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.kit.BufferReader;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationType extends VerificationType
{
	public static final class Kind extends VerificationType.Kind
	{
		public Kind( int tag, String name )
		{
			super( tag, name );
		}

		@Override public VerificationType newVerificationType( CodeAttribute codeAttribute, BufferReader bufferReader )
		{
			return new SimpleVerificationType( this );
		}
	}

	//@formatter:off
	public static final Kind TOP_KIND                = new Kind( 0, "Top" );
	public static final Kind INTEGER_KIND            = new Kind( 1, "Integer" );
	public static final Kind FLOAT_KIND              = new Kind( 2, "Float" );
	public static final Kind DOUBLE_KIND             = new Kind( 3, "Double" );
	public static final Kind LONG_KIND               = new Kind( 4, "Long" );
	public static final Kind NULL_KIND               = new Kind( 5, "Null" );
	public static final Kind UNINITIALIZED_THIS_KIND = new Kind( 6, "UninitializedThis" );
	//@formatter:on

	public SimpleVerificationType( Kind kind )
	{
		super( kind );
		assert kind.tag >= 0 && kind.tag <= 6;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}
}
