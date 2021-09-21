package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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

	private final ClassConstant classConstant;

	private ObjectVerificationType( ClassConstant classConstant )
	{
		super( Tag.Object );
		this.classConstant = classConstant;
	}

	@Deprecated @Override protected ObjectVerificationType asObjectVerificationType() { return this; }

	public ClassConstant classConstant() { return classConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return super.toString() + " classConstant = " + classConstant;
	}
}
