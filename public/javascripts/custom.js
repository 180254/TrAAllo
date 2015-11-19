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
        return postAndProcessForm('/board/delete', $(this), false, function () {
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

    $('.card-cancel-add-js').click(function () {
        $(this).closest('.add-card').toggle();
        $(this).closest('.bList-card-container').find('.add-new-card').toggle();
    });

    $('.card-add-form').on('submit', function () {
        return postAndProcessForm('/card/add', $(this), true);
    });

    $('.card-edit-form-js').on('submit', function () {
        return postAndProcessForm('/card/edit', $(this), true);
    });

    $('li.bList-card').on('dblclick', function () {
        var $labelOfCardName = $(this).find('.card-name-js'),
            $editForm = $(this).find('.card-edit-form-js');


        /* labelOfCardName will be empty array is user has not admin rights */
        if ($editForm.length > 0 && $labelOfCardName.is(":visible")) {
            $labelOfCardName.toggle();
            $editForm.toggle();
        }
    });

    $('.card-cancel-edit-js').on('click', function (e) {
        $(this).closest('.card-edit-form-js').toggle();
        $(this).closest('.bList-card').find('.card-name-js').toggle();
        return false;
    });

    $('.card-delete-btn').on('click', function () {
        var form = buildForm('cardId', $(this).attr('data-id'));
        postAndProcessForm('/card/delete', form, true);
    });

    var $sortable = $('.sortable');
    $sortable.sortable({
        update: function (event, ui) {

            // lets do magic !
            var target = event.target,
                toElement = event.toElement || event.originalEvent.target,

                check1 = target.id === "bList-list",
                check2 = target.contains(toElement),
                check3 = ui.sender == null,

                listMoved = (check1 && check2 && check3),
                cardSorted = (!check1 && check2 && check3),

                cardMoved1 = (!check1 && !check2 && check3),
                cardMoved2 = (!check1 && check2 && !check3);
            // end of magic

            if (listMoved) console.log('list moved');
            if (cardSorted) console.log('card sorted');
            if (cardMoved1) console.log('card moved');

            if (listMoved) bListSorted();
            if (cardSorted) cardsSorted(ui.item.closest('.bList-card-container'));

            if (cardMoved1) {
                var $bList1 = $(target).closest('.bList-one'),
                    $bList2 = $(toElement).closest('.bList-one'),
                    whichCard = $(toElement).closest('.bList-card').attr('data-id');
                cardMoved($bList1, $bList2, whichCard);
            }
        }
    });

    $('.sortable2').sortable({
        connectWith: '.sortable2'
    });

    initAttachmentDelete($('.attachment-delete'));
    initAttachmentUpload();
});

//<li data-id="@attachment.id">
//    <a href="#" class="attachment-delete">[X]</a>
//    <a target="_blank" href="@controllers.boards.routes.AttachmentController.download(attachment.id)">
//@attachment.fileName
//</a>
//</li>
function initAttachmentUpload() {
    $('.modal-add-attachment').each(function () {
        $(this).on('submit', function (event) {
            event.preventDefault();

            var dis = $(this);
            var formData = new FormData($(this)[0]);

            $.ajax({
                type: 'POST',
                url: $(this).attr('action'),
                data: formData,
                async: false,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    var dataArr = data.split("\n");

                    var $li = $('<li/>').attr('data-id', dataArr[0])

                        .append($('<a>', {
                            href: '#',
                            class: 'attachment-delete'
                        }).append('[X]'))

                        .append(' ')

                        .append($('<a>', {
                            target: '_blank',
                            href: '/attachment/download?id=' + dataArr[0]
                        }).append(dataArr[1]))

                        .appendTo(dis.prev());

                    Materialize.toast('Successfully done!', 1000, 'succ-done');
                    initAttachmentDelete($li.find('.attachment-delete'));
                },
                error: function (xhr) {
                    Materialize.toast(xhr.responseText, 1500);
                }


            });
        });
    })
}

function initAttachmentDelete($element) {
    $element.click(function (event) {
        event.preventDefault();

        var $li = $(this).closest('li'),
            attachmentid = $li.attr('data-id');

        $.ajax({
            type: 'POST',
            url: '/attachment/delete',
            data: 'id=' + attachmentid,
            success: function () {
                Materialize.toast('Successfully done!', 1000, 'succ-done');
                $li.remove();
            },
            error: function (xhr) {
                Materialize.toast(xhr.responseText, 1500);
            }
        });
    });
}

function boardAdd() {
    postAndProcessForm('/board/add', $('#new-board-modal'), true)
}

function boardEdit() {
    postAndProcessForm('/board/edit', $('#edit-board-modal'), true)
}

function postAndProcessForm(url, form, reload, callbackOnSuccess) {
    console.log(form.serialize());

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

                if (callbackOnSuccess !== undefined) {
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
    var form = buildForm('bListID', $a.attr('data-id'));
    postAndProcessForm('/bList/delete', form, true);
}

function bListSorted() {
    var sortedBLists = [];
    $('#bList-list').children('li').each(function () {
        sortedBLists.push($(this).attr('id').replace(/[^0-9]/g, ''));
    });

    var form = buildForm('sortedBLists', sortedBLists);
    postAndProcessForm('/bList/sort', form, false);
}

function sortCardUtil(parent) {
    var sortedCards = [];
    parent.find('.bList-cards-list').children('li').each(function () {
        sortedCards.push($(this).attr('data-id'));
    });
    return sortedCards;
}

function cardsSorted(parent) {
    var form = buildForm('sortedCards', sortCardUtil(parent));
    postAndProcessForm('/card/sort', form, false);
}

function cardMoved($bList1, $bList2, whichCard) {
    var $bList1Container = $bList1.find('.bList-card-container'),
        $bList2Container = $bList2.find('.bList-card-container'),
        bList1ID = $bList1.attr('id').replace(/[^0-9]/g, ''),
        bList2ID = $bList2.attr('id').replace(/[^0-9]/g, ''),
        form = $('<form/>');

    buildInput(form, 'sortedCards1', sortCardUtil($bList1Container));
    buildInput(form, 'sortedCards2', sortCardUtil($bList2Container));
    buildInput(form, 'list1IDFrom', bList1ID);
    buildInput(form, 'list2IDTo', bList2ID);
    buildInput(form, 'movedCardID', whichCard);

    postAndProcessForm('/bList/movedCard', form, true);
}

function buildForm(name, value) {
    var form = $('<form/>');
    buildInput(form, name, value);
    return form;
}

function buildInput(form, name, value) {
    form.append(
        $('<input/>',
            {
                name: name,
                value: value
            }
        )
    );
}
