package net.soti.go.plugin.notification.email.model;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PipelineMaterial {
    private final String name;
    private final String counter;

    public PipelineMaterial(String name, String counter) {
        this.name = name;
        this.counter = counter;
    }

    public String getPipeineName() {
        return name;
    }

    public String getPipelineCounter() {
        return counter;
    }
}
