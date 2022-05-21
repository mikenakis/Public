package io.github.mikenakis.testana.kit.structured.writing;

import java.util.function.Consumer;

public interface ArrayWriter
{
	void writeElement( Consumer<StructuredWriter> structuredWriterConsumer );
}
