// Handles the deletion of comments via an AJAX request

document.addEventListener('DOMContentLoaded', () => {
    const deleteButtons = document.querySelectorAll('.delete-comment');

    deleteButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            event.preventDefault();
            const commentId = button.getAttribute('data-id');
            if (!commentId) {
                console.error("Comment ID not found");
                return;
            }
            try {
                const response = await fetch(`/comments/${commentId}`, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' }
                });
                if (response.ok) {
                    const commentDiv = button.closest('.border');
                    if (commentDiv) {
                        commentDiv.remove();
                    } else {
                        console.error("Comment element not found in DOM.");
                    }
                } else if (response.status === 403) {
                    console.error('Not authorized to delete this comment.');
                } else {
                    console.error('Error deleting comment.');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });
    });
});