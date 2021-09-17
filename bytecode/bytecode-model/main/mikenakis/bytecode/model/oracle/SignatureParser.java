package mikenakis.bytecode.model.oracle;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.List;

public class SignatureParser
{
	private String input; // the input signature
	private int index;    // index into the input
	private int mark;     // index of mark

	// prepares parser for new parsing session
	private void init( String s )
	{
		input = s;
		mark = index = 0;
	}

	// Utility methods.

	// Most parsing routines use the following routines to access the
	// input stream, and advance it as necessary.
	// This makes it easy to adapt the parser to operate on streams
	// of various kinds as well as strings.

	// returns current element of the input
	private char current()
	{
		assert index <= input.length();
		return index < input.length() ? input.charAt( index ) : '\0'; //FIXME fix this hack!
	}

	// advance the input
	private void advance()
	{
		assert index <= input.length();
		if( index < input.length() )
			index++;
	}

	// mark current position
	private void mark()
	{
		mark = index;
	}

	// returns a substring of input from mark (inclusive)
	// to current position (exclusive)
	private String markToCurrent()
	{
		return input.substring( mark, index );
	}

	/**
	 * Static factory method. Produces a parser instance.
	 *
	 * @return an instance of {@code SignatureParser}
	 */
	public static SignatureParser make()
	{
		return new SignatureParser();
	}

	/**
	 * Parses a class signature (as defined in the JVMS, chapter 4)
	 * and produces an abstract syntax tree representing it.
	 *
	 * @param s a string representing the input class signature
	 *
	 * @return An abstract syntax tree for a class signature
	 * 	corresponding to the input string
	 *
	 * @throws GenericSignatureFormatError if the input is not a valid
	 *                                     class signature
	 */
	public ClassSignature parseClassSig( String s )
	{
		init( s );
		return parseClassSignature();
	}

	/**
	 * Parses a field type signature.
	 */
	public ObjectSignature parseFieldSig( String s )
	{
		init( s );
		return parseFieldTypeSignature();
	}

	/**
	 * Parses a method signature (as defined in the JVMS, chapter 4)
	 * and produces an abstract syntax tree representing it.
	 *
	 * @param s a string representing the input method signature
	 *
	 * @return An abstract syntax tree for a method signature
	 * 	corresponding to the input string
	 *
	 * @throws GenericSignatureFormatError if the input is not a valid method signature
	 */
	public MethodTypeSignature parseMethodSig( String s )
	{
		init( s );
		return parseMethodTypeSignature();
	}
	//
	//
	//    /**
	//     * Parses a type signature
	//     * and produces an abstract syntax tree representing it.
	//     *
	//     * @param s a string representing the input type signature
	//     * @return An abstract syntax tree for a type signature
	//     * corresponding to the input string
	//     * @throws GenericSignatureFormatError if the input is not a valid
	//     * type signature
	//     */
	//    public TypeSignature parseTypeSig(String s) {
	//        if (DEBUG) System.out.println("Parsing type sig:" + s);
	//        init(s);
	//        return parseTypeSignature();
	//    }
	//
	//    // Parsing routines.
	//    // As a rule, the parsing routines access the input using the
	//    // utilities current() and advance().
	//    // The convention is that when a parsing routine is invoked
	//    // it expects the current input to be the first character it should parse
	//    // and when it completes parsing, it leaves the input at the first
	//    // character after the input parses.
	//
	//    /*
	//     * Note on grammar conventions: a trailing "*" matches zero or
	//     * more occurrences, a trailing "+" matches one or more occurrences,
	//     * "_opt" indicates an optional component.
	//     */

	/**
	 * ClassSignature:
	 * FormalTypeParameters_opt SuperclassSignature SuperinterfaceSignature*
	 */
	private ClassSignature parseClassSignature()
	{
		// parse a class signature based on the implicit input.
		assert index == 0;
		return ClassSignature.make( parseZeroOrMoreFormalTypeParameters(), parseClassTypeSignature(), // Only rule for SuperclassSignature
			parseSuperInterfaces() );
	}

	private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters()
	{
		if( current() == '<' )
			return parseFormalTypeParameters();
		else
			return new FormalTypeParameter[0];
	}

	/**
	 * FormalTypeParameters:
	 * "<" FormalTypeParameter+ ">"
	 */
	private FormalTypeParameter[] parseFormalTypeParameters()
	{
		assert current() == '<';
		advance();
		List<FormalTypeParameter> ftps = new ArrayList<>( 3 );
		ftps.add( parseFormalTypeParameter() );
		while( current() != '>' )
		{
			int startingPosition = index;
			ftps.add( parseFormalTypeParameter() );
			assert index > startingPosition;
		}
		advance();
		return ftps.toArray( new FormalTypeParameter[0] );
	}

	/**
	 * FormalTypeParameter:
	 * Identifier ClassBound InterfaceBound*
	 */
	private FormalTypeParameter parseFormalTypeParameter()
	{
		String id = parseIdentifier();
		ObjectSignature[] bs = parseBounds();
		return FormalTypeParameter.make( id, bs );
	}

