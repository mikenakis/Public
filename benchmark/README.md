# mikenakis-benchmark
#### A small but powerful micro-benchmarking library.

<p align="center">
<img title="mikenakis-benchmark logo" src="mikenakis-benchmark.svg" width="256"/><br/>
The mikenakis-benchmark logo<br/>
<small>Created by Musmellow from the Noun Project<br/>
Used under CC BY license.<br/></small>
</p>

## Description

`mikenakis-benchmark` allows you to measure how long it takes for a certain piece of code to run.

The intention is to be able to write very short and very fast benchmarks which run as part of the tests,
to ensure that a certain piece of code:
  - meets a certain performance requirement, or
  - runs faster than another piece of code by at least a certain factor.

This way, you can have tests that fail if a performance-critical piece of code 
happens to be modified in a way which inadvertently degrades its performance.

### Features

- Accurate
  - Repeatability is not perfect, but it is surprisingly high compared to what one usually expects 
    when benchmarking code on the Java Virtual Machine.
- Efficient
  - Measures the performance of fast operations such as integer addition with great accuracy 
    in less than a millisecond.
- Compact
  - At least one order of magnitude smaller than other benchmarking frameworks<small><sup>*1</sup></small>.
  - Only one dependency, the `mikenakis.kit` library, which is again at least one order of magnitude smaller
    than the dependencies of other benchmarking frameworks<small><sup>*1</sup></small>.
- Easy to use
  - No need to run with special JVM arguments. (Although you can, for better accuracy.)
  - No need to create a special benchmarking project.
  - No need to write benchmarks according to a particular structure.
  - No annotations to memorize.
  - All you need to do in order to obtain a measurement is:
      - Construct a `Benchmark` object.
      - Wrap the code that you want to benchmark inside a loop.
      - Put that loop in a lambda.
      - Pass the lambda to the `measure()` method of `Benchmark`.

<hr/>
*1 Other frameworks:

 - The Java Microbenchmark Harness (JMH)
 - Google Caliper

The above support micro-benchmarking. For a longer list of frameworks, that do not necessarily support micro-benchmarking,
see <a href="https://stackoverflow.com/a/7445378/773113">Stackoverflow: What is the best macro-benchmarking tool / framework
to measure a single-threaded complex algorithm in Java? [closed]</a>
   
## Discussion

Measuring the performance of a piece of code on a modern Personal Computer and achieving any accuracy whatsoever is 
very difficult, due to a number of factors:

- Virtually all Personal Computers today are running multitasking Operating Systems 
  that keep preempting processes all the time, 
  in a fashion which is for all practical purposes entirely random.
- The amount of other software that is installed and running on the computer
  and constantly wants to do other stuff
  tends to be large, 
  and generally out of our control.
- Most Personal Computers today are laptops, 
  which tend to be configured for aggressive Power Management, 
  and this usually involves throttling the CPU clock.
- Motherboards tend to have a spread-spectrum feature, 
  which is meant to reduce radio interference by deliberately fluctuating the frequency of the system clock, 
  and they usually come from the factory with this feature enabled.
- Modern CPU features such as pipelining, internal register allocation, branch prediction, etc. 
  greatly affect the performance characteristics of code 
  in chaotic and unpredictable ways.
- Accessing main memory is much slower than accessing the CPU cache, 
  so the tiniest fluctuation in CPU cache utilization tends to have a severe impact on performance,
  and yet CPU cache utilization is affected by random factors 
  that we practically have no control over, 
  such as thread switches.

Furthermore, measuring the performance of code running inside the Java Virtual Machine (JVM) in particular is much 
more difficult, due to a number of additional factors:

- There is no reliable high resolution timer available on the JVM. 
  There is a `System.nanoTime()` function, whose specification is intentionally vague, 
  since it may be implemented differently across JVMs and across host systems. 
  On my computer, this function returns a value which advances by 100 nanoseconds every 100 nanoseconds, 
  which is borderline inadequate. 
  On your computer, it may be doing something different; 
  it may have an actual nanosecond resolution, which would be awesome, 
  or it may simply map to `System.currentTimeMillis()`, which would be completely inadequate.
  To make matters worse, every time I invoke `System.nanoTime()` on my computer, 
  I suffer a penalty of a whole 22 nanoseconds while that function is figuring out what value to return to me.
- The JVM performs garbage collection,
  which happens at practically random times,
  and can have a considerable impact on performance.
- When a certain piece of code produces even a tiny bit of garbage per run, 
  it will quite possibly trigger garbage collection while it is being benchmarked, 
  because benchmarking involves running code many, many, I mean _many_ times. 
- Under normal circumstances, the JVM runs our code in interpreted mode, 
  and it only compiles code that gets executed a lot. 
  This means that the performance characteristics of the code under benchmark will change
  at some unknown moment in time 
  from rather slow (interpreted) to quite fast (compiled.)
- The JVM keeps collecting statistics even on code that it has already compiled, 
  and it may recompile that code in a different way
  if it believes that it can do a better job at optimizing it. 
  This means that the performance characteristics of any piece of code are always subject to random change. 
- The JVM uses the statistics that it collects about our code 
  to make assumptions about our code,
  e.g. that a certain variable will never be null, or that it will always be null.
  These assumptions are sometimes proven wrong, 
  in which case a trap is triggered, 
  and the JVM recompiles the code
  to revert that optimization. (A process known as de-optimization.)
  This in turn means even more wild random changes in the performance characteristics of our code.

Due to the above reasons, benchmarking code on the JVM is an almost hopeless endeavor.

In order to somewhat mitigate the above factors, the approach we take is as follows:

1. We repeat measuring the code thousands of times, under the assumption that there will inevitably be some
   measurements which will happen in-between sources of skewing and will therefore yield readings that are 
   free from skewing.

2. We never take the average of measurements.
   Instead, we always select the shortest measurement as the one true measurement and throw away the rest.
   The reasoning behind this choice is that almost all the factors listed above which may throw a measurement off 
   tend to make code appear slower than it really is. 
   Therefore, each time we observe the code running faster than observed thus far,
   we assume that this observation is less skewed than all previous observations.
   Of course, there is an exception: 
   There is a possibility that we will perform a measurement while some optimization is in effect 
   which was based on some assumption which will turn out to be wrong, 
   so the optimization will be undone, and our code will not really be running so fast. 
   There is nothing we can do about this, 
   so we accept the possibility of some measurement bias in this regard.

3. We keep repeating the measurements 
   until we detect that there has been no discernible improvement
   over the last N measurements, where N is configurable,
   and "discernible" is also configurable.

This approach yields acceptably accurate and repeatable results.
By 'acceptably' we mean that the results are not perfect,  
but they are pretty darn good compared to the utter chaos that is usually observed
when trying to benchmark code on the Java Virtual Machine.

This approach also yields acceptable performance.
Measuring the tiniest piece of code, such as a single integer addition,
can be done with decent accuracy and repeatability in under a millisecond, 
while for longer pieces of code it really depends on how accurate you want the result to be. 
(Then again, the longer it takes to run the code, the less accuracy matters, so you can
relax your accuracy demands in order to keep the total running time of the benchmark low.)

## License

This creative work is explicitly published under ***No License***. 
This means that I remain the exclusive copyright holder of this creative work, 
and you may not do anything with it other than view its source code and admire it. 
More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
However, when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)
