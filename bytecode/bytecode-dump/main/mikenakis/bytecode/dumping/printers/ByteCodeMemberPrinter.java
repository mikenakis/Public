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
public abstract class ByteCodeMemberPrinter extends Printer
{
	public final ByteCodeMember byteCodeMember;

	protected ByteCodeMemberPrinter( ByteCodeMember byteCodeMember )
	{
		this.byteCodeMember = byteCodeMember;
	}

	@Override public final List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return byteCodeMember.attributes.size() == 0 ? List.of() : List.of( renderingContext.newPrinter( byteCodeMember.attributes ).toTwig( renderingContext, "attributes" ) );
	}

	@Override public final void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "accessFlags = 0x" ).append( Integer.toHexString( byteCodeMember.accessFlags ) );
			builder.append( ", name = " );
			renderingContext.newPrinter( byteCodeMember.nameConstant ).appendRawIndexTo( renderingContext, builder );
			builder.append( ", descriptor = " );
			renderingContext.newPrinter( byteCodeMember.descriptorConstant ).appendRawIndexTo( renderingContext, builder );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			RenderingContext.appendAccessFlags( builder, byteCodeMember.accessFlags, this::getAccessFlagName );
			RenderingContext.appendGildedNameAndTypeAndDescriptor( builder, byteCodeMember.nameConstant, byteCodeMember.descriptorConstant );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	protected abstract String getAccessFlagName( int accessFlag );
}
