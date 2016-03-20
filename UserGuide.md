# User Guide for ESTHER

## Introduction
Welcome to the user guide for **ESTHER**! **ESTHER** stands for Event Scheduler and Task Handler for Efficient Results, and her main function is to help you manage your time. Here we’ll teach you the basics of how she works, so she can help you make the most of your busy schedule.


## Getting Started
To begin using ESTHER, launch the executable file (**esther.exe**) from a directory of your choice. When starting, **ESTHER** scans the local directory for saved information. If she finds none, she will proceed as if starting for the first time. After the first run, **ESTHER** saves any tasks and other information in the local directory.


## Commands
Commands are the bread and butter of **ESTHER**. In this section, we’ll teach you the various functions that **ESTHER** supports and the commands required to make them happen (as well as the different formats these commands may require). [] indicates a field that’s up to you.

### _Add_
The **add** command allows you to create a task. There are three formats to the add command: without deadline, with deadline and with duration.

**Without deadline**

Use this format for tasks that do not have a set deadline.

**>> add [Task name]**

Your professor told you during the lecture that there is going to be an assignment. You want to record it down so that you would not forget it later. However, the professor has yet to decide on the final submission date. 

`>> add CS2105 assignment`

You can update this task later when your professor has made up his mind.


**With deadline**

Use this format for tasks with a set deadline. 

**>> add [Task name] on / by [date/time]**

Unfortunately, your other professor has also given you an assignment, due by 1 Apr 2016 at 4pm. You can add it to **ESTHER** as such.

`>> add MA1101R Assignment by 1 April 4pm`

The on/by field is a keyword field used to indicate a following date or time. If your task name also uses those words, enclose your entire task name within a pair of quotes.

`>> add “cake by the ocean” by apr 2 2359`


**With duration**

Use this format for events with a set duration.

**>> add [Task name] from [date/time] to [date/time]**

One of your professors has also announced a test next Monday from 2-3pm.

`>> add LSM1301 test from next Monday 2pm to next Monday 3pm`


**Examples of acceptable date and time formats**

Most “normal” date and time formats are acceptable. For a more complete list, please refer to the cheat sheet at the back of this user guide.

*	13/02/2015 or 13.02.2015
*	13 February 2015
*	Feb 13, 2015
*	13 Feb

For time: 
*	3pm or 4AM
*	1500


### _Update_
The **update** command allows you to change existing tasks. 

**>> update [taskname] [taskfield] to [newvalue]**

In the next lecture, your professor announced the deadline of the earlier assignment.

`>> update CS2105 assignment date to 3 April`

The date will now be updated in the associated task. These are the fields you can update (they are not case sensitive).

*	startDate (will change the task to one with duration)
*	endDate
*	startTime (see start date)
*	endTime
*	taskName


### _Delete_
The **delete** command is used to remove any tasks completely, without marking them as complete. Use this command when you have entered a task incorrectly, or the task no longer needs to be done.

**>> delete [taskname]**

By popular demand, your professor has decided to cancel the test. Hallelujah! Now you can delete the test task.

`>> delete LSM1301 test`

Alternatively, you can also delete it by using its task id.

`>> delete 3`


### _Complete_
The **complete** command is used to mark tasks as complete. Completed tasks will only show up at the bottom of the list or when searched for.

**>> complete [taskname/taskID]**

It’s 1 April. You have submitted your assignment. Now you can mark the task as complete.

`>> complete CS2105 assignment`


### _Sort_
The **sort** command can be used to reorder the tasks you have according to alphabetical order or deadline. Your new sort order will also be saved to file.

**>> sort by [name/date/time]**

If your tasks become jumbled up for some reason, use this command to resort your tasks in your desired order.

`>> sort by date`


### _Undo_
Use this command to **undo** previously entered commands. You can enter undo multiple times to undo multiple steps. Mistakes are a thing of the past!

**>> undo**


### _Help_
Last but not least, the **help** command provides you with a brief version of what you see in this guide.

**>> help**


## Troubleshooting
### _How to use this troubleshooting guide_
Sometimes, you may run into an error when using **ESTHER**. When that happens, she will show you an error message, as seen below.

[logo]: https://drive.google.com/file/d/0B3_4TQRv9s3SbVBhZjVhODlINGM/view?usp=sharing "Where to find the error message"

This troubleshooting guide shows you all possible error messages you may encounter when using **ESTHER**. Please find the section in this guide that corresponds to the error message you see on **ESTHER** and follow the instructions in that section to get **ESTHER** to work normally again.

### _Possible errors_
**1. "A system error has occured in ESTHER. Please restart this application."**

This indicates that **ESTHER** has encountered a system error in your computer and is unable to work normally.
Therefore, you should stop using **ESTHER** and restart **ESTHER** **_immediately_**.
If **ESTHER** still shows this error message after you have done the above, please delete **ESTHER** and download **ESTHER** again.


**2. "Command is not recognized. Type 'help' to see the list of commands that can be used in ESTHER."**

This means that you have entered something that **ESTHER** doesn’t recognise. You can either review what commands are accepted by **ESTHER** in this guide, or use the `help` command in **ESTHER**.


