package mikenakis.bytecode.test.model;

import java.util.ArrayList;
import java.util.List;

/**
 * test class.
 *
 * @author Michael Belivanakis (michael.gr)
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

	public final void methodWithTableSwitchInstruction( int i )
	{
		switch( i )
		{
			case 100:
				System.out.println( "100" );
				break;
			case 101:
				System.out.println( "101" );
				break;
			case 102:
				System.out.println( "102" );
				break;
			case 103:
				System.out.println( "103" );
				break;
			case 104:
				System.out.println( "104" );
				break;
			case 105:
				System.out.println( "105" );
				break;
			default:
				System.out.println( Class1.PublicConstant );
				break;
		}
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
				System.out.println( "default" );
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
}
