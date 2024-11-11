function createTask() {
    const form = document.getElementById('create_task_form');
    const formData = new FormData(form);


    console.log(formData);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/tasks', true);
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
    xhr.setRequestHeader("Content-Type", "application/json");


    const title = formData.get('title')
    const desc = formData.get('description')
    const deadline = formData.get('deadline')
    const priority = formData.get('priority')
    const reminder = formData.get('reminder')



    const taskDTO = {
        title: title,
        description: desc,
        deadline: deadline,
        priority: priority,
        reminder: reminder
    };

    console.log(title+" , "+desc);
    console.log(deadline+" , "+priority+" "+reminder);
    console.log(taskDTO);



    xhr.onload = function () {

        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                alert("Your task was created successfully ");
                return;
            }

            if (xhr.status === 500 || xhr.status === 409 || xhr.status === 400) {
                alert(xhr.responseText);
                return false;
            }


        }
    }

    xhr.send(JSON.stringify(taskDTO));
}

function deleteTask(taskId) {
    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', `/tasks/${taskId}`, true);

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {

                const taskRow = document.getElementById(taskId);

                console.log(taskRow+" bah")
                if (taskRow) {
                    taskRow.remove();
                }
                alert("Task deleted successfully");
                // Optionally, you could refresh the task list or remove the deleted task from the UI
            } else if (xhr.status === 404) {
                alert("Task not found");
            } else {
                alert("Failed to delete task");
            }
        }
    };

    xhr.send();
}

function openEditTaskPage(id)
{
    location.href = "/tasks/" + id + "/update_page";
}

function editTask(taskId) {





    const form = document.getElementById('update_task_form');
    const formData = new FormData(form);




    const title = formData.get('title')
    const desc = formData.get('description')
    const deadline = formData.get('deadline')
    const priority = formData.get('priority')
    const reminder = formData.get('reminder')



    const taskDTO = {
        title: title,
        description: desc,
        deadline: deadline,
        priority: priority,
        reminder: reminder
    };

    // Create a new XMLHttpRequest
    const xhr = new XMLHttpRequest();
    xhr.open("PUT", `/tasks/${taskId}`, true);
    xhr.setRequestHeader("Content-Type", "application/json");

    // Define what happens on successful response
    xhr.onload = function () {
        if (xhr.status === 200) {
            // Redirect to /tasks if update is successful
            window.location.href = "/tasks/page";
        } else {
                alert("Error updating task\n"+xhr.responseText);
                console.log(xhr.responseText);
        }
    };

    // Define what happens in case of error
    xhr.onerror = function () {
        alert("Request failed. Please check your network connection.");
    };

    // Send the request with the taskDTO object as JSON
    xhr.send(JSON.stringify(taskDTO));
}



function completeTask(id) {
    const xhr = new XMLHttpRequest();
    xhr.open('PUT', `/tasks/${id}/complete`, true);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {


                // Locate the task row using the task ID
                const taskRow = document.getElementById(id);

                console.log(taskRow)

                if (taskRow) {
                    // Remove the "Complete" button
                    const completeButton = taskRow.querySelector('.bg-green-500');
                    console.log(completeButton)
                    if (completeButton) {
                        completeButton.remove();
                        console.log("ga")
                    }

                    const editButton = taskRow.querySelector('.bg-yellow-500');
                    console.log(editButton)
                    if (editButton) {
                        editButton.remove();
                        console.log("ga")
                    }

                    // Set the "completed" text to "true"
                    const completionCell = taskRow.querySelector('.completion-status');
                    if (completionCell) {
                        completionCell.textContent = "true";
                        console.log("ma")
                    }
                }
                alert("Task marked as completed");
            } else if (xhr.status === 404) {
                alert("Task not found");
            } else {
                alert("Failed to mark task as completed");
            }
        }
    };

    xhr.send();  // Send the PUT request without a body
}
