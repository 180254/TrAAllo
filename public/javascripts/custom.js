$(document).ready(function () {
    $('.modal-trigger').leanModal();

    $('.hidden-add').hide();
    $('#bList-add-open').click(bListAddOpenClose);
    $('#bList-add-close').click(bListAddOpenClose);

    $('.bList-one-name').click(function () {
        bListRenameOpenClose($(this));
    });
    $('.bList-one-close').click(function () {
        bListRenameOpenClose($(this));
    });


    $('#bList-add-form').on('submit', function () {
        return postAndProcessForm('/bList/add', $(this));
    });
    $('.bList-one-edit-form').on('submit', function () {
        return postAndProcessForm('/bList/edit', $(this));
    });

    $('.bList-one-del').click(function () {
        bListDel($(this));
    })
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
            Materialize.toast('Successfully done!', 1000, 'succ-done', function () {
                location.reload();
                if (url.indexOf('/board') !== -1) {
                    form[0].reset();
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

    postAndProcessForm('/bList/delete', form)
}