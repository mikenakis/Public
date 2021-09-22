package mikenakis.bytecode.test;

import mikenakis.benchmark.Benchmark;
import mikenakis.benchmark.Benchmarkable;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.bytecode.test.model.Model;
import mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BytecodeBenchmark
{
	public static void main( String[] args )
	{
		List<Path> classFilePathNames = Model.getClassFilePathNames();
		List<byte[]> bytesList = classFilePathNames.stream().map( path -> Kit.unchecked( () -> Files.readAllBytes( path ) ) ).toList();
		double readingDurationSeconds = benchmarkReading( bytesList );
		double printingDurationSeconds = benchmarkPrinting( bytesList );
		System.out.printf( Locale.ROOT, "Reading:  %6.2f millis\n", readingDurationSeconds * 1e3 );
		System.out.printf( Locale.ROOT, "Printing: %6.2f millis\n", printingDurationSeconds * 1e3 );
	}

	private static double benchmarkReading( Iterable<byte[]> bytesList )
	{
		Benchmark benchmark = new Benchmark( 0.001, 50 );
		return benchmark.measure( Benchmarkable.of( () -> //
		{
			int sum = 0;
			for( var bytes : bytesList )
			{
				ByteCodeType byteCodeType = ByteCodeReader.read( bytes );
				sum += byteCodeType.hashCode();
			}
			return sum;
		} ) );
	}

	private static double benchmarkPrinting( Iterable<byte[]> bytesList )
	{
		Benchmark benchmark = new Benchmark( 0.001, 50 );
		return benchmark.measure( Benchmarkable.of( () -> //
		{
			int sum = 0;
			for( var bytes : bytesList )
			{
				ByteCodeType byteCodeType = ByteCodeReader.read( bytes );
				ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() );
				sum += byteCodeType.hashCode();
			}
			return sum;
		} ) );
	}
}
