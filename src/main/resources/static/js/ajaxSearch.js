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