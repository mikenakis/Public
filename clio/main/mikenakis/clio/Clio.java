package mikenakis.clio;

import mikenakis.clio.arguments.EnumChoiceParameterOption;
import mikenakis.clio.arguments.Option;
import mikenakis.clio.arguments.PathPositionalOption;
import mikenakis.clio.arguments.PositionalOption;
import mikenakis.clio.arguments.StringParameterOption;
import mikenakis.clio.arguments.StringPositionalOption;
import mikenakis.clio.arguments.SwitchOption;
import mikenakis.clio.exceptions.ClioException;
import mikenakis.kit.Kit;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
	private final List<Option<?>> options = new ArrayList<>();

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
	 * @param helpSwitchName        the name of the 'help' switch.  (Usually, "-help" or "--help | -h")
	 * @param helpSwitchDescription the description of the 'help' switch, used for displaying usage help.
	 */
	public Clio( String programName, String helpSwitchName, String helpSwitchDescription )
	{
		this.programName = programName;
		helpSwitch = helpSwitchName == null ? null : add( new SwitchOption( this, helpSwitchName, helpSwitchDescription ) );
	}

	/**
	 * Adds a switch option.
	 *
	 * @param name        the name of the switch. (For example, "--all | -a")
	 * @param description the description of the switch, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])}; will return {@code true} if the switch was present in the command line,
	 *    {@code false} otherwise.
	 */
	public Supplier<Boolean> addSwitchOption( String name, String description )
	{
		return add( new SwitchOption( this, name, description ) );
	}

	/**
	 * Adds a {@link String}-parameter option with a default value.
	 *
	 * @param name                  the name of the option. (For example, "--output | -o")
	 * @param parameterName         the name of the parameter of the option, used for displaying usage help.
	 * @param defaultParameterValue the default value that will be used for the parameter if the option is not present in the command line.
	 * @param description           the description of the option, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the parameter of the option.
	 */
	public Supplier<String> addStringParameterOption( String name, String parameterName, String defaultParameterValue, String description )
	{
		assert defaultParameterValue != null;
		return add( new StringParameterOption( this, name, parameterName, defaultParameterValue, description ) );
	}

	/**
	 * Adds a mandatory {@link String}-parameter option.
	 *
	 * @param name                  the name of the option. (For example, "--output | -o")
	 * @param parameterName         the name of the parameter of the option, used for displaying usage help.
	 * @param description           the description of the option, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the parameter of the option.
	 */
	public Supplier<String> addMandatoryStringParameterOption( String name, String parameterName, String description )
	{
		return add( new StringParameterOption( this, name, parameterName, null, description ) );
	}

	/**
	 * Adds an {@link Enum}-parameter option with a default value.
	 *
	 * @param name                  the name of the option. (For example, "--mode | -m")
	 * @param parameterName         the name of the parameter of the option, used for displaying usage help.
	 * @param enumClass             the {@link Class} of the parameter of the option.
	 * @param defaultParameterValue the default value that will be used for the parameter if the option is not given in the command line.
	 * @param description           the description of the option, used for displaying usage help.
	 * @param <T>                   the type of the parameter.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the parameter of the option.
	 */
	public <T extends Enum<T>> Supplier<T> addEnumParameterOption( String name, String parameterName, Class<T> enumClass, T defaultParameterValue, String description )
	{
		assert defaultParameterValue != null;
		return add( new EnumChoiceParameterOption<>( this, name, parameterName, enumClass, defaultParameterValue, description ) );
	}

	/**
	 * Adds a mandatory {@link Enum}-parameter option.
	 *
	 * @param name                  the name of the option. (For example, "--mode | -m")
	 * @param parameterName         the name of the parameter of the option, used for displaying usage help.
	 * @param enumClass             the {@link Class} of the parameter of the option.
	 * @param description           the description of the option, used for displaying usage help.
	 * @param <T>                   the type of the parameter.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the parameter of the option.
	 */
	public <T extends Enum<T>> Supplier<T> addMandatoryEnumParameterOption( String name, String parameterName, Class<T> enumClass, String description )
	{
		return add( new EnumChoiceParameterOption<>( this, name, parameterName, enumClass, null, description ) );
	}

	/**
	 * Adds a {@link String} positional option with a default value.
	 *
	 * @param name         the name of the positional option, used for displaying usage help.
	 * @param defaultValue the default value of the positional option.
	 * @param description  the description of the positional option, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the positional option.
	 */
	public Supplier<String> addStringPositionalOption( String name, String defaultValue, String description )
	{
		assert defaultValue != null;
		return add( new StringPositionalOption( this, name, defaultValue, description ) );
	}

	/**
	 * Adds a mandatory {@link String} positional option.
	 *
	 * @param name         the name of the positional option, used for displaying usage help.
	 * @param description  the description of the positional option, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the positional option.
	 */
	public Supplier<String> addMandatoryStringPositionalOption( String name, String description )
	{
		return add( new StringPositionalOption( this, name, null, description ) );
	}

	/**
	 * Adds a {@link Path} positional option with a default value.
	 *
	 * @param name         the name of the positional option, used for displaying usage help.
	 * @param defaultValue the default value of the positional option.
	 * @param description  the description of the positional option, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the positional option.
	 */
	public Supplier<Path> addPathPositionalOption( String name, String defaultValue, String description )
	{
		assert defaultValue != null;
		return add( new PathPositionalOption( this, name, defaultValue, description ) );
	}

	/**
	 * Adds a mandatory {@link Path} positional option.
	 *
	 * @param name         the name of the positional option, used for displaying usage help.
	 * @param description  the description of the positional option, used for displaying usage help.
	 *
	 * @return a {@link Supplier} that can be invoked after {@link #parse(String[])} to return the value of the positional option.
	 */
	public Supplier<Path> addPathPositionalOption( String name, String description )
	{
		return add( new PathPositionalOption( this, name, null, description ) );
	}

	private <T> Supplier<T> add( Option<T> option )
	{
		assert argumentIsOkAssertion( option );
		options.add( option );
		return option;
	}

	private boolean argumentIsOkAssertion( Option<?> option )
	{
		if( !(option instanceof PositionalOption) )
		{
			PositionalOption<?> positionalArgument = findFirstPositionalArgument();
			assert positionalArgument == null : "non-positional argument '" + option.name + "' must appear before positional argument '" + positionalArgument.name + "'.";
		}
		return true;
	}

	private PositionalOption<?> findFirstPositionalArgument()
	{
		for( Option<?> option : options )
			if( option instanceof PositionalOption )
				return (PositionalOption<?>)option;
		return null;
	}

	/**
	 * Parses an array of command line arguments.
	 *
	 * @param commandLineArguments the command line arguments to parse.
	 *
	 * @return {@code true} if everything went ok; {@code false} if errors have been reported and the calling program should terminate.
	 */
	public boolean parse( String[] commandLineArguments )
	{
		return parse( Arrays.asList( commandLineArguments ) );
	}

	/**
	 * Parses a list of command line arguments and reports any errors.
	 *
	 * @param commandLineArguments the command line arguments to parse.
	 *
	 * @return {@code true} if there were any errors; {@code false} otherwise.
	 */
	public boolean parse( List<String> commandLineArguments )
	{
		List<String> remainingArguments = new ArrayList<>( commandLineArguments );
		Collection<Option<?>> remainingOptions = new ArrayList<>( options );
		while( !remainingArguments.isEmpty() )
		{
			Option<?> foundOption;
			try
			{
				foundOption = tryFindArgument( remainingArguments, remainingOptions );
			}
			catch( ClioException exception )
			{
				System.err.println( exception.getMessage() );
				return false;
			}
			if( foundOption == null )
			{
				System.err.println( "Unrecognized token: " + remainingArguments.get( 0 ) );
				help( System.out );
				return false;
			}
			if( foundOption == helpSwitch )
			{
				help( System.out );
				return false;
			}
			Kit.collection.remove( remainingOptions, foundOption );
		}
		for( Option<?> remainingOption : remainingOptions )
			if( !remainingOption.optional )
			{
				System.err.println( "Required argument missing: " + remainingOption.name );
				return false;
			}
		return true;
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
		for( Option<?> option : options )
		{
			String shortUsage = option.getShortUsage();
			maxShortUsageLength = Math.max( maxShortUsageLength, shortUsage.length() );
			if( option.optional )
				printStream.print( '[' );
			printStream.print( shortUsage );
			if( option.optional )
				printStream.print( ']' );
			printStream.print( ' ' );
		}
		return maxShortUsageLength;
	}

	private void printLongUsages( int maxShortUsageLength, PrintStream printStream )
	{
		for( Option<?> option : options )
		{
			printStream.print( "    " );
			printStream.print( padLeft( option.getShortUsage(), maxShortUsageLength ) );
			printStream.print( ' ' );
			printStream.print( option.getLongUsage() );
			printStream.println();
		}
	}

	private static Option<?> tryFindArgument( List<String> remainingTokens, Iterable<? extends Option<?>> remainingOptions ) throws ClioException
	{
		boolean lookForPositional = !remainingTokens.get( 0 ).startsWith( "-" );
		for( Option<?> option : remainingOptions )
		{
			boolean positional = option instanceof PositionalOption;
			if( positional != lookForPositional )
				continue;
			if( tryParseArgument( option, remainingTokens ) )
				return option;
		}
		return null;
	}

	private static boolean tryParseArgument( Option<?> option, List<String> remainingTokens ) throws ClioException
	{
		int length = remainingTokens.size();
		boolean ok = option.tryParse( remainingTokens );
		assert ok == remainingTokens.size() < length;
		return ok;
	}

	private static String padLeft( String s, int width )
	{
		var builder = new StringBuilder();
		builder.append( s );
		while( builder.length() < width )
			builder.append( ' ' );
		return builder.toString();
	}
}
