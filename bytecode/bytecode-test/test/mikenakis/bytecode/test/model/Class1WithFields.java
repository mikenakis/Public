package mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author michael.gr
 */
@SuppressWarnings( { "FieldMayBeStatic", "unused" } )
public abstract class Class1WithFields
{
	public static final String PublicStringConstant = "publicConstantValue";
	public static final double PublicDoubleConstant = 1.0;
	public static final float PublicFloatConstant = 2.0f;
	public static final long PublicLongConstant = 3L;
	public static final int PublicIntConstant = 4;

	private final Enum1 enumMember;
	private final boolean booleanMember = true;
	private final byte byteMember = 1;
	private final char charMember = 'c';
	private final double doubleMember = 0.5;
	private final float floatMember = 0.25f;
	private final int intMember = 2;
	private final long longMember = 3;
	private final short shortVar = 4;
	private final String stringMember = "x";
	private final int[] arrayOfPrimitiveMember = { 0 };
	private final String[] arrayOfObjectMember = { "" };

	protected Class1WithFields()
	{
		this( Enum1.ENUM_CONSTANT_A );
	}

	protected Class1WithFields( Enum1 enumMember )
	{
		this.enumMember = enumMember;
	}

	@Override public String toString()
	{
		return getClass().getName() + " " + enumMember;
	}
}
