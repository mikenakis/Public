package io.github.mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author michael.gr
 */
@SuppressWarnings( { "MethodMayBeStatic", "SpellCheckingInspection", "unused" } )
public abstract class Class2WithTableswitch
{
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
				/* Excercise the "ExtraConstants" feature:
				   Here we make use of constant (public static final) fields from other classes,
				   without referencing these other classes in any other way.
				   The compiler does not emit any code that references the fields of the other classes;
				   instead, it copies the values of these fields verbatim into the current class.
				   However, (probably in order to facilitate dependency checking,)
				   the compiler does emit a class constant referring to each class from which a constant was copied.
				   This "extra" class constant might not be used in any other way in this class file. */
				System.out.println( Class1WithFields.PublicStringConstant + Class3ImplementingInterface.stringLiteral );
				break;
		}
	}

	protected Class2WithTableswitch()
	{
	}
}
