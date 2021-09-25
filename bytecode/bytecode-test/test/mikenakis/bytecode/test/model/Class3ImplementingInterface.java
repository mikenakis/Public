package mikenakis.bytecode.test.model;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * test class.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@SuppressWarnings( { "FieldCanBeLocal", "FieldMayBeStatic", "unused" } )
public abstract class Class3ImplementingInterface implements Interface1
{
	public static final String stringLiteral = "a\u03A6\uB414b"; //these characters are chosen so as to cover all unicode groups.
	public static final boolean booleanLiteral = true;
	public static final byte byteLiteral = 1;
	public static final char charLiteral = 'c';
	public static final double doubleLiteral = 0.5;
	public static final float floatLiteral = 0.25f;
	public static final int intLiteral = 2;
	public static final long longLiteral = 3;
	public static final short shortLiteral = 4;

	private final Enum1 enum1;
	private final boolean booleanMember = booleanLiteral;
	private final byte byteMember = byteLiteral;
	private final char charMember = charLiteral;
	private double doubleMember = doubleLiteral;
	private final float floatMember = floatLiteral;
	private final int intMember = intLiteral;
	private final long longMember = longLiteral;
	private final short shortMember = shortLiteral;
	private final String stringMember = stringLiteral;

	protected Class3ImplementingInterface()
	{
		this( Enum1.ENUM_CONSTANT_A );
	}

	protected Class3ImplementingInterface( Enum1 enum1 )
	{
		this.enum1 = enum1;
	}

	@Override public Enum1 getEnum1()
	{
		return enum1;
	}

	@Override public int getInteger( Class3ImplementingInterface class3ImplementingInterface )
	{
		//noinspection ConstantConditions
		return Collections.emptyList().size();
	}

	public final void setDouble( double d ) //method of TestInterface2
	{
		doubleMember = 5.0 * d;
	}

	public final boolean methodThatThrowsCheckedException() throws IOException
	{
		File tempFile = File.createTempFile( "", "" );
		return tempFile.exists();
	}

	public final File methodThatCatchesCheckedException()
	{
		File tempFile = null;
		try
		{
			tempFile = File.createTempFile( "", "" );
		}
		catch( IOException ignore )
		{
			System.out.println( getClass().getName() );
		}
		finally
		{
			if( tempFile != null )
				//noinspection ResultOfMethodCallIgnored
				tempFile.delete();
		}
		return tempFile;
	}

	@Override public String toString()
	{
		return getClass().getName() + " " + enum1;
	}

	public final Supplier<Integer> methodWithInnerClass( int i )
	{
		class Inner implements Supplier<Integer>
		{
			final int i;

			Inner( int i )
			{
				this.i = i;
			}

			@Override public Integer get()
			{
				return i;
			}
		}
		return new Inner( i );
	}

	@Deprecated public final void deprecatedMethod()
	{
		System.out.println( "deprecated" );
	}

	@SuppressWarnings( "EmptyMethod" ) public final void methodWithInvokeDynamic()
	{
		//TODO
	}
}