	private String parseIdentifier()
	{
		mark();
		skipIdentifier();
		return markToCurrent();
	}

	private void skipIdentifier()
	{
		char c = current();
		while( c != ';' && c != '.' && c != '/' && c != '[' && c != ':' && c != '>' && c != '<' && !Character.isWhitespace( c ) )
		{
			advance();
			c = current();
		}
	}

	/**
	 * FieldTypeSignature:
	 * ClassTypeSignature
	 * ArrayTypeSignature
	 * TypeVariableSignature
	 */
	private ObjectSignature parseFieldTypeSignature()
	{
		return parseFieldTypeSignature( true );
	}

	private ObjectSignature parseFieldTypeSignature( boolean allowArrays )
	{
		switch( current() )
		{
			case 'L':
				return parseClassTypeSignature();
			case 'T':
				return parseTypeVariableSignature();
			case '[':
				assert allowArrays;
				return parseArrayTypeSignature();
			default:
				throw new AssertionError();
		}
	}

	/**
	 * ClassTypeSignature:
	 * "L" PackageSpecifier_opt SimpleClassTypeSignature ClassTypeSignatureSuffix* ";"
	 */
	private ClassTypeSignature parseClassTypeSignature()
	{
		assert current() == 'L';
		advance();
		List<SimpleClassTypeSignature> scts = new ArrayList<>( 5 );
		scts.add( parsePackageNameAndSimpleClassTypeSignature() );
		parseClassTypeSignatureSuffix( scts );
		assert current() == ';';
		advance();
		return ClassTypeSignature.make( scts );
	}

	/**
	 * PackageSpecifier:
	 * Identifier "/" PackageSpecifier*
	 */
	private SimpleClassTypeSignature parsePackageNameAndSimpleClassTypeSignature()
	{
		// Parse both any optional leading PackageSpecifier as well as
		// the following SimpleClassTypeSignature.

		mark();
		skipIdentifier();
		while( current() == '/' )
		{
			advance();
			skipIdentifier();
		}
		String id = markToCurrent().replace( '/', '.' );
		switch( current() )
		{
			case ';':
				return SimpleClassTypeSignature.make( id, false, new TypeArgument[0] ); // all done!
			case '<':
				return SimpleClassTypeSignature.make( id, false, parseTypeArguments() );
			default:
				throw new AssertionError();
		}
	}

	/**
	 * SimpleClassTypeSignature:
	 * Identifier TypeArguments_opt
	 */
	private SimpleClassTypeSignature parseSimpleClassTypeSignature( boolean dollar )
	{
		String id = parseIdentifier();
		switch( current() )
		{
			case ';':
			case '.':
				return SimpleClassTypeSignature.make( id, dollar, new TypeArgument[0] );
			case '<':
				return SimpleClassTypeSignature.make( id, dollar, parseTypeArguments() );
			default:
				throw new AssertionError();
		}
	}

	/**
	 * ClassTypeSignatureSuffix:
	 * "." SimpleClassTypeSignature
	 */
	private void parseClassTypeSignatureSuffix( List<SimpleClassTypeSignature> scts )
	{
		while( current() == '.' )
		{
			advance();
			scts.add( parseSimpleClassTypeSignature( true ) );
		}
	}

	/**
	 * TypeArguments:
	 * "<" TypeArgument+ ">"
	 */
	private TypeArgument[] parseTypeArguments()
	{
		assert current() == '<';
		advance();
		List<TypeArgument> tas = new ArrayList<>( 3 );
		tas.add( parseTypeArgument() );
		while( current() != '>' )
		{
			//(matches(current(),  '+', '-', 'L', '[', 'T', '*')) {
			tas.add( parseTypeArgument() );
		}
		advance();
		return tas.toArray( new TypeArgument[0] );
	}

	/**
	 * TypeArgument:
	 * WildcardIndicator_opt FieldTypeSignature
	 * "*"
	 */
	private TypeArgument parseTypeArgument()
	{
		ObjectSignature[] ub, lb;
		ub = new ObjectSignature[1];
		lb = new ObjectSignature[1];
		TypeArgument[] ta = new TypeArgument[0];
		char c = current();
		switch( c )
		{
			case '+':
			{
				advance();
				ub[0] = parseFieldTypeSignature();
				lb[0] = BottomSignature.instance; // bottom
				return Wildcard.make( ub, lb );
			}
			case '*':
			{
				advance();
				ub[0] = SimpleClassTypeSignature.make( "java.lang.Object", false, ta );
				lb[0] = BottomSignature.instance; // bottom
				return Wildcard.make( ub, lb );
			}
			case '-':
			{
				advance();
				lb[0] = parseFieldTypeSignature();
				ub[0] = SimpleClassTypeSignature.make( "java.lang.Object", false, ta );
				return Wildcard.make( ub, lb );
			}
			default:
				return parseFieldTypeSignature();
		}
	}

