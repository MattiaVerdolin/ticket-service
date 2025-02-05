const context = document.querySelector('base').getAttribute('href');
const searchInput = document.querySelector("#search-input");

searchInput.addEventListener("keyup", function () {
    console.log(searchInput.value)
    if (searchInput.value.length > 2)
        search();
    if (searchInput.value.length === 0)
        search();
})

function search() {
    const url = context + "ticket/search?q=" + searchInput.value;
    const options = {method: "GET"};

    fetch(url, options).then(function (response) {
        //check network error LS 10dic20
        if (!response.ok) {
            throw new Error('Network response was not ok, HTTP error Status = ' + response.status);
        }
        return response.json();
    }).then(function (tickets) {
        const container = document.querySelector("#content_container");

        let articles = '';
        for (var i = 0; i < tickets.length; i++) {
            const date = new Date(tickets[i].date);
            const dateFormatted = date.getHours() + ':' + date.getMinutes() + ' ' + date.getDate() + '/' + (date.getMonth() + 1) + "/" + date.getFullYear();
            articles +=
                '<article class="col-sm-6 col-md-4">\n' +
                '    <div class="card mb-4 shadow-sm">\n' +
                '        <div class="card-body">\n' +
                '            <p style="color:grey">\n' +
                '                <span>#' + tickets[i].id + '</span>\n' +
                '               <span class="badge bg-primary detail-status">' + tickets[i].status + '</span>\n' +
                '                <strong>' + tickets[i].type + '</strong> | <span>' + dateFormatted + '</span> by <a href="#">' + tickets[i].author + '</a>\n' +
                '           </p>\n' +
                '           <strong><span class="card-title">' + tickets[i].title + '</span></strong>\n' +
                '            <hr>\n' +
                '                <p class="card-description"><span>' + tickets[i].description + '</span>\n' +
                '                </p>\n' +
                '                <div class="d-flex justify-content-between align-items-center">\n' +
                '                    <div class="btn-group">\n' +
                '                        <a class="btn btn-sm btn-outline-secondary" href="' + context + 'ticket/' + tickets[i].id + '">View</a>\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '        </div>\n' +
                '    </div>\n' +
                '</article>\n';
        }

        if (tickets.length == 0) {
            articles = '<article class="col-md-4"><p>Nessun ticket trovato</p></article>';
        }

        container.innerHTML = '<h2 class="mt-4">Risultati ricerca: ' + searchInput.value + ' <a class="btn btn-sm btn-danger" href="' + window.location.href + '">chiudi</a></h2>' +
            '<section class="row">\n' +
            '        \n' +
            articles +
            '            \n' +
            '        \n' +
            '    </section>';

    });
}

/**
 * Mostra i dettagli di un ticket specifico.
 * Effettua una richiesta AJAX utilizzando `fetch` per ottenere i dettagli del ticket
 * dal server e aggiorna dinamicamente il contenuto del riquadro dei dettagli.
 *
 * @param {number} ticketId - L'ID del ticket di cui si vogliono visualizzare i dettagli.
 */
function showTicketDetails(ticketId) {
    // Effettua una richiesta GET al server per ottenere i dettagli del ticket
    fetch(`/tickets/details/${ticketId}`) // Assicurati che il percorso corrisponda a un endpoint esistente
        .then(response => {
            // Controlla se la risposta è andata a buon fine
            if (!response.ok) {
                throw new Error('Network response was not ok, HTTP error Status = ' + response.status);
            }
            return response.json(); // Converte la risposta in JSON
        })
        .then(ticket => {
            // Aggiorna il contenuto HTML del riquadro dei dettagli con i dati del ticket
            document.getElementById('ticket-title').textContent = ticket.title;
            document.getElementById('ticket-creation-date').textContent = ticket.creationDate;
            document.getElementById('ticket-due-date').textContent = ticket.dueDate;
            document.getElementById('ticket-time-estimate').textContent = ticket.timeEstimate;
            document.getElementById('ticket-time-spent').textContent = ticket.timeSpent;
            document.getElementById('ticket-author').textContent = ticket.author;
            document.getElementById('ticket-assignee').textContent = ticket.assignee;
            document.getElementById('ticket-status').textContent = ticket.status;
            document.getElementById('ticket-type').textContent = ticket.type;

            // Mostra il riquadro dei dettagli rendendolo visibile
            document.getElementById('ticket-details').style.display = 'block';
        })
        .catch(error => console.error('Error fetching ticket details:', error)); // Gestisce eventuali errori
}

