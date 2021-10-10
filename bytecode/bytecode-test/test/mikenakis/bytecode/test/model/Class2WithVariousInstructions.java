package mikenakis.bytecode.test.model;

import java.util.ArrayList;
import java.util.List;

/**
 * test class.
 *
 * @author michael.gr
 */
@SuppressWarnings( { "MethodMayBeStatic", "SpellCheckingInspection", "unused" } )
public abstract class Class2WithVariousInstructions
{
	public final int methodWithIincInstruction( int a )
	{
		a += 5;
		return a;
	}

	public final Object methodWithMultiANewArrayInstruction()
	{
		return new Object[4][5][];
	}

	public final void methodWithLookupSwitchInstruction( int i )
	{
		switch( i )
		{
			case 100:
				System.out.println( "100" );
				break;
			case 110:
				System.out.println( "110" );
				break;
			case 120:
				System.out.println( "120" );
				break;
			case 130:
				System.out.println( "130" );
				break;
			case 140:
				System.out.println( "140" );
				break;
			case 150:
				System.out.println( "150" );
				break;
			default:
				break;
		}
	}

	public final String methodWithSiPush()
	{
		return Integer.toString( 129 );
	}

	public final String methodWithLambda()
	{
		List<String> list = new ArrayList<>();
		list.add( "x" );
		list.sort( ( a, b ) -> -a.compareTo( b ) );
		return list.get( 0 );
	}

	static class Foo
	{
		final Foo[] foos;

		Foo( Foo... foos )
		{
			this.foos = foos;
		}
	}

	public final Foo methodWithUninitializedVerificationType( boolean b )
	{
		return new Foo( new Foo( b ? new Foo() : null ) );
	}
}
