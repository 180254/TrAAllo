$(document).ready(function () {
    $('.modal-trigger').leanModal();
});

function boardAdd() {
    postAndProcessForm('/board/add', $('#new-board-modal'))
}

function boardEdit() {
    postAndProcessForm('/board/edit', $('#edit-board-modal'))
}

function postAndProcessForm(url, form) {
    $.ajax({
        type: 'POST',
        url: url,
        data: form.serialize(),
        success: function () {
            location.reload();
            $('#new-board-modal')[0].reset();
        },
        error: function (xhr) {
            Materialize.toast(xhr.responseText, 5000);
        }
    });
}