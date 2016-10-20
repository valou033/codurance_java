package com.codurance.training.tasks;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class Task<T> {
	private String project;
    private final T id;
    private final String description;
    private boolean done;
    private LocalDate deadline;

	public Task(String nameProject, T id, String description, boolean done, LocalDate deadline) {
        this.project = nameProject;
		this.id = id;
        this.description = description;
        this.done = done;
        this.deadline = deadline;
    }

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public T getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
    
    public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
	
	public static Task getTaskById(Map<String, List<Task>> tasks, String idString){
    	Long id = Long.parseLong(idString);
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
            	if ((task.getId()).getClass().isInstance(id)){
            		Long idLong = Long.class.cast((task.getId()));
            		if (idLong == id) {
                        return task;
                    }
            	}
            }
        }
        return null;
    }
	
	public static boolean taskDeadlineIsToday(Task task){
		if (task.getDeadline() != null && task.getDeadline().equals(LocalDate.now())){
			return true; 
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("    [").append((this.isDone() ? 'x' : ' ')).append("] ").append(this.getId())
		.append(": ").append(this.getDescription()).append(" - deadline: ").append(this.deadline);
		
		return result.toString();
	}
}