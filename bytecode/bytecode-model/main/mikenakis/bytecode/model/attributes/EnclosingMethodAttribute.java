package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Optional;

/**
 * Represents the "EnclosingMethod" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnclosingMethodAttribute extends KnownAttribute
{
	public static EnclosingMethodAttribute of( ClassConstant classConstant, Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant )
	{
		return new EnclosingMethodAttribute( classConstant, methodNameAndDescriptorConstant );
	}

	private final ClassConstant classConstant;
	private final Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant;

	private EnclosingMethodAttribute( ClassConstant classConstant, Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant )
	{
		super( tagEnclosingMethod );
		this.classConstant = classConstant;
		this.methodNameAndDescriptorConstant = methodNameAndDescriptorConstant;
	}

	public ClassConstant classConstant() { return classConstant; }
	public Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant() { return methodNameAndDescriptorConstant; }

	public ClassDesc classDescriptor()
	{
		return classConstant.classDescriptor();
	}

	public boolean hasMethod()
	{
		return methodNameAndDescriptorConstant.isPresent();
	}

	public String methodName()
	{
		assert hasMethod();
		return methodNameAndDescriptorConstant.map( c -> c.nameConstant.stringValue() ).orElseThrow();
	}

	public MethodTypeDesc methodDescriptor()
	{
		assert hasMethod();
		return methodNameAndDescriptorConstant.map( c -> MethodTypeDesc.ofDescriptor( c.descriptorConstant.stringValue() ) ).orElseThrow();
	}

	@Deprecated @Override public EnclosingMethodAttribute asEnclosingMethodAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "class = " + classConstant + ", methodNameAndDescriptor = { " + methodNameAndDescriptorConstant + " }";
	}
}
