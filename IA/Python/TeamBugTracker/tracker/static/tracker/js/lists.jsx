(function () {
    "use strict";

    class BugList extends React.Component {
        constructor(props) {
            super(props);

            this.state = {
                loading: true,
                error: "",
                bugs: [],
                counts: null,
                showCompleted: true
            };

            this.toggleCompleted = this.toggleCompleted.bind(
                this
            );
        }

        componentDidMount() {
            const options = {
                credentials: "same-origin",
                headers: {
                    "Accept": "application/json"
                }
            };

            const fetchJson = (url) => {
                return fetch(url, options).then(
                    (response) => {
                        if (!response.ok) {
                            throw new Error(
                                "Falha ao consultar os dados."
                            );
                        }

                        return response.json();
                    }
                );
            };

            Promise.all([
                fetchJson(this.props.endpoint),
                fetchJson(this.props.countEndpoint)
            ]).then(
                (results) => {
                    this.setState({
                        loading: false,
                        bugs: results[0].bugs,
                        counts: results[1],
                        error: ""
                    });
                }
            ).catch(
                (error) => {
                    this.setState({
                        loading: false,
                        error: error.message
                    });
                }
            );
        }

        toggleCompleted(event) {
            this.setState({
                showCompleted: event.target.checked
            });
        }

        renderCounters() {
            const counts = this.state.counts;

            if (!counts) {
                return null;
            }

            return (
                <div className="row mb-4">

                    <div className="col-md-3 col-6 mb-3">
                        <div className="counter-card">
                            <span>Organização</span>

                            <strong>
                                {counts.organization_count}
                            </strong>

                            <small>
                                Total de defeitos
                            </small>
                        </div>
                    </div>

                    <div className="col-md-3 col-6 mb-3">
                        <div className="counter-card">
                            <span>Ativos</span>

                            <strong>
                                {counts.organization_active_count}
                            </strong>

                            <small>
                                Na organização
                            </small>
                        </div>
                    </div>

                    <div className="col-md-3 col-6 mb-3">
                        <div className="counter-card">
                            <span>Minha responsabilidade</span>

                            <strong>
                                {counts.personal_count}
                            </strong>

                            <small>
                                Total atribuído
                            </small>
                        </div>
                    </div>

                    <div className="col-md-3 col-6 mb-3">
                        <div className="counter-card">
                            <span>Meus ativos</span>

                            <strong>
                                {counts.personal_active_count}
                            </strong>

                            <small>
                                Aguardando conclusão
                            </small>
                        </div>
                    </div>

                </div>
            );
        }

        renderStatus(bug) {
            if (bug.active) {
                return (
                    <span className="badge badge-warning">
                        Ativo
                    </span>
                );
            }

            return (
                <span className="badge badge-success">
                    Concluído
                </span>
            );
        }

        renderSeverity(bug) {
            let className = "badge badge-secondary";

            if (bug.severity === "critical") {
                className = "badge badge-danger";
            } else if (bug.severity === "high") {
                className = "badge badge-warning";
            } else if (bug.severity === "medium") {
                className = "badge badge-info";
            }

            return (
                <span className={className}>
                    {bug.severity_label}
                </span>
            );
        }

        renderTable() {
            const bugs = this.state.bugs.filter(
                (bug) => {
                    return (
                        this.state.showCompleted ||
                        bug.active
                    );
                }
            );

            if (bugs.length === 0) {
                return (
                    <div className="empty-state">
                        Nenhum defeito encontrado para
                        os filtros selecionados.
                    </div>
                );
            }

            return (
                <div className="table-responsive">
                    <table className="table bug-table">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Título</th>
                                <th>Categoria</th>
                                <th>Severidade</th>
                                <th>Responsável</th>
                                <th>Horas</th>
                                <th>Estado</th>
                                <th>Ações</th>
                            </tr>
                        </thead>

                        <tbody>
                            {bugs.map((bug) => (
                                <tr key={bug.id}>

                                    <td>
                                        #{bug.id}
                                    </td>

                                    <td>
                                        <a href={bug.detail_url}>
                                            {bug.title}
                                        </a>
                                    </td>

                                    <td>
                                        {bug.category_label}
                                    </td>

                                    <td>
                                        {this.renderSeverity(bug)}
                                    </td>

                                    <td>
                                        {bug.assignee.username}
                                    </td>

                                    <td>
                                        {bug.estimated_hours}
                                    </td>

                                    <td>
                                        {this.renderStatus(bug)}
                                    </td>

                                    <td>
                                        <a
                                            className="btn btn-sm btn-outline-primary"
                                            href={bug.detail_url}
                                        >
                                            Abrir
                                        </a>
                                    </td>

                                </tr>
                            ))}
                        </tbody>

                    </table>
                </div>
            );
        }

        render() {
            if (this.state.loading) {
                return (
                    <div className="card app-card p-4">
                        Carregando os dados da organização.
                    </div>
                );
            }

            if (this.state.error) {
                return (
                    <div
                        className="alert alert-danger"
                        role="alert"
                    >
                        {this.state.error}
                    </div>
                );
            }

            return (
                <div>

                    {this.renderCounters()}

                    <div className="card app-card">

                        <div className="card-header list-toolbar">

                            <h2 className="h5 mb-0">
                                {this.props.mode === "personal"
                                    ? "Defeitos atribuídos a mim"
                                    : "Defeitos da equipe"}
                            </h2>

                            <label className="completed-filter">
                                <input
                                    type="checkbox"
                                    checked={this.state.showCompleted}
                                    onChange={this.toggleCompleted}
                                />

                                Exibir concluídos
                            </label>

                        </div>

                        <div className="card-body p-0">
                            {this.renderTable()}
                        </div>

                    </div>

                </div>
            );
        }
    }

    const root = document.getElementById(
        "bug-list-root"
    );

    if (root) {
        ReactDOM.render(
            <BugList
                endpoint={root.getAttribute("data-endpoint")}
                countEndpoint={root.getAttribute(
                    "data-count-endpoint"
                )}
                mode={root.getAttribute("data-mode")}
            />,
            root
        );
    }
})();