/**
 * Nasconde il riquadro dei dettagli del ticket.
 * Modifica lo stile del riquadro dei dettagli per renderlo invisibile.
 */
function hideTicketDetails() {
    // Seleziona l'elemento HTML del riquadro dei dettagli
    const ticketDetails = document.getElementById('ticket-details');

    // Nasconde il riquadro impostando il display su 'none'
    ticketDetails.style.display = 'none';

    // Debug: registra un messaggio nella console per indicare che il riquadro è stato nascosto
    console.log('Details hidden');
}


/**
 * Questo script utilizza l'Event Delegation per gestire i clic su tutti i pulsanti "watch-button".
 * Invece di aggiungere un evento `click` su ogni pulsante, si ascolta l'evento a livello di `document`.
 * Questo approccio è più efficiente e funziona anche per gli elementi aggiunti dinamicamente al DOM.
 * Il comportamento del pulsante viene aggiornato immediatamente al clic, con un feedback visivo,
 * mentre una chiamata al server aggiorna lo stato nel backend.
 */

document.addEventListener('click', (event) => {
    // **1. Identificazione del pulsante cliccato**
    // Utilizza `event.target.closest` per trovare l'elemento con la classe 'watch-button'
    // più vicino al punto in cui è avvenuto il clic. Questo gestisce anche i clic su elementi
    // interni (es. icone o testo) all'interno del pulsante.
    const button = event.target.closest('.watch-button');

    // Se il clic non è avvenuto su un pulsante valido, esci immediatamente dalla funzione.
    if (!button) return;

    // **2. Recupero delle informazioni dal pulsante**
    // Ottieni l'ID del ticket dal pulsante (preso dall'attributo `data-id`).
    const ticketId = button.getAttribute('data-id');
    // Controlla se il ticket è già stato osservato (preso dall'attributo `data-watched`).
    const isWatched = button.getAttribute('data-watched') === 'true';

    // **3. Se il ticket è già osservato, esci**
    if (isWatched) return;

    // **4. Aggiornamento immediato del pulsante**
    // Modifica il testo del pulsante per indicare lo stato "Watched".
    button.querySelector('span:nth-child(2)').textContent = 'Watched';
    button.classList.remove('btn-outline-info');
    button.classList.add('btn-info');
    // Cambia l'icona da un occhio (`bi-eye`) a un segno di spunta (`bi-check`).
    button.querySelector('span:nth-child(1)').classList.replace('bi-eye', 'bi-check');
    // Aggiorna l'attributo `data-watched` per riflettere il nuovo stato.
    button.setAttribute('data-watched', 'true');

    // **5. Invio della richiesta al server**
    // Esegui una chiamata POST per informare il server che il ticket è stato aggiunto alla lista.
    fetch(`/tickets/watch/${ticketId}`, {
        method: 'POST', // Specifica il metodo HTTP
        headers: {
            'Content-Type': 'application/json' // Indica il tipo di contenuto della richiesta
        }
    })
        .then(response => {
            // Controlla se la risposta è OK (status 200-299).
            if (!response.ok) {
                throw new Error('Failed to add ticket to watches'); // Solleva un'eccezione in caso di errore.
            }
            return response.text(); // Estrai il corpo della risposta come testo.
        })
        .then(message => {
            // **6. Aggiornamento del contatore laterale**
            // Trova l'elemento del contatore (#watch-count) e incrementalo.
            const watchCountElement = document.querySelector('#watch-count');
            if (watchCountElement) {
                const currentCount = parseInt(watchCountElement.textContent, 10);
                watchCountElement.textContent = currentCount + 1; // Incrementa il valore attuale.
            }
            console.log(message); // Logga il messaggio di successo dal server.
        })
        .catch(error => {
            // **7. Gestione degli errori**
            console.error('Error:', error);

            // Ripristina lo stato del pulsante in caso di errore.
            button.querySelector('span:nth-child(2)').textContent = 'Watch';
            button.classList.remove('btn-info');
            button.classList.add('btn-outline-info');
            button.querySelector('span:nth-child(1)').classList.replace('bi-check', 'bi-eye');
            button.setAttribute('data-watched', 'false');
        });
});

