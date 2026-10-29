(function () {
    "use strict";

    var root = document.getElementById(
        "bug-detail-root"
    );

    if (!root) {
        return;
    }

    var detailUrl = root.getAttribute(
        "data-detail-url"
    );

    var completeUrl = root.getAttribute(
        "data-complete-url"
    );

    var message = document.getElementById(
        "detailMessage"
    );

    var content = document.getElementById(
        "detailContent"
    );

    var completeForm = document.getElementById(
        "completeBugForm"
    );

    var completeButton = document.getElementById(
        "completeBugButton"
    );

    function setText(id, value) {
        document.getElementById(
            id
        ).textContent = value == null ? "" : String(value);
    }

    function showMessage(text, isError) {
        message.hidden = false;

        message.className = isError
            ? "alert alert-danger"
            : "alert alert-success";

        message.textContent = text;
    }

    function formatDate(value) {
        if (!value) {
            return "Não informado";
        }

        var date = new Date(value);

        if (isNaN(date.getTime())) {
            return "Data inválida";
        }

        return date.toLocaleString("pt-BR");
    }

    function renderBug(bug) {
        setText(
            "detailTitle",
            bug.title
        );

        setText(
            "detailSubtitle",
            "Defeito #" + bug.id
        );

        setText(
            "detailSteps",
            bug.reproduction_steps
        );

        setText(
            "detailStatus",
            bug.status
        );

        setText(
            "detailAssignee",
            bug.assignee.username
        );

        setText(
            "detailCategory",
            bug.category_label
        );

        setText(
            "detailSeverity",
            bug.severity_label
        );

        setText(
            "detailHours",
            bug.estimated_hours + " hora(s)"
        );

        setText(
            "detailOrganization",
            bug.organization
        );

        setText(
            "detailCreator",
            bug.created_by
        );

        setText(
            "detailCreated",
            formatDate(bug.created_at)
        );

        setText(
            "detailUpdated",
            formatDate(bug.updated_at)
        );

        document.getElementById(
            "editBugLink"
        ).href = bug.edit_url;

        completeForm.hidden = !bug.active;

        completeButton.disabled = !bug.active;

        content.hidden = false;
    }

    function loadBug() {
        fetch(
            detailUrl,
            {
                credentials: "same-origin",
                headers: {
                    "Accept": "application/json"
                }
            }
        ).then(function (response) {
            if (!response.ok) {
                throw new Error(
                    "Não foi possível consultar o defeito."
                );
            }

            return response.json();

        }).then(function (data) {

            renderBug(data.bug);

        }).catch(function (error) {

            showMessage(
                error.message,
                true
            );

        });
    }

    completeForm.addEventListener(
        "submit",
        function (event) {
            event.preventDefault();

            if (completeButton.disabled) {
                return;
            }

            var csrf = completeForm.querySelector(
                'input[name="csrfmiddlewaretoken"]'
            );

            completeButton.disabled = true;

            fetch(
                completeUrl,
                {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Accept": "application/json",
                        "X-CSRFToken": csrf.value
                    }
                }
            ).then(function (response) {
                return response.json().then(
                    function (data) {
                        return {
                            status: response.status,
                            ok: response.ok,
                            data: data
                        };
                    }
                );

            }).then(function (result) {

                if (!result.ok) {
                    throw new Error(
                        result.data.error ||
                        "Não foi possível concluir o defeito."
                    );
                }

                renderBug(result.data.bug);

                showMessage(
                    "Defeito marcado como concluído.",
                    false
                );

            }).catch(function (error) {

                completeButton.disabled = false;

                showMessage(
                    error.message,
                    true
                );

            });
        }
    );

    loadBug();
})();