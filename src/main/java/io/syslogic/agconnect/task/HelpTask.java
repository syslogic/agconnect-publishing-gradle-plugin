package io.syslogic.agconnect.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Help {@link DefaultTask}
 *
 * @author Martin Zeitler
 */
public class HelpTask extends DefaultTask {

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        System.out.println("welp!");
    }
}
