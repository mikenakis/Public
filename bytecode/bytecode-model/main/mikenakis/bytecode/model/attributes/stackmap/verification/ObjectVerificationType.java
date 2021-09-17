package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Object' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ObjectVerificationType extends VerificationType
{
	public static ObjectVerificationType of( ClassConstant classConstant )
	{
		return new ObjectVerificationType( classConstant );
	}

	public static final int tag = 7;
	public static final String tagName = "Object";

	private final ClassConstant classConstant;

	private ObjectVerificationType( ClassConstant classConstant )
	{
		super( tag );
		this.classConstant = classConstant;
	}

	@Deprecated @Override public Optional<ObjectVerificationType> tryAsObjectVerificationType() { return Optional.of( this ); }

	public ClassConstant classConstant() { return classConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return super.toString() + " classConstant = " + classConstant;
	}
}
