package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.kit.Kit;

import java.util.Map;

/**
 * {@link ByteCodeMethod} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeMethodPrinter extends ByteCodeMemberPrinter
{
	private static final Map<Integer,String> accessFlagNameMap = Map.ofEntries(
		//@formatter:off
		Map.entry( ByteCodeMethod.ACC_PUBLIC      , "public"        ),
		Map.entry( ByteCodeMethod.ACC_PRIVATE     , "private"       ),
		Map.entry( ByteCodeMethod.ACC_PROTECTED   , "protected"     ),
		Map.entry( ByteCodeMethod.ACC_STATIC      , "static"        ),
		Map.entry( ByteCodeMethod.ACC_FINAL       , "final"         ),
		Map.entry( ByteCodeMethod.ACC_SYNCHRONIZED, "synchronized"  ),
		Map.entry( ByteCodeMethod.ACC_BRIDGE      , "bridge"        ),
		Map.entry( ByteCodeMethod.ACC_VARARGS     , "varargs"       ),
		Map.entry( ByteCodeMethod.ACC_NATIVE      , "native"        ),
		Map.entry( ByteCodeMethod.ACC_ABSTRACT    , "abstract"      ),
		Map.entry( ByteCodeMethod.ACC_STRICT      , "strict"        ),
		Map.entry( ByteCodeMethod.ACC_SYNTHETIC   , "synthetic"     )
		//@formatter:on
	);

	public ByteCodeMethodPrinter( ByteCodeMethod byteCodeMethod )
	{
		super( byteCodeMethod );
	}

	@Override protected String getAccessFlagName( int accessFlag )
	{
		return Kit.map.get( accessFlagNameMap, accessFlag );
	}
}
