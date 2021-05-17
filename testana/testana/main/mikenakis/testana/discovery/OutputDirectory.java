package mikenakis.testana.discovery;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Represents an output directory of a module.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class OutputDirectory
{
	public final Path path;
	private final Collection<OutputFile> projectFiles;

	public OutputDirectory( Path path, Collection<OutputFile> projectFiles )
	{
		this.path = path;
		this.projectFiles = projectFiles;
	}

	@Override public String toString()
	{
		return path.toString();
	}

	public Collection<OutputFile> files()
	{
		return projectFiles;
	}
}
