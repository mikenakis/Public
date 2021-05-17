package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.Descriptor;
import mikenakis.bytecode.constants.ReferenceConstant;
import mikenakis.bytecode.dumping.RenderingContext;

import java.util.Optional;

/**
 * {@link ReferenceConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ReferenceConstantPrinter extends ConstantPrinter
{
	private final ReferenceConstant referenceConstant;

	public ReferenceConstantPrinter( ReferenceConstant referenceConstant )
	{
		super( referenceConstant );
		this.referenceConstant = referenceConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "type = " );
		renderingContext.newPrinter( referenceConstant.typeConstant ).appendRawIndexTo( renderingContext, builder );
		builder.append( ", nameAndType = " );
		renderingContext.newPrinter( referenceConstant.nameAndTypeConstant ).appendRawIndexTo( renderingContext, builder );
		builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		Descriptor descriptor = Descriptor.from( referenceConstant.nameAndTypeConstant.descriptorConstant.getStringValue() );
		RenderingContext.appendDescriptorTo( descriptor, builder, referenceConstant.nameAndTypeConstant.nameConstant.getStringValue(), Optional.of( referenceConstant.typeConstant.getClassName() ) );
	}
}
