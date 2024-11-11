function deleteUser(id) {
    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', `/users/${id}`, true);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                alert("User deleted successfully");

                // Remove the row from the table
                const userRow = document.getElementById(id);
                if (userRow) {
                    userRow.remove();
                }
            } else if (xhr.status === 404) {
                alert("User not found");
            } else {
                alert("Failed to delete user");
            }
        }
    };

    xhr.send();  // Send the DELETE request
}