document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".change-state-btn");

    buttons.forEach(button => {
        button.addEventListener("click", function (event) {
            event.preventDefault(); // Evita il comportamento di default di un link

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

document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".mark-completed-btn");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            const milestoneId = this.getAttribute("data-milestone-id");

            fetch(`/milestones/${milestoneId}/complete`, {
                method: "POST",
                headers: { "Content-Type": "application/json" }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Trova e aggiorna lo stato della milestone
                        const statusBadge = this.closest(".card-body").querySelector(".detail-status");
                        if (statusBadge) {
                            statusBadge.textContent = "COMPLETED";
                        }

                        // Nasconde il pulsante
                        this.style.display = "none";
                    }
                })
                .catch(error => console.error("Error updating milestone:", error));
        });
    });
});

//Add tag: js function used to add a tag to a ticket when you press the "add tag" button
document.addEventListener("DOMContentLoaded", function () {
    const addTagButton = document.getElementById("add-tag-btn");
    const tagInput = document.getElementById("tag-input");
    const tagsContainer = document.querySelector(".tags-container");
    const ticketId = window.location.pathname.split("/").pop(); // Prende l'ID del ticket dall'URL

    addTagButton.addEventListener("click", function () {
        const tagValue = tagInput.value.trim().replace(/\s+/g, "").toLowerCase();
        if (tagValue === "") return;

        // Controlla se il tag esiste già
        const existingTags = Array.from(tagsContainer.getElementsByClassName("badge"));
        if (existingTags.some(tag => tag.textContent.trim().toLowerCase() === tagValue)) {
            alert("Il tag esiste già!");
            tagInput.value = "";
            return;
        }

        fetch(`/api/tickets/${ticketId}/tags`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ tag: tagValue })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Creazione dinamica del tag visibile
                    const newTag = document.createElement("span"); // Cambiato da <div> a <span>
                    newTag.classList.add("badge", "bg-dark", "text-white", "me-1");
                    newTag.textContent = data.tag;

                    // Inserisce il tag accanto agli altri (inline)
                    tagsContainer.appendChild(newTag);

                    // Svuota il campo input
                    tagInput.value = "";
                }
            })
            .catch(error => console.error("Errore aggiunta tag:", error));
    });
});

//show tags in the left column of the master.html
document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/tags")
        .then(response => response.json())
        .then(tags => {
            const tagsContainer = document.getElementById("tags-container");
            tags.forEach(tag => {
                const tagElement = document.createElement("span");
                tagElement.classList.add("badge", "bg-dark", "text-white", "me-1", "mb-1");
                tagElement.textContent = tag;
                tagsContainer.appendChild(tagElement);
            });
        })
        .catch(error => console.error("Errore nel caricamento dei tag:", error));
});

