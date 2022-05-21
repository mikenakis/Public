package io.github.mikenakis.testana.kit.structured.writing;

import java.util.function.Consumer;

public interface ObjectWriter
{
	void writeMember( String memberName, Consumer<StructuredWriter> structuredWriterConsumer );
}
