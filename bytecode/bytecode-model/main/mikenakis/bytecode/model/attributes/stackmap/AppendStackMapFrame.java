package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 'Append' {@link StackMapFrame}.
 * <p>
 * the operand stack is empty and the current locals are the same as the locals in the previous frame, except that k additional locals are defined.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AppendStackMapFrame extends StackMapFrame
{
	public static AppendStackMapFrame of( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		return new AppendStackMapFrame( targetInstruction, localVerificationTypes );
	}

	public static final String typeName = "AppendFrame";

	private final List<VerificationType> localVerificationTypes;

	private AppendStackMapFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		super( typeName, targetInstruction );
		assert !localVerificationTypes.isEmpty();
		assert localVerificationTypes.size() <= 3;
		this.localVerificationTypes = localVerificationTypes;
	}

	public List<VerificationType> localVerificationTypes() { return Collections.unmodifiableList( localVerificationTypes ); }

	@Override public String getName( Optional<StackMapFrame> previousFrame ) { return typeName; }
	@Deprecated @Override public Optional<AppendStackMapFrame> tryAsAppendStackMapFrame() { return Optional.of( this ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return localVerificationTypes.size() + " localVerificationTypes"; }
}
