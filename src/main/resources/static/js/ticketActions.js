// Change stage function for the tickets' dashboard

document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".change-state-btn");

    buttons.forEach(button => {
        button.addEventListener("click", function (event) {
            event.preventDefault();

            const ticketId = this.getAttribute("data-ticket-id");
            const currentStatus = this.getAttribute("data-current-status");

            // Definire i possibili stati in sequenza
            const states = ["OPEN", "IN_PROGRESS", "DONE"];
            let newStateIndex = (states.indexOf(currentStatus) + 1) % states.length;
            let newState = states[newStateIndex];

            console.log(`Changing state of ticket ${ticketId} from ${currentStatus} to ${newState}`); // Debug

            fetch(`/tickets/${ticketId}/change-status`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ status: newState })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.setAttribute("data-current-status", newState);

                        // Trova e aggiorna il badge dello stato
                        const statusBadge = this.closest(".card-body").querySelector(".detail-status");
                        statusBadge.textContent = newState;
                    } else {
                        console.error("Error updating status:", data.message);
                    }
                })
                .catch(error => console.error("Fetch error:", error));
        });
    });
});

//Quick edit button

document.addEventListener('DOMContentLoaded', () => {
    const fields = document.querySelectorAll('#title, #description');
    fields.forEach(field => {

        field.addEventListener('blur', async (event) => {

            const element = event.target;
            const ticketId = element.dataset.ticketId;
            const fieldName = element.id;
            const newValue = element.value;

            try {
                const response = await fetch(`/tickets/${ticketId}`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `fieldName=${fieldName}&newValue=${encodeURIComponent(newValue)}`
                });

                if (response.ok) {
                    element.style.border = '2px solid green';
                } else {
                    element.style.border = '2px solid red';
                }
            } catch (error) {

                console.error('Errore durante l\'aggiornamento:', error);
                element.style.border = '2px solid red';
            }

            setTimeout(() => {
                element.style.border = '';
            }, 2000);
        });
    });
});