**3. "Unable to add task: Please check that your input is of the correct format."**

This means that while your input was recognised, it was not in the format required by **ESTHER**. Please review this guide or use the `help` command in **ESTHER** for more information.


**4. "Unable to add task: Task name is required."**

There are 2 situations for this:

a) _You did not enter a task name_

Please make sure you do not forget to specify the task name of the task you want to add, when running the `add` command in **ESTHER**.

b) _Your task name may contain sensitive keywords._

**ESTHER** picks up certain commands via certain keywords (such as `at`, `by`, `etc`), and your task name contains such keywords. Therefore, please place quotation marks (`""`) around the task name of the task you want to add, as shown below:

`>> add "Meet Joe at mall" at 3pm`


**5. "Unable to delete/update task: Please supply a proper task name or task ID."**

This means that **ESTHER** could not find the task you mentioned by either task name or task ID. Please make sure you used the exact name or a valid task ID.

For example, if you want to delete a task in your list called "`Meet Joe at mall`", please specify the task to be deleted as "`Meet Joe at mall`" and not something like "`meet joe at mall`”.

If you are not sure what the name or ID of your task is, use the `show` command and refer to the list of tasks shown in **ESTHER** (see screenshot below).

[logo]: https://drive.google.com/file/d/0B3_4TQRv9s3SZDItLS15TFA2Y2M/view?usp=sharing "What you should see after running the `show` command"


**6. "Unable to update task: The field you have specified does not exist."**

This means that you have entered an invalid field name. Refer to the `update` section or use the `help` command for more information.


### _Cheatsheet_
This section shows you a quick glance of all the commands that can be used in **ESTHER**.

|     Command	    |                                Format                           |              Remarks                           |
| :-------------: | :-------------------------------------------------------------- | :--------------------------------------------- |
|   Add task	     | `>> add [Task name]` <br> `>> add [Task name] [on/by] [date/time]` <br> `>> add [Task name] from [date/time] to [date/time]`       |	See ‘Acceptable Date and Time Formats’ below for `[date/time]`.                  |
|   Update task	  | `>> update [taskname/taskID] [taskfield] to [new value]`        | Available `[taskfield]`:<br> 1. `startDate`<br> 2. `endDate`<br> 3. `startTime`<br> 4. `endTime`<br> 5. `taskName` |
| Delete task	    | `>> delete [taskname/taskID]`	                                  | `[taskname]` must be the exact name of a task in the list.         |
| Complete task	  | `>> complete [taskname/taskID]` 	                               | `[taskname]` must be the exact name of a task in the list.         |
| Sort tasks	     | `>> sort by [criteria]`                                         |	Available `[criteria]`: <br> 1. `name`<br> 2. `date`<br> 3. `time`  |
| Undo	           | `>> undo`                                                       |                                                |


**Acceptable Date and Time formats**

| Relative Date Format |	    Examples    	|               Meaning               |
| -------------------- | :--------------- | :---------------------------------- |
| `next MM`            |	next Monday	     | The closest Monday from now         |
| `this MM`	           | this Tuesday	    | The Tuesday in the current week     |
| `now`	               | now	             | Today’s date and time               |
| `today`	             | today	           | Today’s date and time               |
| `tomorrow`           |	tomorrow	        | 1 day after today                   |

**Fixed Date Format	Examples**

|     Format     |    Examples                       |
| -------------- | --------------------------------- |
| `dd/MM/yyyy`	  | 12/02/2015                        |
| `dd.MM.yyyy`	  | 12.02.2015                        |
| `dd-MM-yyyy`   |	12-02-2015                        |
| `dd MM yyyy`   |	12 02 2015                        |
| `ddMMyyyy`	    | 12022015                          |
| `dd MMM yyyy`	 | 12 February 2015 or 12 Feb 2015   |
| `ddMMMyyyy`	   | 12February2015 or 12Feb2015       |
| `dd MMM, yyyy` |	12 February, 2015 or 12 Feb, 2015 |
| `MMM dd yyyy`	 | February 12 2015 or Feb 12 2015   |
| `MMM dd, yyyy`	| February 12, 2015 or Feb 12, 2015 |
| `MMMdd yyyy`   |	February12 2015 or Feb12 2015     |
| `dd/MM`        |	12/02                             |
| `dd MMM`	      | 12 February or 12 Feb             |
| `ddMMM`	       | 12February or 12Feb               |
| `MMM dd`       |	February 12 or Feb 12             |
| `MMMdd`	       | February12 or Feb12               |

**Time format	Examples**

|  Format  |   Examples   |
| -------- | ------------ |
| `hh:mma` |	03:00PM      |
| `hh:mm a`|	03:00 PM     |
| `hh-mma` |	03-00PM      |
| `hh-mm a`|	03-00 PM     |
| `hhmma`	 | 0300PM       |
| `hhmm a` |	0300 PM      |
| `HH:mm`	 | 15:00        |
| `HH-mm`  | 15-00        |
| `HHmm`   |	1500         |
| `HH.mm`	 | 15.00        |
| `hha`    |	3PM          |
| `hh a`	  | 3 PM         |
| `HH`     |	15           |
