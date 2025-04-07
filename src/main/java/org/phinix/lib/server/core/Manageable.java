package org.phinix.lib.server.core;

import org.phinix.lib.server.core.task.Task;

/**
 * {@code Manageable} interface is a marker interface for manageable components in the server.
 * Classes implementing this interface can implement running async {@link Task} instances.
 * <p>
 * This interface is implemented by instance of servers, workers and sessions
 *
 * @see Task
 */
public interface Manageable {}
