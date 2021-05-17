package mikenakis.testana.discovery;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Discovers {@link DiscoveryModule}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Discoverer
{
	Optional<DiscoveryModule> discover( Path sourceDirectory );
}
