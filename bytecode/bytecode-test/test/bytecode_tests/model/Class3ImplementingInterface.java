package bytecode_tests.model;

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
	private final Enum1 enum1;
	private final boolean booleanMember = true;
	private final byte byteMember = 1;
	private final char charMember = 'c';
	private double doubleMember = 0.5;
	private final float floatMember = 0.25f;
	private final int intMember = 2;
	private final long longMember = 3;
	private final short shortVar = 4;
	private final String stringMember = "a\u03A6\uB414b"; //these characters are chosen so as to cover all unicode groups.

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
