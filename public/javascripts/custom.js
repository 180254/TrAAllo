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
            Materialize.toast("Successfully done!", 1000, 'succ-done', function () {
                location.reload();
                form[0].reset();
            });
        },

        error: function (xhr) {
            Materialize.toast(xhr.responseText, 1500);
        }
    });
}