package mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@SuppressWarnings( { "FieldMayBeStatic", "unused" } )
public abstract class Class1
{
	public static final String PublicConstant = "publicConstantValue";

	private final Enum1 enum1;
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

	protected Class1()
	{
		this( Enum1.ENUM_CONSTANT_A );
	}

	protected Class1( Enum1 enum1 )
	{
		this.enum1 = enum1;
	}

	@Override public String toString()
	{
		return getClass().getName() + " " + enum1;
	}
}