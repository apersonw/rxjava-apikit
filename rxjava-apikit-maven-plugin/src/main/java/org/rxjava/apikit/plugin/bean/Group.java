package org.rxjava.apikit.plugin.bean;

import lombok.*;

import java.util.List;

/**
 * @author happy
 */
@Getter
@Setter
@ToString
public class Group {
    private List<Task> tasks;
    private String rootPackage;
    public Group() {
    }
    public Group(List<Task> tasks, String rootPackage) {
        this.tasks = tasks;
        this.rootPackage = rootPackage;
    }
}
