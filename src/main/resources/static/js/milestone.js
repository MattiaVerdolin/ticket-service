// Mark as completed function used for the button in the milestone's card

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