	/**
	 * TypeVariableSignature:
	 * "T" Identifier ";"
	 */
	private TypeVariableSignature parseTypeVariableSignature()
	{
		assert current() == 'T';
		advance();
		TypeVariableSignature ts = TypeVariableSignature.make( parseIdentifier() );
		assert current() == ';';
		advance();
		return ts;
	}

	/**
	 * ArrayTypeSignature:
	 * "[" TypeSignature
	 */
	private ArrayTypeSignature parseArrayTypeSignature()
	{
		assert current() == '[';
		advance();
		return ArrayTypeSignature.make( parseTypeSignature() );
	}

	/**
	 * TypeSignature:
	 * FieldTypeSignature
	 * BaseType
	 */
	private TypeSignature parseTypeSignature()
	{
		switch( current() )
		{
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
			case 'Z':
				return parseBaseType();

			default:
				return parseFieldTypeSignature();
		}
	}

	private PrimitiveTypeSignature parseBaseType()
	{
		switch( current() )
		{
			case 'B':
				advance();
				return ByteSignature.instance;
			case 'C':
				advance();
				return CharSignature.instance;
			case 'D':
				advance();
				return DoubleSignature.instance;
			case 'F':
				advance();
				return FloatSignature.instance;
			case 'I':
				advance();
				return IntSignature.instance;
			case 'J':
				advance();
				return LongSignature.instance;
			case 'S':
				advance();
				return ShortSignature.instance;
			case 'Z':
				advance();
				return BooleanSignature.instance;
			default:
				throw new AssertionError();
		}
	}

	/**
	 * ClassBound:
	 * ":" FieldTypeSignature_opt
	 * <p>
	 * InterfaceBound:
	 * ":" FieldTypeSignature
	 */
	private ObjectSignature[] parseBounds()
	{
		assert current() == ':';
		advance();
		List<ObjectSignature> fts = new ArrayList<>( 3 );
		switch( current() )
		{
			case ':': // empty class bound
				break;

			default: // parse class bound
				fts.add( parseFieldTypeSignature() );
		}

		// zero or more interface bounds
		while( current() == ':' )
		{
			advance();
			fts.add( parseFieldTypeSignature() );
		}
		return fts.toArray( new ObjectSignature[0] );
	}

	/**
	 * SuperclassSignature:
	 * ClassTypeSignature
	 */
	private ClassTypeSignature[] parseSuperInterfaces()
	{
		List<ClassTypeSignature> cts = new ArrayList<>( 5 );
		while( current() == 'L' )
			cts.add( parseClassTypeSignature() );
		return cts.toArray( new ClassTypeSignature[0] );
	}

	/**
	 * MethodTypeSignature:
	 * FormalTypeParameters_opt "(" TypeSignature* ")" ReturnType ThrowsSignature*
	 */
	private MethodTypeSignature parseMethodTypeSignature()
	{
		// Parse a method signature based on the implicit input.
		assert index == 0;
		return MethodTypeSignature.make( parseZeroOrMoreFormalTypeParameters(), parseFormalParameters(), parseReturnType(), parseZeroOrMoreThrowsSignatures() );
	}

	// "(" TypeSignature* ")"
	private TypeSignature[] parseFormalParameters()
	{
		assert current() == '(';
		advance();
		TypeSignature[] pts = parseZeroOrMoreTypeSignatures();
		assert current() == ')';
		advance();
		return pts;
	}

	// TypeSignature*
	private TypeSignature[] parseZeroOrMoreTypeSignatures()
	{
		List<TypeSignature> ts = new ArrayList<>();
		boolean stop = false;
		while( !stop )
		{
			switch( current() )
			{
				case 'B':
				case 'C':
				case 'D':
				case 'F':
				case 'I':
				case 'J':
				case 'S':
				case 'Z':
				case 'L':
				case 'T':
				case '[':
				{
					ts.add( parseTypeSignature() );
					break;
				}
				default:
					stop = true;
			}
		}
		return ts.toArray( new TypeSignature[0] );
	}

	/**
	 * ReturnType:
	 * TypeSignature
	 * VoidDescriptor
	 */
	private ReturnType parseReturnType()
	{
		if( current() == 'V' )
		{
			advance();
			return VoidDescriptor.instance;
		}
		else
			return parseTypeSignature();
	}

	// ThrowSignature*
	private ObjectSignature[] parseZeroOrMoreThrowsSignatures()
	{
		List<ObjectSignature> ets = new ArrayList<>( 3 );
		while( current() == '^' )
		{
			ets.add( parseThrowsSignature() );
		}
		return ets.toArray( new ObjectSignature[0] );
	}

	/**
	 * ThrowsSignature:
	 * "^" ClassTypeSignature
	 * "^" TypeVariableSignature
	 */
	private ObjectSignature parseThrowsSignature()
	{
		assert current() == '^';
		advance();
		return parseFieldTypeSignature( false );
	}
}
