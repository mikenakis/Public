package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.MethodParameter;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link MethodParameter} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParameterPrinter extends Printer
{
	private final MethodParameter methodParameter;

	public MethodParameterPrinter( MethodParameter methodParameter )
	{
		this.methodParameter = methodParameter;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "accessFlags = 0x" ).append( Integer.toHexString( methodParameter.accessFlags ) );
			builder.append( ", name = " );
			RenderingContext.newPrinter( methodParameter.nameConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			if( methodParameter.accessFlags != 0 )
			{
				RenderingContext.appendBitsAsString( builder, methodParameter.accessFlags, MethodParameterPrinter::getAccessFlagName, " " );
				builder.append( ' ' );
			}
			builder.append( methodParameter.nameConstant.getStringValue() );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	private static String getAccessFlagName( int flag )
	{
		assert Integer.bitCount( flag ) == 1;
		switch( flag )
		{
			//@formatter:off
			case MethodParameter.ACC_FINAL         : return "final";
			case MethodParameter.ACC_SYNTHETIC     : return "synthetic";
			case MethodParameter.ACC_MANDATED      : return "mandated";
			default : return null;
			//@formatter:on
		}
	}
}
