$(document).ready(function () {
    $('.modal-trigger').leanModal();
});

function boardAdd() {
    postAndProcessForm('/board/add', $('#new-board-modal').serialize())
}

function boardRename() {
    postAndProcessForm('/board/rename', $('#edit-board-modal').serialize())
}

function postAndProcessForm(url, data) {
    $.ajax({
        type: 'POST',
        url: url,
        data: data,
        success: function () {
            location.reload();
        },
        error: function (xhr) {
            Materialize.toast(xhr.responseText, 2000);
        }
    });
}