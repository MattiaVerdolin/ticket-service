// show details for a specific ticket in the dashboard

function showTicketDetails(ticketId) {
    fetch(`/tickets/details/${ticketId}`)
        .then(response => {

            if (!response.ok) {
                throw new Error('Network response was not ok, HTTP error Status = ' + response.status);
            }
            return response.json();
        })
        .then(ticket => {
            document.getElementById('ticket-title').textContent = ticket.title;
            document.getElementById('ticket-creation-date').textContent = ticket.creationDate;
            document.getElementById('ticket-due-date').textContent = ticket.dueDate;
            document.getElementById('ticket-time-estimate').textContent = ticket.timeEstimate;
            document.getElementById('ticket-time-spent').textContent = ticket.timeSpent;
            document.getElementById('ticket-author').textContent = ticket.author;
            document.getElementById('ticket-assignee').textContent = ticket.assignee;
            document.getElementById('ticket-status').textContent = ticket.status;
            document.getElementById('ticket-type').textContent = ticket.type;

            document.getElementById('ticket-details').style.display = 'block';
        })
        .catch(error => console.error('Error fetching ticket details:', error));
}

// hide the detail tickets' box

function hideTicketDetails() {
    const ticketDetails = document.getElementById('ticket-details');
    ticketDetails.style.display = 'none';
    console.log('Details hidden');
}