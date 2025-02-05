//Add tag: js function used to add a tag to a ticket when you press the "add tag" button

document.addEventListener("DOMContentLoaded", function () {
    const addTagButton = document.getElementById("add-tag-btn");
    const tagInput = document.getElementById("tag-input");
    const tagsContainer = document.querySelector(".tags-container");
    const ticketId = window.location.pathname.split("/").pop(); // Prende l'ID del ticket dall'URL

    addTagButton.addEventListener("click", function () {
        const tagValue = tagInput.value.trim().replace(/\s+/g, "").toLowerCase();
        if (tagValue === "") return;

        // Check
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