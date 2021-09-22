package mikenakis.bytecode.model.attributes.stackmap.verification;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationType extends VerificationType
{
	public SimpleVerificationType( int tag )
	{
		super( tag );
		assert tag == tagTop ||tag == tagInteger || tag == tagFloat || tag == tagDouble || tag == tagLong || tag == tagNull || tag == tagUninitializedThis;
	}

	@Deprecated @Override public SimpleVerificationType asSimpleVerificationType() { return this; }
}
