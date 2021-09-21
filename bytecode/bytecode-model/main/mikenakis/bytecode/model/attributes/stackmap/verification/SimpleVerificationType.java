package mikenakis.bytecode.model.attributes.stackmap.verification;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationType extends VerificationType
{
	public SimpleVerificationType( Tag tag )
	{
		super( tag );
		assert tag == Tag.Top ||tag == Tag.Integer || tag == Tag.Float || tag == Tag.Double || tag == Tag.Long || tag == Tag.Null || tag == Tag.UninitializedThis;
	}

	@Deprecated @Override protected SimpleVerificationType asSimpleVerificationType() { return this; }
}
