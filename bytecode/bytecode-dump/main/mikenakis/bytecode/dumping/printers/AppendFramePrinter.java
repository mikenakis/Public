package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.AppendFrame;
import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link AppendFrame} {@link FramePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AppendFramePrinter extends FramePrinter
{
	private final AppendFrame appendFrame;

	public AppendFramePrinter( AppendFrame appendFrame, Optional<Frame> previousFrame )
	{
		super( appendFrame, previousFrame );
		this.appendFrame = appendFrame;
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of( Twig.of( "localVerificationTypes (" + appendFrame.localVerificationTypes.size() + " entries)",
			appendFrame.localVerificationTypes.stream().map( t -> renderingContext.newPrinter( t ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ) );
	}
}
