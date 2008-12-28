/*
 * Milyn - Copyright (C) 2006
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.cache;

/**
 * A computable is a task that should take some time to execute.
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 * @param <A> The object type of the argument that will be used in the computation.
 * @param <V> The object return type that will be the result of the computation.
 */
public interface Computable<A, V>
{
    /**
     * The computable method.
     *
     * @param arg The argument for this computation.
     * @param V The object type that will be the result of the computation.
     * @throws InterruptedException
     */
    V compute(A arg) throws InterruptedException;
}
