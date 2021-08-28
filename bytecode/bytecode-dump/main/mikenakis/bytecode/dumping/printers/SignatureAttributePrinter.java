package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.SignatureAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link SignatureAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SignatureAttributePrinter extends AttributePrinter
{
	private final SignatureAttribute signatureAttribute;

	public SignatureAttributePrinter( SignatureAttribute signatureAttribute )
	{
		this.signatureAttribute = signatureAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		signatureAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "utf8 = " );
			RenderingContext.newPrinter( signatureAttribute.signatureConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendTo( builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	public void appendTo( StringBuilder builder )
	{
		builder.append( signatureAttribute.getSignature() );
	}
}
