package com.codurance.training.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utils.class, Task.class })
public class TaskListTest {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	PrintWriter out = new PrintWriter(System.out);

	@Mock
	Map<String, List<Task>> tasks;

	@Spy
	@InjectMocks
	TaskList service = new TaskList(in, out);

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		tasks = new LinkedHashMap<>();
	}

	@Test
	public void addProjectTest() {
		// given
		String project = "project1";
		List<Task> taskListExpected = new ArrayList<Task>();
		Map<String, List<Task>> myMapExpected = new HashMap<>();
		service.setTasks(myMapExpected);

		// when
		Map<String, List<Task>> myMapReturned = service.addProject(project);

		// then
		Assertions.assertThat(myMapReturned).isEqualTo(myMapExpected);
	}

	@Test
	public void addTaskTestWithNumericId() {
		// given
		String project = "project1";
		String task = "task1";
		Object id = "1";
		boolean done = false;
		LocalDate deadline = null;
		List<Task> taskList = new ArrayList<Task>();
		Map<String, List<Task>> myMap = new HashMap<>();
		myMap.put(project, taskList);

		service.setTasks(myMap);

		List<Task> taskListExpected = new ArrayList<Task>();
		Task myTaskExpected = new Task(project, id, task, done, deadline);
		taskListExpected.add(myTaskExpected);

		// when
		List<Task> taskReturned = service.addTask(project, task, id.toString());

		// then
		Assertions.assertThat(taskReturned.get(0).getProject()).isEqualTo(taskListExpected.get(0).getProject());
		Assertions.assertThat(taskReturned.get(0).getId()).isEqualTo(taskListExpected.get(0).getId());
		Assertions.assertThat(taskReturned.get(0).getDeadline()).isEqualTo(taskListExpected.get(0).getDeadline());
		Assertions.assertThat(taskReturned.get(0).getDescription()).isEqualTo(taskListExpected.get(0).getDescription());
		Assertions.assertThat(taskReturned.get(0).getTaskById(myMap, id.toString()))
				.isEqualTo(taskListExpected.get(0).getTaskById(myMap, id.toString()));
	}
	
	@Test
	public void addTaskTestWithStringId() {
		// given
		String project = "project1";
		String task = "task1";
		Object id = "monSuperIDNumberOne";
		boolean done = false;
		LocalDate deadline = null;
		List<Task> taskList = new ArrayList<Task>();
		Map<String, List<Task>> myMap = new HashMap<>();
		myMap.put(project, taskList);

		service.setTasks(myMap);

		List<Task> taskListExpected = new ArrayList<Task>();
		Task myTaskExpected = new Task(project, id, task, done, deadline);
		taskListExpected.add(myTaskExpected);

		// when
		List<Task> taskReturned = service.addTask(project, task, id.toString());

		// then
		Assertions.assertThat(taskReturned.get(0).getProject()).isEqualTo(taskListExpected.get(0).getProject());
		Assertions.assertThat(taskReturned.get(0).getId()).isEqualTo(taskListExpected.get(0).getId());
		Assertions.assertThat(taskReturned.get(0).getDeadline()).isEqualTo(taskListExpected.get(0).getDeadline());
		Assertions.assertThat(taskReturned.get(0).getDescription()).isEqualTo(taskListExpected.get(0).getDescription());
//		Assertions.assertThat(taskReturned.get(0).getTaskById(myMap, id.toString()))
//				.isEqualTo(taskListExpected.get(0).getTaskById(myMap, id.toString()));
	}

	@Test
	public void deadlineTest() {
		// given
		String commandLine = "1 2016-01-08";
		String[] subcommandRest = commandLine.split(" ", 2);
		String idString = subcommandRest[0];
		String dateString = subcommandRest[1];

		String PATTERN = "yyyy-MM-dd";
		PowerMockito.mockStatic(Utils.class);
		LocalDate date = LocalDate.of(2016, Month.JANUARY, 8);
		Mockito.when(Utils.getDateFromString(dateString, PATTERN)).thenReturn(date);

		Task taskExpected = new Task(null, 1, "task1", false, date);
		PowerMockito.mockStatic(Task.class);
		Mockito.when(Task.getTaskById(tasks, idString)).thenReturn(taskExpected);
		taskExpected.setDeadline(date);

		// when
		Task taskDeadline = service.deadline(commandLine);

		// then
		Assertions.assertThat(taskDeadline.isDone()).isEqualTo(taskExpected.isDone());
	}

	@Test
	public void getTaskByIdTest() {
		// given
		String idStringParam = "1";
		
		String project = "project1";
		String task = "task1";
		Object id = "1";
		boolean done = false;
		LocalDate deadline = null;
		List<Task> taskList = new ArrayList<Task>();
		Task myTask = new Task(project, id, task, done, deadline);
		taskList.add(myTask);
		Map<String, List<Task>> myMap = new HashMap<>();
		myMap.put(project, taskList);
		service.setTasks(myMap);
		
		// when 
		Task taskReturned = service.getTaskByIdString(idStringParam);
		
		// then
		Assertions.assertThat(taskReturned).isEqualTo(myTask);
	}
	
	@Test
	public void setUncheckTest(){
		// given
		String idStringParam = "1";
		boolean doneParam = false;
		
		String project = "project1";
		String task = "task1";
		boolean done = false;
		LocalDate deadline = null;
		List<Task> taskList = new ArrayList<Task>();
		Task taskExpected = new Task(project, idStringParam, task, done, deadline);
		taskList.add(taskExpected);
		Map<String, List<Task>> myMap = new HashMap<>();
		myMap.put(project, taskList);
		service.setTasks(myMap);
		
		// when
		Task taskReturned = service.setDone(idStringParam, doneParam);
		
		// then
		Assertions.assertThat(taskReturned.isDone()).isEqualTo(false);
	}
	
	@Test
	public void setCheckTest(){
		// given
		String idStringParam = "1";
		boolean doneParam = true;
		
		String project = "project1";
		String task = "task1";
		boolean done = true;
		LocalDate deadline = null;
		List<Task> taskList = new ArrayList<Task>();
		Task taskExpected = new Task(project, idStringParam, task, done, deadline);
		taskList.add(taskExpected);
		Map<String, List<Task>> myMap = new HashMap<>();
		myMap.put(project, taskList);
		service.setTasks(myMap);
		
		// when
		Task taskReturned = service.setDone(idStringParam, doneParam);
		
		// then
		Assertions.assertThat(taskReturned.isDone()).isEqualTo(true);
	}
}