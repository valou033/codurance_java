package com.codurance.training.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskList implements Runnable {
	private static final String QUIT = "quit";

	private Map<String, List<Task>> tasks = new LinkedHashMap<>();

	private final BufferedReader in;
	private final PrintWriter out;

	private final String PATTERN = "yyyy-MM-dd";
	private long lastId = 0;

	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		new TaskList(in, out).run();
	}

	public TaskList(BufferedReader reader, PrintWriter writer) {
		this.in = reader;
		this.out = writer;
	}

	public void run() {
		while (true) {
			out.print("> ");
			out.flush();
			String command;
			try {
				command = in.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (command.equals(QUIT)) {
				break;
			}
			execute(command);
		}
	}

	/**
	 * Method which execute user menu for beginning application
	 * 
	 * @param commandLine
	 */
	private void execute(String commandLine) {
		String[] commandRest = commandLine.split(" ", 2);
		String command = commandRest[0];
		switch (command) {
		case "view":
			switch (commandRest[1]) {
			case "by project":
				viewByProject(true);
				break;
			case "by deadline":
				viewByDeadline();
				break;
			}
			break;
		case "add":
			add(commandRest[1]);
			break;
		case "check":
			check(commandRest[1]);
			break;
		case "uncheck":
			uncheck(commandRest[1]);
			break;
		case "delete":
			delete(commandRest[1]);
			break;
		case "deadline":
			deadline(commandRest[1]);
			break;
		case "today":
			viewByProject(false);
			break;
		case "help":
			help();
			break;
		default:
			error(command);
			break;
		}
	}
	
	
	/**
	 * Add a new project or a new task
	 * 
	 * @param commandLine
	 */
	private void add(String commandLine) {
		String[] subcommandRest = commandLine.split(" ", 2);
		String subcommand = subcommandRest[0];
		if (subcommand.equals("project")) {
			addProject(subcommandRest[1]);
		} else if (subcommand.equals("task")) {
			String[] projectTask = subcommandRest[1].split(" ", 3);
			String idTask = null;
			// if id paramemeter is optional
			if (projectTask.length == 3) {
				idTask = projectTask[2];
			}
			if (Utils.isPatternOk(idTask)) {
				addTask(projectTask[0], projectTask[1], idTask);
			} else {
				out.printf("Spaces and special characters from the ID is disallowed");
				out.println();
			}
		}
	}

	/**
	 * Add a new project
	 * 
	 * @param name
	 * @return TODO
	 */
	public Map<String, List<Task>> addProject(String name) {
		tasks.put(name, new ArrayList<Task>());
		return tasks;
	}

	/**
	 * Add a new task
	 * 
	 * @param project
	 * @param description
	 * @param id
	 * @return
	 */
	public List<Task> addTask(String project, String description, String id) {
		List<Task> projectTasks = tasks.get(project);
		if (projectTasks == null) {
			out.printf("Could not find a project with the name \"%s\".", project);
			out.println();
			return projectTasks;
		}
		// ID auto generated
		if (id == null) {
			projectTasks.add(new Task<Long>(project, nextId(), description, false, null));
		} else { // ID choosen by user
			projectTasks.add(new Task<String>(project, id, description, false, null));
		}
		return projectTasks;
	}

	/**
	 * View tasks ordered by deadline
	 */
	public void viewByDeadline() {
		List<Task> listTask = new ArrayList<>();
		for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
			// get task list
			for (Task task : project.getValue()) {
				listTask.add(task);
			}
			// display task list ordered
			Collections.sort(listTask, (Task t1, Task t2) -> t1.getDeadline().compareTo(t2.getDeadline()));
			for (Task t : listTask) {
				out.printf("%s", t.toString());
			}
		}
	}

	/**
	 * View tasks ordered by projet
	 * 
	 * @param showAll
	 *            : indicates if command "all tasks due today" are active or not
	 */
	private void viewByProject(boolean showAll) {
		StringBuilder result = new StringBuilder("");
		for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
			boolean projectWithTodayDeadline = false;
			result.append(" --- ").append(project.getKey()).append(" --- \n ");
			for (Task task : project.getValue()) {
				projectWithTodayDeadline = false;
				if (showAll || Task.taskDeadlineIsToday(task)) {
					projectWithTodayDeadline = true;
					result.append(task.toString());
				}
			}
			if (showAll || projectWithTodayDeadline) {
				out.printf("%s", result);
			}
			out.println();
		}
	}

	/**
	 * Indicates deadline for a task
	 * 
	 * @param commandLine
	 * @return
	 */
	public Task deadline(String commandLine) {
		String[] subcommandRest = commandLine.split(" ", 2);
		String idString = subcommandRest[0];
		String dateString = subcommandRest[1];

		Task taskToDeadline = Task.getTaskById(tasks, idString);
		LocalDate date = Utils.getDateFromString(dateString, PATTERN);
		taskToDeadline.setDeadline(date);
		return taskToDeadline;
	}

	/**
	 * Delete a task by its ID
	 * 
	 * @param commandLine
	 */
	private void delete(String commandLine) {
		String[] subcommandRest = commandLine.split(" ", 2);
		String subcommand = subcommandRest[0];
		String idString = subcommandRest[1];
		if (subcommand.equals("task")) {
			Task task = getTaskByIdString(idString);
			List<Task> list = new ArrayList<Task>();
			list.add(task);
			List<Task> projectTasks = tasks.get(task.getProject());
			projectTasks.remove(task);
			tasks.replace(task.getProject().toString(), projectTasks);
		}
	}

	/**
	 * Indicates that a task is done
	 * 
	 * @param idString
	 *            : Task ID
	 */
	private void check(String idString) {
		setDone(idString, true);
	}

	/**
	 * Indicates that a task is undone
	 * 
	 * @param idString
	 *            : Task ID
	 */
	private void uncheck(String idString) {
		setDone(idString, false);
	}
	
	private List<String> listTest;
	public List<String> testList(){
		listTest = new ArrayList<String>();
		String a = listTest.toString();
		return listTest;
	}

	/**
	 * Get task by ID
	 * 
	 * @param <T>
	 * @param idString
	 *            : Task ID
	 * @return
	 */
	public <T> Task getTaskByIdString(String idString) {
		Object id = null;
		Object idParam = null;
		
		for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
			for (Task task : project.getValue()) {
				if (Utils.isNumeric(idString) && task.getId() instanceof Long) {
						id = Long.class.cast(task.getId());
						idParam = Long.parseLong(idString);
				} else {
					if (task.getId() instanceof String) {
						id = String.valueOf(task.getId());
						idParam = idString;
					}
				}
				if (idParam != null && idParam.equals(id)) {
					return task;
				}
			}
		}
		return null;
	}

	/**
	 * Done a task
	 * 
	 * @param idString
	 *            : task ID
	 * @param done
	 *            : boolean - done or undone the task
	 * @return TODO
	 */
	public Task setDone(String idString, boolean done) {
		Task task = getTaskByIdString(idString);
		if (task != null) {
			task.setDone(done);
		}
		out.printf("Could not find a task with an ID of %s.", idString);
		out.println();
		return task;
	}

	/**
	 * Display all commands available
	 */
	private void help() {
		out.println("Commands:");
		out.println("  add project <project name>");
		out.println("  add task <project name> <task description>");
		out.println("  delete task <ID>");
		out.println("  view by deadline");
		out.println("  view by project");
		out.println("  check <task ID>");
		out.println("  uncheck <task ID>");
		out.println("  deadline <task ID> <date>");
		out.println("  today");
		out.println();
	}

	/**
	 * Display an error
	 * 
	 * @param command
	 *            - Command passed in parameters
	 */
	private void error(String command) {
		out.printf("I don't know what the command \"%s\" is.", command);
		out.println();
	}

	/**
	 * Increase ID number for tasks
	 * 
	 * @return
	 */
	private long nextId() {
		return ++lastId;
	}
	
	/**
	 * Get task list map
	 * @return
	 */
	public Map<String, List<Task>> getTasks() {
		return tasks;
	}
	
	/**
	 * Set a task list map
	 * @param list
	 * @return
	 */
	public Map<String, List<Task>> setTasks(Map<String, List<Task>> list) {
		return tasks = list;
	}
}