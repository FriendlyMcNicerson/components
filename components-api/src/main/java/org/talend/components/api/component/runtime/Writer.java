/*
 * Copyright (C) 2015 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.talend.components.api.component.runtime;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * A Writer writes a bundle of elements from a PCollection to a sink. {@link Writer#open} is called before writing
 * begins and {@link Writer#close} is called after all elements in the bundle have been written. {@link Writer#write} writes an
 * element to the sink.
 *
 * <p>
 * Note that any access to static members or methods of a Writer must be thread-safe, as multiple instances of a Writer may be
 * instantiated in different threads on the same worker.
 *
 * <p>
 * See {@link Sink} for more detailed documentation about the process of writing to a Sink.
 *
 * @param <WriteT> The writer results type (e.g., the bundle's output filename, as String)
 */
public interface Writer<WriteT> {

    /**
     * Performs bundle initialization. For example, creates a temporary file for writing or initializes any state that
     * will be used across calls to {@link Writer#write}.
     *
     * <p>
     * The unique id that is given to open should be used to ensure that the writer's output does not interfere with the output of
     * other Writers, as a bundle may be executed many times for fault tolerance. See {@link Sink} for more information about
     * bundle ids.
     */
    void open(String uId) throws IOException;

    /**
     * Called for each value in the bundle.
     */
    void write(Object object) throws IOException;
    
    /**
     * Returns the value of the data item that was written by the last {@link #write} call. The returned value
     * must be effectively immutable and remain valid
     * indefinitely.
     *
     * <p>
     * Multiple calls to this method without an intervening call to {@link #write} should return the same result.
     *
     * @throws java.util.NoSuchElementException if {@link #write} was never called, or if
     */
    Object getCurrent() throws NoSuchElementException;

    /**
     * Finishes writing the bundle. Closes any resources used for writing the bundle.
     *
     * <p>
     * Returns a writer result that will be used in the {@link WriteOperation}'s finalization. The result should contain some way
     * to identify the output of this bundle (using the bundle id). {@link WriteOperation#finalize} will use the writer result to
     * identify successful writes. See {@link Sink} for more information about bundle ids.
     *
     * @return the writer result
     */
    WriteT close() throws IOException;

    /**
     * Returns the write operation this writer belongs to.
     */
    WriteOperation<WriteT> getWriteOperation();
}
