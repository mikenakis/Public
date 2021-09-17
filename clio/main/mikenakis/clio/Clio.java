package mikenakis.clio;

import mikenakis.clio.arguments.Argument;
import mikenakis.clio.arguments.DefaultingArgument;
import mikenakis.clio.arguments.NamedValueArgument;
import mikenakis.clio.arguments.OptionalArgument;
import mikenakis.clio.arguments.PositionalValueArgument;
import mikenakis.clio.arguments.SwitchArgument;
import mikenakis.clio.exceptions.RequiredArgumentMissingException;
import mikenakis.clio.exceptions.UnrecognizedTokenException;
import mikenakis.clio.parsers.EnumChoiceValueParser;
import mikenakis.clio.parsers.PathValueParser;
import mikenakis.clio.parsers.StringValueParser;
import mikenakis.clio.parsers.ValueParser;
import mikenakis.kit.Kit;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Parses command-line arguments.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Clio
{
	public final String programName;
	private final Supplier<Boolean> helpSwitch;
	private final List<Argument<?>> arguments = new ArrayList<>();

	/**
	 * Initializes a new instance of {@link Clio} with a default help switch.
	 *
	 * @param programName the name of the program. (Used for displaying usage, when needed.)
	 */
	public Clio( String programName )
	{
		this( programName, "--help | -h", "display this help." );
	}

	/**
	 * Initializes a new instance of {@link Clio} with a custom help switch.
	 *
	 * @param programName           the name of the program, used for displaying usage help.
	 * @param helpSwitchNames       the argument names of the 'help' switch.  (Usually, "-help" or "--help | -h")
	 * @param helpSwitchDescription the description of the 'help' switch, used for displaying usage help.
	 */
	public Clio( String programName, String helpSwitchNames, String helpSwitchDescription )
	{
		this.programName = programName;
		helpSwitch = helpSwitchNames == null ? null : add( new SwitchArgument( splitSwitchNames( helpSwitchNames ), helpSwitchDescription ) );
	}

	/**
	 * Adds an optional {@link SwitchArgument}.
	 *
	 * @param switchNamesString the "|"-separated names of the switches. (For example, "--all | -a")
	 * @param description       the description of the switch, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])}; will return {@code true} if the switch was present in the command line,
	 *    {@code false} otherwise.
	 */
	public Supplier<Boolean> addOptionalSwitchArgument( String switchNamesString, String description )
	{
		return add( new SwitchArgument( splitSwitchNames( switchNamesString ), description ) );
	}

	/**
	 * Adds an optional {@link String} {@link NamedValueArgument}.
	 *
	 * @param switchNamesString the name of the argument. (For example, "--output | -o")
	 * @param description       the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<Optional<String>> addOptionalStringNamedArgument( String switchNamesString, String description )
	{
		ValueParser<String> valueParser = StringValueParser.instance;
		Argument<String> mandatoryArgument = new NamedValueArgument<>( splitSwitchNames( switchNamesString ), description, valueParser );
		Argument<Optional<String>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		return add( optionalArgument );
	}

	/**
	 * Adds an optional {@link String} {@link NamedValueArgument} with a default value.
	 *
	 * @param switchNamesString the name of the argument. (For example, "--output | -o")
	 * @param defaultValue      the default value that will be used if the argument is not supplied in the command line.
	 * @param description       the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<String> addOptionalStringNamedArgumentWithDefault( String switchNamesString, String defaultValue, String description )
	{
		ValueParser<String> valueParser = StringValueParser.instance;
		Argument<String> mandatoryArgument = new NamedValueArgument<>( splitSwitchNames( switchNamesString ), description, valueParser );
		Argument<Optional<String>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		Argument<String> defaultingArgument = new DefaultingArgument<>( optionalArgument, defaultValue );
		return add( defaultingArgument );
	}

	/**
	 * Adds a mandatory {@link String} {@link NamedValueArgument}.
	 *
	 * @param switchNamesString the name of the argument. (For example, "--output | -o")
	 * @param description       the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<String> addMandatoryStringNamedArgument( String switchNamesString, String description )
	{
		ValueParser<String> valueParser = StringValueParser.instance;
		Argument<String> mandatoryArgument = new NamedValueArgument<>( splitSwitchNames( switchNamesString ), description, valueParser );
		return add( mandatoryArgument );
	}

	/**
	 * Adds an optional {@link Enum} {@link NamedValueArgument}.
	 *
	 * @param switchNamesString the name of the argument. (For example, "--mode | -m")
	 * @param enumClass         the {@link Class} of the argument value.
	 * @param description       the description of the argument, used for displaying usage help.
	 * @param <T>               the type of the argument value.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public <T extends Enum<T>> Supplier<Optional<T>> addOptionalEnumNamedArgument( String switchNamesString, Class<T> enumClass, String description )
	{
		ValueParser<T> valueParser = new EnumChoiceValueParser<>( enumClass );
		Argument<T> mandatoryArgument = new NamedValueArgument<>( splitSwitchNames( switchNamesString ), description, valueParser );
		Argument<Optional<T>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		return add( optionalArgument );
	}

	/**
	 * Adds an optional {@link Enum} {@link NamedValueArgument} with a default value.
	 *
	 * @param switchNamesString the name of the argument. (For example, "--mode | -m")
	 * @param enumClass         the {@link Class} of the argument value.
	 * @param defaultValue      the value that will be used if the argument is not given in the command line.
	 * @param description       the description of the argument, used for displaying usage help.
	 * @param <T>               the type of the argument value.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public <T extends Enum<T>> Supplier<T> addOptionalEnumNamedArgumentWithDefault( String switchNamesString, Class<T> enumClass, T defaultValue, String description )
	{
		ValueParser<T> valueParser = new EnumChoiceValueParser<>( enumClass );
		Argument<T> mandatoryArgument = new NamedValueArgument<>( splitSwitchNames( switchNamesString ), description, valueParser );
		Argument<Optional<T>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		Argument<T> defaultingArgument = new DefaultingArgument<>( optionalArgument, defaultValue );
		return add( defaultingArgument );
	}

	/**
	 * Adds a mandatory {@link Enum} {@link NamedValueArgument}.
	 *
	 * @param switchNamesString the name of the argument. (For example, "--mode | -m")
	 * @param enumClass         the {@link Class} of the argument value.
	 * @param description       the description of the argument, used for displaying usage help.
	 * @param <T>               the type of the argument value.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public <T extends Enum<T>> Supplier<T> addMandatoryEnumNamedArgument( String switchNamesString, Class<T> enumClass, String description )
	{
		ValueParser<T> valueParser = new EnumChoiceValueParser<>( enumClass );
		Argument<T> mandatoryArgument = new NamedValueArgument<>( splitSwitchNames( switchNamesString ), description, valueParser );
		return add( mandatoryArgument );
	}

	/**
	 * Adds an optional {@link String} {@link PositionalValueArgument}.
	 *
	 * @param name        the name of the argument, used for displaying usage help.
	 * @param description the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<Optional<String>> addOptionalStringPositionalArgument( String name, String description )
	{
		ValueParser<String> valueParser = StringValueParser.instance;
		Argument<String> mandatoryArgument = new PositionalValueArgument<>( name, description, valueParser );
		Argument<Optional<String>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		return add( optionalArgument );
	}

	/**
	 * Adds an optional {@link String} {@link PositionalValueArgument} with a default value.
	 *
	 * @param name         the name of the argument, used for displaying usage help.
	 * @param defaultValue the value that will be used if the argument is not given in the command line.
	 * @param description  the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<String> addOptionalStringPositionalArgumentWithDefault( String name, String defaultValue, String description )
	{
		ValueParser<String> valueParser = StringValueParser.instance;
		Argument<String> mandatoryArgument = new PositionalValueArgument<>( name, description, valueParser );
		Argument<Optional<String>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		Argument<String> defaultingArgument = new DefaultingArgument<>( optionalArgument, defaultValue );
		return add( defaultingArgument );
	}

	/**
	 * Adds a mandatory {@link String} {@link PositionalValueArgument}.
	 *
	 * @param name        the name of the argument, used for displaying usage help.
	 * @param description the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<String> addMandatoryStringPositionalArgument( String name, String description )
	{
		ValueParser<String> valueParser = StringValueParser.instance;
		Argument<String> mandatoryArgument = new PositionalValueArgument<>( name, description, valueParser );
		return add( mandatoryArgument );
	}

	/**
	 * Adds an optional {@link Path} {@link PositionalValueArgument}.
	 *
	 * @param name        the name of the argument, used for displaying usage help.
	 * @param description the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<Optional<Path>> addOptionalPathPositionalArgument( String name, String description )
	{
		ValueParser<Path> valueParser = PathValueParser.instance;
		Argument<Path> mandatoryArgument = new PositionalValueArgument<>( name, description, valueParser );
		Argument<Optional<Path>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		return add( optionalArgument );
	}

	/**
	 * Adds an optional {@link Path} {@link PositionalValueArgument} with a default value.
	 *
	 * @param name         the name of the argument, used for displaying usage help.
	 * @param defaultValue the default value of the argument.
	 * @param description  the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<Path> addOptionalPathPositionalArgumentWithDefault( String name, Path defaultValue, String description )
	{
		ValueParser<Path> valueParser = PathValueParser.instance;
		Argument<Path> mandatoryArgument = new PositionalValueArgument<>( name, description, valueParser );
		Argument<Optional<Path>> optionalArgument = new OptionalArgument<>( mandatoryArgument );
		Argument<Path> defaultingArgument = new DefaultingArgument<>( optionalArgument, defaultValue );
		return add( defaultingArgument );
	}

	/**
	 * Adds a mandatory {@link Path} {@link PositionalValueArgument}.
	 *
	 * @param name        the name of the argument, used for displaying usage help.
	 * @param description the description of the argument, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the argument value.
	 */
	public Supplier<Path> addMandatoryPathPositionalArgument( String name, String description )
	{
		ValueParser<Path> valueParser = PathValueParser.instance;
		Argument<Path> mandatoryArgument = new PositionalValueArgument<>( name, description, valueParser );
		return add( mandatoryArgument );
	}

	private <T> Supplier<T> add( Argument<T> argument )
	{
		assert argumentIsOkAssertion( argument );
		arguments.add( argument );
		return argument;
	}

	private boolean argumentIsOkAssertion( Argument<?> argument )
	{
		if( !argument.isPositional() )
		{
			Argument<?> positionalArgument = findFirstPositionalArgument();
			assert positionalArgument == null : "non-positional argument '" + argument.name() + "' must appear before positional argument '" + positionalArgument.name() + "'.";
		}
		return true;
	}

	private Argument<?> findFirstPositionalArgument()
	{
		for( Argument<?> argument : arguments )
			if( argument.isPositional() )
				return argument;
		return null;
	}

	/**
	 * Parses an array of command line arguments.
	 *
	 * @param commandLineArguments the command line arguments to parse.
	 *
	 * @return {@code true} if everything went ok; {@code false} if help was requested, in which case help has been shown and the calling program should now terminate.
	 */
	public boolean parse( String[] commandLineArguments )
	{
		return parse( Arrays.asList( commandLineArguments ) );
	}

	/**
	 * Parses a list of command line arguments and reports any errors.
	 *
	 * @param commandLineTokens the command line arguments to parse.
	 *
	 * @return {@code true} if everything went ok; {@code false} if help was requested, in which case help has been shown and the calling program should now terminate.
	 */
	public boolean parse( List<String> commandLineTokens )
	{
		List<String> remainingTokens = new ArrayList<>( commandLineTokens );
		Collection<Argument<?>> remainingArguments = new ArrayList<>( arguments );
		while( !remainingTokens.isEmpty() )
		{
			Argument<?> foundArgument = tryFindArgument( remainingTokens, remainingArguments );
			if( foundArgument == null )
				throw new UnrecognizedTokenException( remainingTokens.get( 0 ) );
			if( foundArgument == helpSwitch )
			{
				help( System.out );
				return false;
			}
			Kit.collection.remove( remainingArguments, foundArgument );
		}
		for( Argument<?> remainingArgument : remainingArguments )
			if( !remainingArgument.isOptional() )
				throw new RequiredArgumentMissingException( remainingArgument );
		return true;
	}

	private static Argument<?> tryFindArgument( List<String> remainingTokens, Iterable<? extends Argument<?>> remainingArguments )
	{
		boolean lookForPositional = !remainingTokens.get( 0 ).startsWith( "-" );
		for( Argument<?> argument : remainingArguments )
		{
			if( argument.isPositional() != lookForPositional )
				continue;
			if( tryParseArgument( argument, remainingTokens ) )
				return argument;
		}
		return null;
	}

	private static boolean tryParseArgument( Argument<?> argument, List<String> remainingTokens )
	{
		int length = remainingTokens.size();
		boolean ok = argument.tryParse( remainingTokens );
		assert ok == remainingTokens.size() < length;
		return ok;
	}

	private void help( PrintStream printStream )
	{
		printStream.print( "usage: " + programName + " " );
		int maxShortUsageLength = printShortUsagesAndGetMaxLength( printStream );
		printStream.println();
		printLongUsages( maxShortUsageLength, printStream );
	}

	private int printShortUsagesAndGetMaxLength( PrintStream printStream )
	{
		int maxShortUsageLength = 0;
		for( Argument<?> argument : arguments )
		{
			String shortUsage = argument.getShortUsage();
			maxShortUsageLength = Math.max( maxShortUsageLength, shortUsage.length() );
			if( argument.isOptional() )
				printStream.print( '[' );
			printStream.print( shortUsage );
			if( argument.isOptional() )
				printStream.print( ']' );
			printStream.print( ' ' );
		}
		return maxShortUsageLength;
	}

	private void printLongUsages( int maxShortUsageLength, PrintStream printStream )
	{
		for( Argument<?> argument : arguments )
		{
			printStream.print( "    " );
			printStream.print( padLeft( argument.getShortUsage(), maxShortUsageLength ) );
			printStream.print( ' ' );
			printStream.print( argument.getLongUsage() );
			printStream.println();
		}
	}

	private static String padLeft( String s, int width )
	{
		var builder = new StringBuilder();
		builder.append( s );
		while( builder.length() < width )
			builder.append( ' ' );
		return builder.toString();
	}

	private static List<String> splitSwitchNames( String switchNamesString )
	{
		List<String> switchNames = Arrays.stream( switchNamesString.split( "\\|" ) ).map( s -> s.trim() ).toList();
		assert !switchNames.isEmpty();
		assert Kit.iterable.trueForAll( switchNames, n -> nameIsOkAssertion( n ) );
		return switchNames;
	}

	private static boolean nameIsOkAssertion( String part )
	{
		if( part.startsWith( "--" ) )
			assert matches( part, "--[a-z][a-zA-Z0-9_-]+" ) : part;
		else if( part.startsWith( "-" ) )
			assert matches( part, "-[a-z]+" ) : part;
		else
			assert matches( part, "[a-z][a-zA-Z0-9_-]+" ) : part;
		return true;
	}

	private static boolean matches( String part, String pattern )
	{
		return part.matches( pattern );
	}
}
