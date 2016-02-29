# ESTHER: Event Scheduler & Task Handler for Efficient Results

## User Guide

All of our commands will roughly follow this format.

A **command keyword** at the beginning will determine what type of command this is.

If required, a **Unique Task Reference (UTR)** points to a specific task. This is usually in the form of a *task* name (which is assigned by the user and may be a duplicate) or *task ID* (which is assigned by the program and is not a duplicate).

These are followed by a **fieldname keyword** which specifies the *field* and then the *value* of the field itself. There may be more than one of these “keyword-value” pairs. In the implementation of **keyword-value pairs**, we wanted the keyword to make as much sense in English as far as possible to increase ease of use for the user.

Required parameters for each command type are indicated by _underscores_ whilst optional parameters are indicated by [square brackets]

\_command keyword\_ [Unique Task Reference (UTR)] [fieldname keyword] [field value] 

We currently have 7 commands in the program. Their usage and some examples are illustrated below.


### Add task

The add command allows the user to add a task to his to-do list. A **task name** is required. The user may optionally include a *date* or *time* for the task using the keyword *on*.

**Usage:** add \_task name\_ [on] [date/time]

**Examples:**

add tea with grandma on tuesday 3pm

add tea with grandma on tomorrow 


### Delete task

The delete command allows the user to remove a task from his to-do list. Some reference to the task is required, such as a **name** or **ID**.

**Usage:** delete \_task name/ID\_ 

**Examples:**

delete tea with grandma


### Show tasks

The show command is a complex command with multiple parameters. It will instruct the program to display a range of tasks depending on the parameters used. There are four possible keywords: **by**, **before**, **on**, **after**. The **by** keyword can be used to specify *sort order*. The **before** keyword is to show all tasks before a certain *date/time*. The **on** keyword is to show all tasks on a specific *day*. The **after** keyword is used to show all tasks after a certain *date/time*.

**Usage:** show [by] [“date”/“time”] [before/on/after] [some date/time]

**Examples:**

show (shows all tasks)

show by time (same as) show by date (show all tasks, ordered chronologically)

show before Tuesday (show all tasks that are due on or before tuesday)

show by time before Tuesday (show all tasks due on/before Tuesday, ordered by time)

show on tomorrow (shows all tasks due tomorrow)

show after Thursday (shows all tasks due after Thursday)

show after Friday before Monday (shows all tasks due over the weekend)


### Update task

The update command is used to change any of the properties or fields of a current existing task. The task must first be **referenced** by *name* or *id*, then the *name of the field* to change, then a keyword “**to**”, then the *new field value* to update to.

**Usage:** update \_UTR\_ \_field name\_ \_to\_ \_new field value\_

**Examples:**

update Tea with grandma time to 3pm

update ST2334 Tutorial name to MA1101 Tutorial


### Sort tasks

Sorting is used when the user wishes to sort the external file that the program saves its data to. Since the file is meant to be readable, it may benefit the user to be sorted in the way he likes. The user must specify the *criteria or field* he wants to sort **by**.

**Usage:** sort \_by\_ \_criteria/field\_

**Examples:** 

sort by date

sort by name


### Mark task as done

After a user has completed a task, he can mark the *task* as done or *completed*. This can be thought of as a shortcut for the command **update completed**

e.g. update st2334 tutorial completed to completed

**Usage:** completed \_UTR\_

**Examples:**

completed ST2334 Tutorial


### Help

The help command will display help information in the form of a list of commands and detailed information for each.

**Usage:** help


### Undo

The undo command will be multi-step so the user can undo multiple steps in one go.

**Usage:** undo


## Non-functional requirements

**Performance**

1.	Response Time: Each command should have a response in ≤ 3 seconds.
  * Search functions such as “show” may require a longer response time with around 3 seconds.
  * Other functions such as “add” and “done” should have an immediate response.

2.	Survivability: In case of crashes, the text files used by ESTHER should not be damaged in any way and they are updated to the last command changes. ESTHER will be simply be required to be start up again.

3.	Usability: ESTHER is easy to understand and use.
  * ESTHER will run from a simple executable file without installation.
  * ESTHER has a similar interface with the command prompt so users will be able to easily understand how to use the software.
  * ESTHER only requires a keyboard to work.	
  * You can use the “help” command to see the list of commands and information for each. It also helps the user to understand how to use each command so not much effort is required to learn how to use ESTHER.
  * ESTHER uses commands that are commonly used in user-defined languages to allow users to easily adapt to the software.

4.	Reliability: ESTHER meets its goal in being a task management tool. It is able to create tasks and give reminders to the user about upcoming tasks.

5.	Availability: ESTHER is available 24/7.

6.	Integrity: ESTHER is a stand-alone software. It reads from a text file for the list of tasks. The tasks can be modified directly in the text file. The user is responsible for the integrity of the text file.

**Adaptation**

1.	Portability: ESTHER is a standalone software that is easily installable in any operating system.
  * ESTHER is available only on Windows.

2.	Expandability: Any further upgrades or enhancements on the existing software will require the user to connect to the internet to allow ESTHER to download the upgrade.

**Constraints**

1.	ESTHER only allows a maximum of 200 tasks carried out at any given time. Beyond 200, the task will be added to another text file to be stored for manual reading.
