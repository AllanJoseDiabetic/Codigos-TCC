(function () {
    "use strict";

    var form = document.getElementById(
        "bugForm"
    );

    if (!form) {
        return;
    }

    var message = document.getElementById(
        "formMessage"
    );

    var saveButton = document.getElementById(
        "saveBugButton"
    );

    function clearErrors() {
        var errors = document.querySelectorAll(
            ".field-error"
        );

        Array.prototype.forEach.call(
            errors,
            function (element) {
                element.textContent = "";
            }
        );

        message.hidden = true;
        message.textContent = "";
    }

    function showMessage(text, isError) {
        message.hidden = false;

        message.className = isError
            ? "alert alert-danger"
            : "alert alert-success";

        message.textContent = text;
    }

    function showValidationErrors(errors) {
        var hasGeneralError = false;

        Object.keys(errors).forEach(function (fieldName) {
            var element = document.getElementById(
                "error-" + fieldName
            );

            var text = errors[fieldName].join(" ");

            if (element) {
                element.textContent = text;
            } else {
                hasGeneralError = true;
            }
        });

        if (hasGeneralError) {
            showMessage(
                "Verifique os dados informados.",
                true
            );
        } else {
            showMessage(
                "O formulário contém campos inválidos.",
                true
            );
        }
    }

    form.addEventListener(
        "submit",
        function (event) {
            event.preventDefault();

            clearErrors();

            saveButton.disabled = true;

            var data = new FormData(form);

            var csrf = form.querySelector(
                'input[name="csrfmiddlewaretoken"]'
            );

            fetch(
                form.action,
                {
                    method: "POST",
                    body: data,
                    credentials: "same-origin",
                    headers: {
                        "Accept": "application/json",
                        "X-Requested-With": "XMLHttpRequest",
                        "X-CSRFToken": csrf.value
                    }
                }
            ).then(function (response) {
                var contentType = (
                    response.headers.get("content-type") ||
                    ""
                );

                if (
                    contentType.indexOf(
                        "application/json"
                    ) === -1
                ) {
                    throw new Error(
                        "O servidor não retornou uma resposta JSON."
                    );
                }

                return response.json().then(
                    function (body) {
                        return {
                            ok: response.ok,
                            status: response.status,
                            body: body
                        };
                    }
                );

            }).then(function (result) {
                if (!result.ok) {
                    if (result.body.errors) {
                        showValidationErrors(
                            result.body.errors
                        );

                        return;
                    }

                    throw new Error(
                        result.body.error ||
                        "Não foi possível salvar o defeito."
                    );
                }

                showMessage(
                    "Informações salvas com sucesso.",
                    false
                );

                window.location.assign(
                    result.body.redirect
                );

            }).catch(function (error) {

                showMessage(
                    error.message,
                    true
                );

            }).then(function () {

                saveButton.disabled = false;

            });
        }
    );
})();