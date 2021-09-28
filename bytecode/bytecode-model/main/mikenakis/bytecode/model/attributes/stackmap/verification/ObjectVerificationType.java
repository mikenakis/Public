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

	public final ClassConstant classConstant;

	private ObjectVerificationType( ClassConstant classConstant )
	{
		super( tag_Object );
		this.classConstant = classConstant;
	}

	public String typeName() { return classConstant.typeName(); }

	@Deprecated @Override public ObjectVerificationType asObjectVerificationType() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return super.toString() + " classConstant = " + classConstant;
	}
}
