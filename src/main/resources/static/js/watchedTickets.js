// Handles ticket watch functionality dynamically

document.addEventListener('click', (event) => {
    const button = event.target.closest('.watch-button');
    if (!button) return;

    const ticketId = button.getAttribute('data-id');
    const isWatched = button.getAttribute('data-watched') === 'true';
    if (isWatched) return;

    button.querySelector('span:nth-child(2)').textContent = 'Watched';
    button.classList.replace('btn-outline-info', 'btn-info');
    button.querySelector('span:nth-child(1)').classList.replace('bi-eye', 'bi-check');
    button.setAttribute('data-watched', 'true');

    fetch(`/tickets/watch/${ticketId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to add ticket to watches');
            return response.text();
        })
        .then(message => {
            const watchCountElement = document.querySelector('#watch-count');
            if (watchCountElement) {
                watchCountElement.textContent = parseInt(watchCountElement.textContent, 10) + 1;
            }
            console.log(message);
        })
        .catch(error => {
            console.error('Error:', error);
            button.querySelector('span:nth-child(2)').textContent = 'Watch';
            button.classList.replace('btn-info', 'btn-outline-info');
            button.querySelector('span:nth-child(1)').classList.replace('bi-check', 'bi-eye');
            button.setAttribute('data-watched', 'false');
        });
});
