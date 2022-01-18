package mikenakis.kit;

/**
 * A generic (as in, "general purpose") exception.
 * Normally this should never be used; as I have already explained elsewhere, (https://softwareengineering.stackexchange.com/a/278958/41811)
 * the message of an exception is the class name of the exception, so instead of writing human-readable messages we should be declaring separate
 * exception classes for each and every occasion.
 * However, this is very handy.
 * Each time an instance of this class is created, the programmer is making an implicit promise to come back one day and replace it with the creation of
 * an instance of an exception class which is specific to the kind of error that is being reported.
 *
 * @author michael.gr
 */
public class GenericException extends UncheckedException
{
	public final String message;

	public GenericException( String message )
	{
		this.message = message;
	}
}