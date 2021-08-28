package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeMember;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;

/**
 * {@link ByteCodeMember} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeMemberPrinter extends Printer
{
	public final ByteCodeMember byteCodeMember;

	public ByteCodeMemberPrinter( ByteCodeMember byteCodeMember )
	{
		this.byteCodeMember = byteCodeMember;
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return byteCodeMember.attributes.size() == 0 ? List.of() : List.of( RenderingContext.newPrinter( byteCodeMember.attributes ).toTwig( renderingContext, "attributes" ) );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "accessFlags = 0x" ).append( Integer.toHexString( byteCodeMember.accessAsInt() ) );
			builder.append( ", name = " );
			RenderingContext.newPrinter( byteCodeMember.nameConstant ).appendRawIndexTo( renderingContext, builder );
			builder.append( ", descriptor = " );
			RenderingContext.newPrinter( byteCodeMember.descriptorConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			byteCodeMember.accessToStringBuilder( builder );
			RenderingContext.appendGildedNameAndTypeAndDescriptor( builder, byteCodeMember.nameConstant, byteCodeMember.descriptorConstant );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
