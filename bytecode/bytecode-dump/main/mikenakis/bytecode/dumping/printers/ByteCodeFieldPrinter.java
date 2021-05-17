package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.kit.Kit;

import java.util.Map;

/**
 * {@link ByteCodeField} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeFieldPrinter extends ByteCodeMemberPrinter
{
	private static final Map<Integer,String> accessFlagNameMap = Map.ofEntries(
		//@formatter:off
		Map.entry( ByteCodeField.ACC_PUBLIC   , "public"    ),
		Map.entry( ByteCodeField.ACC_PRIVATE  , "private"   ),
		Map.entry( ByteCodeField.ACC_PROTECTED, "protected" ),
		Map.entry( ByteCodeField.ACC_STATIC   , "static"    ),
		Map.entry( ByteCodeField.ACC_FINAL    , "final"     ),
		Map.entry( ByteCodeField.ACC_VOLATILE , "volatile"  ),
		Map.entry( ByteCodeField.ACC_TRANSIENT, "transient" ),
		Map.entry( ByteCodeField.ACC_SYNTHETIC, "synthetic" ),
		Map.entry( ByteCodeField.ACC_ENUM     , "enum"      )
		//@formatter:on
	);

	public ByteCodeFieldPrinter( ByteCodeField byteCodeField )
	{
		super( byteCodeField );
	}

	@Override protected String getAccessFlagName( int accessFlag )
	{
		return Kit.map.get( accessFlagNameMap, accessFlag );
	}
}
