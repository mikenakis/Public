package io.github.mikenakis.kit.exceptions;

import io.github.mikenakis.kit.Kit;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base class for unchecked exceptions. (Which are the only kind of exceptions that I use by my own free will.)
 * Disallows the setting of a human-readable message, builds the human-readable message using reflection.
 * Disallows overriding getMessage() and toString() to prevent misuse.
 *
 * @author michael.gr
 */
public class UncheckedException extends RuntimeException
{
	public UncheckedException()
	{
	}

	public UncheckedException( Optional<? extends Throwable> cause )
	{
		super( cause.orElse( null ) );
	}

	public UncheckedException( Throwable cause )
	{
		super( cause );
	}

	@Override public final String getMessage()
	{
		//NOTE: we are not adding the message of the base class because we have not set a value to it, so super.getMessage() would return the message of the
		// cause exception, which is useless, since there will be a "caused by" message anyway.
		return Arrays.stream( getClass().getFields() ) //
			.filter( field -> Kit.reflect.isInstanceMember( field ) ) //
			.filter( field -> isNotMemberOfSuper( field ) ) //
			.map( field -> field.getName() + "=" + fieldValueToString( field ) ) //
			.collect( Collectors.joining( "; " ) );
	}

	private static boolean isNotMemberOfSuper( Member field )
	{
		return UncheckedException.class.isAssignableFrom( field.getDeclaringClass() );
	}

	private String fieldValueToString( Field field )
	{
		Object value = fieldValue( field );
		return Kit.string.from( value );
	}

	private Object fieldValue( Field field )
	{
		try
		{
			return field.get( this );
		}
		catch( IllegalAccessException ignore )
		{
			return "<inaccessible>";
		}
	}

	@SuppressWarnings( "EmptyMethod" ) @Override public final String toString()
	{
		//PEARL: the constructor of AssertionError begins by calling `toString()` on the cause exception, and the default `toString()` of `Throwable` invokes
		//       `getLocalizedMessage()` (which is another PEARL of its own, but never mind) which invokes `getMessage()`. This means that `getMessage()` will
		//       always be invoked once as soon as the exception is thrown. We don't want this, so yield a bogus message.
		return getClass().getName(); //super.toString();
	}
}
