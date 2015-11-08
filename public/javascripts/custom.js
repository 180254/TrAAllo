$(document).ready(function () {
    $('.modal-trigger').leanModal();

    $('.hidden-add').hide();
    $('#bList-add-open').click(bListAddOpenClose);
    $('#bList-add-close').click(bListAddOpenClose);

    $('.bList-one-name').dblclick(function () {
        bListRenameOpenClose($(this));
    });
    $('.bList-one-close').click(function () {
        bListRenameOpenClose($(this));
    });

    $('#board-delete-form').on('submit', function () {
        return postAndProcessForm('/board/delete', $(this), false, function() {
            location.replace("/");
        });
    });

    $('#bList-add-form').on('submit', function () {
        return postAndProcessForm('/bList/add', $(this), true);
    });
    $('.bList-one-edit-form').on('submit', function () {
        return postAndProcessForm('/bList/edit', $(this), true);
    });

    $('.bList-one-del').click(function () {
        bListDel($(this));
    });

    $('.add-new-card').click(function () {
        $(this).toggle();
        $(this).closest('.bList-card-container').find('.add-card').toggle();
    });

    $('.card-cancel').click(function () {
        $(this).closest('.add-card').toggle();
        $(this).closest('.bList-card-container').find('.add-new-card').toggle();
    });

    $('.card-edit-form').on('submit', function () {
        return postAndProcessForm('/card/add', $(this), true);
    });

    var $sortable = $('.sortable');
    $sortable.sortable({
        update: function (event, ui) {
            var $item = ui.item;
            if($item.hasClass('bList-one')){
                var $id = $item.attr('id');

                if ($id !== undefined && $id.indexOf('bList-one') !== -1) {
                    bListSorted();
                }
            }
            else if ($item.hasClass('bList-card')) {
                var $id = $item.attr('data-id');
                if ($id != undefined) {
                    cardsSorted($item.closest('.bList-card-container'));
                }
            }
        }
    });
    $sortable.disableSelection();
});

function boardAdd() {
    postAndProcessForm('/board/add', $('#new-board-modal'), true)
}

function boardEdit() {
    postAndProcessForm('/board/edit', $('#edit-board-modal'), true)
}

function postAndProcessForm(url, form, reload, callbackOnSuccess) {
    $.ajax({
        type: 'POST',
        url: url,
        data: form.serialize(),
        success: function () {
            Materialize.toast('Successfully done!', 1000, 'succ-done', function () {
                if (reload) {
                    location.reload();
                }

                if (url.indexOf('/board') !== -1) {
                    form[0].reset();
                }

                if(callbackOnSuccess !== undefined) {
                    callbackOnSuccess();
                }

            });
        },

        error: function (xhr) {
            Materialize.toast(xhr.responseText, 1500);
        }
    });

    return false;
}

function bListOpenClose($div1, $div2, $input) {
    var fadeTime = 200;

    if ($div2.hasClass('hidden-add')) {
        $div1 = [$div2, $div2 = $div1][0]; // swap
    }

    $div1.toggleClass('hidden-add');
    $div2.toggleClass('hidden-add');
    var input = $div1.find('input');

    $div2.fadeOut(fadeTime, function () {
        $div1.fadeIn(fadeTime);

        if (input[0] !== undefined) {
            $input.val('');
            input.focus();
        }
    });

    return false;
}

function bListAddOpenClose() {
    return bListOpenClose($('#bList-add-pre-div'),
        $('#bList-add-div'),
        $('#bList-add-form-input'));
}

function bListRenameOpenClose($a) {
    var $div = $a.parent();

    while ($div.attr('id') === undefined) {
        $div = $div.parent();
    }

    var $id = $div.attr('id').replace(/[^0-9]/g, '');

    return bListOpenClose($('#bList-one-div-name-' + $id),
        $('#bList-one-edit-' + $id),
        $('#bList-one-close-' + $id));
}

function bListDel($a) {
    var form = $('<form/>');
    form.append(
        $('<input/>',
            {
                name: 'bListID',
                value: $a.attr('data-id')
            }
        )
    );

    postAndProcessForm('/bList/delete', form, true);
}

function bListSorted() {
    var sortedBLists = [];
    $('#bList-list').children('li').each(function () {
        sortedBLists.push($(this).attr('id').replace(/[^0-9]/g, ''));
    });

    var $form = $('<form/>');
    $form.append(
        $('<input/>',
            {
                name: 'sortedBLists',
                value: sortedBLists
            }
        )
    );

    postAndProcessForm('/bList/sort', $form, false);
}

function cardsSorted(parent) {
    var sortedCards = [];
    parent.find('.bList-cards-list').children('li').each(function () {
        sortedCards.push($(this).attr('data-id'));
    });

    var $form = $('<form/>');
    $form.append(
        $('<input/>',
            {
                name: 'sortedCards',
                value: sortedCards
            }
        )
    );

    postAndProcessForm('/card/sort', $form, false);
}