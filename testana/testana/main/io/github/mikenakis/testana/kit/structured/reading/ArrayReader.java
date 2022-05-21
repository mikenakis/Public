package io.github.mikenakis.testana.kit.structured.reading;

import java.util.function.Consumer;

public interface ArrayReader
{
	void readElements( Consumer<StructuredReader> structuredReaderConsumer );
}
