This project is created as part of EK24 training program.

This project is a Task Management System built with Spring Boot that enables users to efficiently manage tasks with features for task creation, updating, and searching. Users can create, update, delete, and track their tasks, set deadlines, prioritize tasks. The Application includes user authentication, a dashboard to overview tasks, notifications  and analytic for task completion.

# Features

•	User Authentication: User registration and login functionality. There are 2 types of users ( ADMIN,USER ). USER can create/edit/delete tasks .
ADMIN can remove registered users.

•	Task Management: USER can create tasks by giving task details , priority , deadline and reminder. If no reminder is given, it is automatically set to 1hr before deadline. USER can edit tasks, mark them complete or delete them.

•	Notifications: In-app notifications are provided for task creation/completion/deletion.
For task deadlines, notifications are sent through email and also shown in-app.

•	Search and Filter: Search tasks by title or description. Filter tasks by status
(Completed, Pending) and priority.

# Setup and Usage

## Prerequisites 

You need docker compose installed on your machine

## Setup and Usage

Clone this repository.

Navigate to project folder.

Create a file env.env and enter the following environment variables

Note: The actual values for the username and password variables are not provided here since it is sensitive information, instead it is provided in the project documentation. Please use the credentials in the documentation or substitute them with your own email credentials to enable email notifications.

```
EXT_SPRING_MAIL_USERNAME='your_email_username'
EXT_SPRING_MAIL_PASSWORD='your_email_password'
EXT_SPRING_MAIL_HOST='your_email_host'
EXT_SPRING_MAIL_PORT='mail_port'
```
If using Gmail credentials , you can use the following values
```
EXT_SPRING_MAIL_HOST='smtp.gmail.com'
EXT_SPRING_MAIL_PORT='587'
```
If you are on windows machine and env.env file is not detected, put the environment variables in .env file.

Open terminal in project root

Start project with 

```
docker compose up
```

Visit localhost:8000