// Attende che il documento HTML sia completamente caricato
document.addEventListener('DOMContentLoaded', () => {
    // Seleziona tutti i pulsanti con la classe CSS "delete-comment"
    const deleteButtons = document.querySelectorAll('.delete-comment');

    // Itera su ogni pulsante trovato
    deleteButtons.forEach(button => {
        // Aggiunge un event listener al clic del pulsante
        button.addEventListener('click', async (event) => {
            // Impedisce il comportamento predefinito del pulsante (es. navigazione)
            event.preventDefault();

            // Recupera l'attributo "data-id" del pulsante che rappresenta l'ID del commento
            const commentId = button.getAttribute('data-id');

            // Controlla se l'ID del commento esiste; se no, mostra un messaggio di errore
            if (!commentId) {
                console.error("ID del commento non trovato");
                return; // Termina l'esecuzione della funzione
            }

            try {
                // Effettua una richiesta AJAX utilizzando il metodo DELETE all'endpoint del commento
                const response = await fetch(`/comments/${commentId}`, {
                    method: 'DELETE', // Specifica il metodo HTTP DELETE
                    headers: {
                        'Content-Type': 'application/json' // Imposta l'intestazione per indicare JSON
                    }
                });

                // Controlla se la risposta del server è positiva
                if (response.ok) {
                    // Trova l'elemento DOM più vicino con la classe "border" (contenitore del commento)
                    const commentDiv = button.closest('.border');

                    // Rimuove l'elemento dal DOM se esiste
                    if (commentDiv) {
                        commentDiv.remove();
                    } else {
                        // Se non trova l'elemento, mostra un messaggio di errore nel console log
                        console.error("Elemento commento non trovato nel DOM.");
                    }
                } else if (response.status === 403) {
                    // Caso in cui l'utente non è autorizzato a eliminare il commento
                    console.error('Non sei autorizzato a rimuovere questo commento.');
                } else {
                    // Caso generico di errore nella rimozione del commento
                    console.error('Errore durante la rimozione del commento.');
                }
            } catch (error) {
                // Gestisce eventuali errori durante la richiesta
                console.error('Errore:', error);
            }
        });
    });
});


// Aspetta che il documento HTML sia completamente caricato prima di eseguire il codice
document.addEventListener('DOMContentLoaded', () => {
    // Seleziona i campi di input con gli ID "title" e "description"
    const fields = document.querySelectorAll('#title, #description');

    // Itera su ogni campo selezionato
    fields.forEach(field => {
        // Aggiunge un listener per l'evento "blur" (quando il campo perde il focus)
        field.addEventListener('blur', async (event) => {
            // Recupera l'elemento che ha generato l'evento
            const element = event.target;

            // Estrae l'ID del ticket dall'attributo "data-ticket-id" del campo
            const ticketId = element.dataset.ticketId;

            // Estrae l'ID del campo (ad es. "title" o "description") per identificare il campo modificato
            const fieldName = element.id;

            // Recupera il nuovo valore inserito dall'utente
            const newValue = element.value;

            try {
                // Esegue una richiesta AJAX utilizzando il metodo PATCH per aggiornare il ticket
                const response = await fetch(`/tickets/${ticketId}`, {
                    method: 'PATCH', // Indica che stiamo eseguendo un aggiornamento parziale
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded', // Specifica il formato dei dati inviati
                    },
                    // Invia i dati aggiornati (nome del campo e nuovo valore) codificati come URL
                    body: `fieldName=${fieldName}&newValue=${encodeURIComponent(newValue)}`
                });

                // Se la risposta è positiva, cambia il bordo del campo in verde
                if (response.ok) {
                    element.style.border = '2px solid green'; // Aggiornamento riuscito
                } else {
                    // In caso di errore, cambia il bordo del campo in rosso
                    element.style.border = '2px solid red'; // Errore nell'aggiornamento
                }
            } catch (error) {
                // Gestisce eventuali errori durante la richiesta
                console.error('Errore durante l\'aggiornamento:', error);
                element.style.border = '2px solid red'; // Errore generico
            }

            // Dopo 2 secondi, rimuove il bordo per ripristinare l'aspetto originale
            setTimeout(() => {
                element.style.border = '';
            }, 2000);
        });
    });
});






