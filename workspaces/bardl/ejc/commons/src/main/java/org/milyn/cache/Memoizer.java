package org.milyn.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.milyn.assertion.AssertArgument;

/**
 * A memorizer is a concurrent cache the "remembers" computed values and caches
 * already computed values to enhance performance.
 *
 * Note: Currently no eviction policy is implemented.
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 */
public class Memoizer<A, V> implements Computable<A, V>
{
    /**
     * The Map that holds the cached values.
     */
    private final ConcurrentHashMap<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();

    /**
     * The {@link Computable} strategy.
     */
    private final Computable<A, V> computable;

    /**
     * Sole constructor which set the {@link Computable} strategy to be
     * used with this implemention.
     *
     * @param computable The {@link Computable} strategy.
     */
    public Memoizer(final Computable<A, V> computable)
    {
        AssertArgument.isNotNull(computable, "computable");
        this.computable = computable;
    }

    /**
     * Computes by delegating to the Computable. Will return
     * a cached value if the computation has been previsouly evaluted.
     *
     * @param arg argment for the Computation strategy.
     * @return V The value to the Computation strategy.
     * @throws InterruptedException If the futur get call is interrupted.
     */
    public V compute(final A arg) throws InterruptedException
    {
        AssertArgument.isNotNull(arg, "arg");
        while (true)
        {
            Future<V> future = cache.get(arg);
            if (notInCache(future))
            {
                FutureTask<V> futureTask = createFutureTask(arg);
                future = cache.putIfAbsent(arg, futureTask);
                if (wasAddedToCache(future))
                {
                    future = futureTask;
                    futureTask.run();
                }
            }

            try
            {
                return future.get();
            }
            catch (CancellationException e)
            {
                cache.remove(arg, future);
            }
            catch (ExecutionException e)
            {
                throw Memoizer.launderThrowable(e);
            }
        }
    }

    private boolean notInCache(final Future<V> future)
    {
        return future == null;
    }

    private boolean wasAddedToCache(final Future<V> future)
    {
        return future == null;
    }

    private FutureTask<V> createFutureTask(final A arg)
    {
        final Callable<V> callable = new Callable<V>()
        {
            public V call() throws InterruptedException
            {
                return computable.compute(arg);
            }
        };
        return new FutureTask<V>(callable);
    }

    public static RuntimeException launderThrowable(final Throwable t)
    {
        if (t instanceof RuntimeException)
        {
            return (RuntimeException) t;
        }
        else if (t instanceof Error)
        {
            throw (Error) t;
        }
        else
        {
            throw new IllegalStateException("Not unchecked", t);
        }
    }
}
