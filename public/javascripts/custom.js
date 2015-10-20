$(document).ready(function(){
    $('.modal-trigger').leanModal();
});

function addBoard(){
    postAndProcessForm('/addBoard', $('#new-board-modal').serialize())
}

function renameBoard(){
    postAndProcessForm('/renameBoard', $('#edit-board-modal').serialize())
}

function postAndProcessForm(url, data){
    $.ajax({
        type: 'POST',
        url: url,
        data: data,
        success: function(){
            location.reload();
        },
        error: function(xhr){
            Materialize.toast(xhr.responseText, 2000)
        }
    });
}