package mikenakis.clio.arguments;

import mikenakis.clio.Clio;
import mikenakis.clio.exceptions.ClioException;
import mikenakis.clio.exceptions.ExpectedParameterException;
import mikenakis.clio.exceptions.UnparsableParameterException;
import mikenakis.kit.Kit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link Option} which is followed by a parameter.
 *
 * TODO: make it possible to specify "-name=value" instead of only "-name value"
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ParameterOption<T> extends Option<T>
{
	protected final String parameterName;
	private final T defaultParameterValue;
	private final String description;
	private T givenParameterValue = null;

	protected ParameterOption( Clio clio, String optionName, String parameterName, T defaultParameterValue, String description )
	{
		super( clio, optionName, defaultParameterValue != null );
		this.parameterName = parameterName;
		this.defaultParameterValue = defaultParameterValue;
		this.description = description;
	}

	@Override public final boolean tryParse( List<String> tokens ) throws ClioException
	{
		String nameToken = tokens.get( 0 );
		if( !Kit.string.splitAtCharacter( name, '|' ).stream().map( s -> s.trim() ).collect( Collectors.toList() ).contains( nameToken ) )
			return false;
		tokens.remove( 0 );
		if( tokens.isEmpty() )
			throw new ExpectedParameterException( nameToken, parameterName );
		String parameterToken = tokens.remove( 0 );
		givenParameterValue = valueFromString( parameterToken );
		return true;
	}

	@Override public final T get()
	{
		return givenParameterValue == null ? defaultParameterValue : givenParameterValue;
	}

	protected abstract T valueFromString( String value ) throws UnparsableParameterException;

	@Override public String getShortUsage()
	{
		return name + " <" + parameterName + ">";
	}

	@Override public String getLongUsage()
	{
		var builder = new StringBuilder();
		builder.append( description );
		builder.append( getDescriptionSuffix() );
		if( defaultParameterValue != null )
			builder.append( " (default is " ).append( defaultParameterValue ).append( ")" );
		return builder.toString();
	}

	String getDescriptionSuffix()
	{
		return "";
	}
}
