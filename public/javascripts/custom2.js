$(document).ready(function () {

    $("#team-add-form").submit(function () {
        teamAddFormHandler();
        return false;
    });

    $('.team-delete').click(function () {
        teamDeleteHandler($(this));
        return false;
    });

    $('.team-one-add-member-form').submit(function () {
        teamAddMemberHandler($(this));
        return false;
    });

    $('.team-member-delete').click(function () {
        teamDeleteMemberHandler($(this));
        return false;
    });

    $('.team-edit').click(function () {
        teamEditHandler($(this));
    });

});

function teamAddFormHandler() {
    $.ajax({
        type: 'POST',
        url: '/team/add',
        data: $("#team-add-form").serialize(),

        success: function (data) {
            var teamMainOnes,
                lastTeamOne;

            $('#team-add-form').find('input[type="text"]').val('');

            teamMainOnes = $("#team-main-ones");
            teamMainOnes.append(data);

            lastTeamOne = teamMainOnes.find('.team-one').last();

            lastTeamOne.find('.team-delete').click(function () {
                teamDeleteHandler($(this));
                return false;
            });

            lastTeamOne.find('.team-one-add-member-form').submit(function () {
                teamAddMemberHandler($(this));
                return false;
            });

            lastTeamOne.find('.team-member-delete').click(function () {
                teamDeleteMemberHandler($(this));
                return false;
            });

            lastTeamOne.find('input').focus();

            Materialize.toast('Successfully done!', 1000, 'succ-done');

        },

        error: function (xhr) {
            Materialize.toast(xhr.responseText, 1500);
        }

    });
}

function teamDeleteHandler($delElement) {
    $.ajax({
        type: 'POST',
        url: '/team/delete',
        data: 'teamID=' + $delElement.attr('data-team-id'),

        success: function (data) {
            $delElement.closest('.team-one').remove();
            Materialize.toast('Successfully done!', 1000, 'succ-done');
        },

        error: function (xhr) {
            Materialize.toast(xhr.responseText, 1500);
        }

    });
}

function teamAddMemberHandler($memberAddForm) {
    $.ajax({
        type: 'POST',
        url: '/team/member/add',
        data: $memberAddForm.serialize(),

        success: function (data) {
            $memberAddForm.parent().find('ul').append(data);
            $memberAddForm.find('input[type="text"]').val('');

            $memberAddForm.parent().find('.team-member-delete').last().click(function () {
                teamDeleteMemberHandler($(this));
                return false;
            });

            Materialize.toast('Successfully done!', 1000, 'succ-done');

        },

        error: function (xhr) {
            Materialize.toast(xhr.responseText, 1500);
        }

    });
}

function teamDeleteMemberHandler($delMemberEl) {
    $.ajax({
        type: 'POST',
        url: '/team/member/delete',
        data: 'teamID=' + $delMemberEl.attr('data-team-id')
        + '&userID=' + $delMemberEl.attr('data-user-id'),

        success: function (data) {
            $delMemberEl.closest('li').remove();
            Materialize.toast('Successfully done!', 1000, 'succ-done');
        },

        error: function (xhr) {
            Materialize.toast(xhr.responseText, 1500);
        }

    });
}

function teamEditHandler($editLink) {

}
