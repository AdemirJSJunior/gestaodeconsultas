// ===================================================================================
// GESTÃO DA DEH - SCRIPT.JS (VERSÃO FINAL COMPLETA)
// ===================================================================================

// --- VARIÁVEIS GLOBAIS ---
const API_BASE_URL = 'http://localhost:8080/api';
let calendar; 
let despesasCalendar;
let dashboardChart;

// --- INICIALIZAÇÃO QUANDO O DOM ESTÁ PRONTO ---
document.addEventListener('DOMContentLoaded', () => {
    // --- MAPEAMENTO DE ELEMENTOS DO DOM ---
    const loginContainer = document.getElementById('login-container');
    const appContainer = document.getElementById('app-container');
    const loginForm = document.getElementById('login-form');
    const logoutBtn = document.getElementById('logout-btn');
    const navLinks = document.querySelectorAll('.nav-link');
    const contentSections = document.querySelectorAll('.content-section');
    const calendarEl = document.getElementById('calendar');
    const despesasCalendarEl = document.getElementById('despesas-calendar');

    // --- INICIALIZAÇÃO DE EVENT LISTENERS ---
    initializeLoginLogout(loginContainer, appContainer, loginForm, logoutBtn);
    initializeNavigation(navLinks, contentSections);
    initializeModalClosers();
    
    initializePacienteListeners();
    initializeDespesaListeners(despesasCalendarEl);
    initializeAgendamentoListeners(calendarEl);
    initializeHorarioListeners();

    checkExistingToken();
});

// =================================================
// FUNÇÕES DE INICIALIZAÇÃO E AUTENTICAÇÃO
// =================================================

function initializeLoginLogout(loginContainer, appContainer, loginForm, logoutBtn) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${API_BASE_URL}/auth/signin`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                throw new Error('Usuário ou senha inválidos.');
            }

            const data = await response.json();
            localStorage.setItem('userToken', data.token);
            localStorage.setItem('userData', JSON.stringify({ id: data.id, nome: data.nome, email: data.email }));

            loginContainer.classList.remove('active');
            appContainer.classList.add('active');
            loadDashboard();

        } catch (error) {
            console.error("Erro de login:", error);
            alert(error.message);
        }
    });

    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('userToken');
        localStorage.removeItem('userData');
        appContainer.classList.remove('active');
        loginContainer.classList.add('active');
        if (calendar) calendar.removeAllEvents();
        if (despesasCalendar) despesasCalendar.removeAllEvents();
    });
}

function checkExistingToken() {
    const token = localStorage.getItem('userToken');
    if (token) {
        console.log("Token encontrado, entrando na aplicação.");
        document.getElementById('login-container').classList.remove('active');
        document.getElementById('app-container').classList.add('active');
        loadDashboard();
    }
}

async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('userToken');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401 || response.status === 403) {
        document.getElementById('logout-btn').click();
        throw new Error('Sessão expirada ou inválida. Por favor, faça o login novamente.');
    }
    
    return response;
}

function initializeSolicitacaoNfListeners() {
    document.getElementById('btn-nova-solicitacao-nf').addEventListener('click', () => openModal('solicitacao-nf'));
    
    const pacienteSelect = document.getElementById('solicitacao-paciente');
    pacienteSelect.addEventListener('change', async () => {
        const pacienteId = pacienteSelect.value;
        const dadosContainer = document.getElementById('solicitacao-dados-container');
        if (!pacienteId) {
            dadosContainer.style.display = 'none';
            return;
        }

        try {
            // Busca os dados do paciente para preencher os campos editáveis
            const paciente = await fetchWithAuth(`${API_BASE_URL}/pacientes/${pacienteId}`).then(res => res.json());
            document.getElementById('solicitacao-nome-nf').value = paciente.nomeCompleto;
            document.getElementById('solicitacao-cpf-nf').value = paciente.cpf || '';
            document.getElementById('solicitacao-endereco-nf').value = paciente.endereco || '';

            // Busca os agendamentos pagos e disponíveis para este paciente
            const agendamentos = await fetchWithAuth(`${API_BASE_URL}/agendamentos/disponiveis-para-nf/${pacienteId}`).then(res => res.json());
            
            const agendamentosList = document.getElementById('solicitacao-agendamentos-list');
            agendamentosList.innerHTML = '';
            if (agendamentos.length > 0) {
                agendamentos.forEach(ag => {
                    const div = document.createElement('div');
                    div.className = 'checklist-item';
                    div.innerHTML = `
                        <input type="checkbox" id="ag-${ag.id}" name="agendamentoIds" value="${ag.id}" data-valor="${ag.valor}">
                        <label for="ag-${ag.id}">
                            ${new Date(ag.dataHora).toLocaleDateString('pt-BR')} - Valor: ${formatCurrency(ag.valor)}
                        </label>
                    `;
                    agendamentosList.appendChild(div);
                });
            } else {
                agendamentosList.innerHTML = '<p>Nenhum agendamento pago disponível para este paciente.</p>';
            }
            
            dadosContainer.style.display = 'grid';
            updateTotalNf();
        } catch (error) {
            console.error("Erro ao carregar dados para solicitação de NF:", error);
            alert("Não foi possível carregar os dados do paciente.");
        }
    });

    // Listener para atualizar o valor total quando um checkbox é marcado/desmarcado
    document.getElementById('solicitacao-agendamentos-list').addEventListener('change', updateTotalNf);
}

function updateTotalNf() {
    const checkboxes = document.querySelectorAll('#solicitacao-agendamentos-list input:checked');
    let total = 0;
    checkboxes.forEach(cb => {
        total += parseFloat(cb.dataset.valor);
    });
    document.getElementById('solicitacao-valor-total').textContent = formatCurrency(total);
}

function openModal(entityName, defaultValues = {}) {
    // ... (código existente)
    if (entityName === 'solicitacao-nf') {
        populatePacientesSelectNf();
        document.getElementById('solicitacao-dados-container').style.display = 'none';
    }
    // ... (código existente)
}

async function populatePacientesSelectNf() {
    const select = document.getElementById('solicitacao-paciente');
    try {
        const pacientes = await fetchWithAuth(`${API_BASE_URL}/pacientes`).then(res => res.json());
        select.innerHTML = '<option value="">Selecione um paciente...</option>';
        pacientes.forEach(p => {
            select.add(new Option(p.nomeCompleto, p.id));
        });
    } catch (error) {
        console.error("Erro ao carregar pacientes no select de NF:", error);
    }
}

// =================================================
// FUNÇÕES DE CARREGAMENTO DE DADOS (LOADERS)
// =================================================

async function loadDashboard() {
    console.log("Carregando Dashboard...");
    try {
        const agendamentos = await fetchWithAuth(`${API_BASE_URL}/agendamentos`).then(res => res.json());
        const despesas = await fetchWithAuth(`${API_BASE_URL}/despesas`).then(res => res.json());
        const monthlyData = processFinancialData(agendamentos, despesas);
        renderDashboardChart(monthlyData);
    } catch (error) {
        console.error("Erro ao carregar dados do dashboard:", error);
    }
}

async function loadPacientes() {
    console.log("Carregando pacientes...");
    const tabelaBody = document.getElementById('tabela-pacientes-body');
    renderTable(`${API_BASE_URL}/pacientes`, tabelaBody, (paciente) => `
        <td>${paciente.nomeCompleto}</td>
        <td>${paciente.telefone || 'N/A'}</td>
        <td>${paciente.email || 'N/A'}</td>
        <td class="actions">
            <button class="btn-icon" onclick="editEntity('paciente', ${paciente.id})"><i class="fas fa-edit"></i></button>
            <button class="btn-icon btn-danger" onclick="deleteEntity('pacientes', ${paciente.id})"><i class="fas fa-trash"></i></button>
        </td>
    `, 4, "Nenhum paciente cadastrado.");
}

async function loadAgenda() {
    console.log("Carregando Agenda...");
    if (calendar) {
        setTimeout(() => {
            calendar.updateSize();
        }, 10);
        calendar.refetchEvents();
    }
}

async function loadHorarios() {
    console.log("Carregando Horários...");
    const tabelaBody = document.getElementById('tabela-horarios-body');
    renderTable(`${API_BASE_URL}/horarios-trabalho`, tabelaBody, (h) => `
        <td>${h.diaSemana}</td>
        <td>${h.horaInicio}</td>
        <td>${h.horaFim}</td>
        <td class="actions">
            <button class="btn-icon" onclick="editEntity('horario', ${h.id})"><i class="fas fa-edit"></i></button>
            <button class="btn-icon btn-danger" onclick="deleteEntity('horarios-trabalho', ${h.id})"><i class="fas fa-trash"></i></button>
        </td>
    `, 4, "Nenhum horário cadastrado.");
}

async function loadDespesas() {
    console.log("Carregando Despesas...");
    const tabelaBody = document.getElementById('tabela-despesas-body');
    renderTable(`${API_BASE_URL}/despesas`, tabelaBody, (d) => `
        <td>${new Date(d.dataDespesa).toLocaleDateString('pt-BR', {timeZone: 'UTC'})}</td>
        <td>${d.descricao}</td>
        <td>${d.categoria || 'N/A'}</td>
        <td>${formatCurrency(d.valor)}</td>
        <td class="actions">
            <button class="btn-icon" onclick="editEntity('despesa', ${d.id})"><i class="fas fa-edit"></i></button>
            <button class="btn-icon btn-danger" onclick="deleteEntity('despesas', ${d.id})"><i class="fas fa-trash"></i></button>
        </td>
    `, 5, "Nenhuma despesa lançada.");

    if (despesasCalendar) {
        despesasCalendar.refetchEvents();
    }
}

async function loadFinanceiro() { console.log("Carregando Financeiro..."); }
async function loadSolicitacoesnf() { console.log("Carregando Solicitações de NF..."); }

// =================================================
// LÓGICA DE NEGÓCIO E HELPERS
// =================================================

function initializeNavigation(navLinks, contentSections) {
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            if (link.id === 'logout-btn') return;
            const targetId = link.getAttribute('data-target');
            navLinks.forEach(navLink => navLink.classList.remove('active'));
            link.classList.add('active');
            contentSections.forEach(section => section.classList.toggle('active', section.id === targetId));
            const loadFunction = window[`load${capitalizeFirstLetter(targetId)}`];
            if (typeof loadFunction === 'function') loadFunction();
        });
    });
}

function initializeModalClosers() {
    document.querySelectorAll('.close-btn').forEach(button => {
        button.addEventListener('click', () => button.closest('.modal').classList.remove('active'));
    });
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) e.target.classList.remove('active');
    });
}

function initializePacienteListeners() {
    document.getElementById('btn-novo-paciente').addEventListener('click', () => openModal('paciente'));
    document.getElementById('paciente-form').addEventListener('submit', (e) => handleFormSubmit(e, 'paciente', 'pacientes'));
}

function initializeDespesaListeners(despesasCalendarEl) {
    document.getElementById('btn-nova-despesa').addEventListener('click', () => openModal('despesa'));
    document.getElementById('despesa-form').addEventListener('submit', (e) => handleFormSubmit(e, 'despesa', 'despesas'));

    const toggleButton = document.getElementById('btn-toggle-despesa-view');
    const tableView = document.getElementById('despesas-table-view');
    const calendarView = document.getElementById('despesas-calendar-view');

    toggleButton.addEventListener('click', () => {
        const isCalendarActive = calendarView.classList.toggle('active');
        tableView.classList.toggle('active', !isCalendarActive);

        if (isCalendarActive) {
            toggleButton.innerHTML = '<i class="fas fa-list"></i> Ver Lista';
            if (!despesasCalendar) {
                despesasCalendar = new FullCalendar.Calendar(despesasCalendarEl, {
                    initialView: 'dayGridMonth',
                    locale: 'pt-br',
                    buttonText: { today: 'Hoje', month: 'Mês', week: 'Semana', day: 'Dia', list: 'Lista'},
                    headerToolbar: {
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,listWeek'
                    },
                    events: async function(fetchInfo, successCallback, failureCallback) {
                        try {
                            const response = await fetchWithAuth(`${API_BASE_URL}/despesas`);
                            if (!response.ok) throw new Error('Falha ao buscar despesas');
                            const despesas = await response.json();
                            const formattedEvents = despesas.map(d => {
                                const color = d.status === 'Pago' ? '#27ae60' : '#f39c12';
                                return {
                                    id: d.id,
                                    title: `${d.descricao} (${formatCurrency(d.valor)})`,
                                    start: d.dataDespesa,
                                    allDay: true,
                                    color: color
                                };
                            });
                            successCallback(formattedEvents);
                        } catch (error) {
                            console.error('Erro ao carregar despesas no calendário:', error);
                            failureCallback(error);
                        }
                    },
                    eventClick: (info) => editEntity('despesa', info.event.id),
                });
                despesasCalendar.render();
            } else {
                despesasCalendar.refetchEvents();
            }
        } else {
            toggleButton.innerHTML = '<i class="fas fa-calendar-alt"></i> Ver Calendário';
        }
    });
}

function initializeAgendamentoListeners(calendarEl) {
    document.getElementById('btn-novo-agendamento').addEventListener('click', () => openModal('agendamento'));
    document.getElementById('agendamento-form').addEventListener('submit', (e) => handleFormSubmit(e, 'agendamento', 'agendamentos'));
    document.getElementById('btn-deletar-agendamento').addEventListener('click', () => {
        const id = document.getElementById('agendamento-id').value;
        if(id) deleteEntity('agendamentos', id);
    });

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'pt-br',
        buttonText: { today: 'Hoje', month: 'Mês', week: 'Semana', day: 'Dia' },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        allDaySlot: false,
        slotMinTime: "08:00:00",
        slotMaxTime: "20:00:00",
        selectable: true,
        selectConstraint: "businessHours",
        eventSources: [
            {
                events: async function(fetchInfo, successCallback, failureCallback) {
                    try {
                        const token = localStorage.getItem('userToken');
                        if (!token) { successCallback([]); return; }

                        const [agendamentosRes, pacientesRes, horariosRes] = await Promise.all([
                            fetchWithAuth(`${API_BASE_URL}/agendamentos`),
                            fetchWithAuth(`${API_BASE_URL}/pacientes`),
                            fetchWithAuth(`${API_BASE_URL}/horarios-trabalho`)
                        ]);

                        if (!agendamentosRes.ok || !pacientesRes.ok || !horariosRes.ok) throw new Error('Falha ao buscar dados da agenda');
                        
                        const agendamentos = await agendamentosRes.json();
                        const pacientes = await pacientesRes.json();
                        const horarios = await horariosRes.json();
                        const pacientesMap = new Map(pacientes.map(p => [p.id, p.nomeCompleto]));

                        const formattedEvents = agendamentos.map(e => {
                            let color;
                            switch(e.status) {
                                case 'Pago': color = '#27ae60'; break;
                                case 'Cancelado': color = '#C44865'; break;
                                case 'Em_Aberto': default: color = '#5F8979';
                            }
                            
                            const startDate = new Date(e.dataHora);
                            const endDate = new Date(startDate.getTime() + 60 * 60 * 1000);

                            return {
                                id: e.id,
                                title: pacientesMap.get(e.pacienteId) || 'Paciente não encontrado',
                                start: startDate,
                                end: endDate,
                                color: color,
                                display: 'block'
                            };
                        });

                        const weekDaysMap = { 'Domingo': 0, 'Segunda': 1, 'Terca': 2, 'Quarta': 3, 'Quinta': 4, 'Sexta': 5, 'Sabado': 6 };
                        const businessHours = horarios.map(h => ({
                            daysOfWeek: [weekDaysMap[h.diaSemana]],
                            startTime: h.horaInicio,
                            endTime: h.horaFim
                        }));
                        
                        calendar.setOption('businessHours', businessHours);
                        
                        successCallback(formattedEvents);
                    } catch (error) {
                        console.error('Erro ao carregar eventos do calendário:', error);
                        failureCallback(error);
                    }
                }
            }
        ],
        eventClick: (info) => editEntity('agendamento', info.event.id),
        select: function(info) {
            const data = info.startStr.slice(0, 10);
            const hora = info.startStr.slice(11, 16);
            openModal('agendamento', { data, hora });
        }
    });
    calendar.render();
}

function initializeHorarioListeners() {
    document.getElementById('btn-novo-horario').addEventListener('click', () => openModal('horario'));
    document.getElementById('horario-form').addEventListener('submit', (e) => handleFormSubmit(e, 'horario', 'horarios-trabalho'));
}

async function editEntity(entitySingular, id) {
    const entityPlural = entitySingular === 'horario' ? 'horarios-trabalho' : `${entitySingular}s`;
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/${entityPlural}/${id}`);
        if (!response.ok) throw new Error('Entidade não encontrada.');
        const entityData = await response.json();
        populateForm(entitySingular, entityData);
    } catch (error) {
        console.error(`Erro ao buscar ${entitySingular}:`, error);
        alert('Não foi possível carregar os dados para edição.');
    }
}

async function deleteEntity(entityPlural, id) {
    if (confirm(`Tem certeza de que deseja excluir este item?`)) {
        try {
            const response = await fetchWithAuth(`${API_BASE_URL}/${entityPlural}/${id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('Falha ao excluir.');
            alert('Item excluído com sucesso!');
            const loadFunctionName = `load${capitalizeFirstLetter(entityPlural.replace('-trabalho', ''))}`;
            if (typeof window[loadFunctionName] === 'function') {
                window[loadFunctionName]();
            }
        } catch (error) {
            console.error(`Erro ao excluir ${entityPlural}:`, error);
            alert('Ocorreu um erro ao excluir.');
        }
    }
}

async function handleFormSubmit(event, entitySingular, entityPlural) {
    event.preventDefault();
    const form = event.target;
    const id = form.querySelector(`#${entitySingular}-id`).value;
    const url = `${API_BASE_URL}/${entityPlural}` + (id ? `/${id}` : '');
    const method = id ? 'PUT' : 'POST';

    if (entitySingular === 'despesa') {
        const despesaData = getFormData(entitySingular, form);
        const formData = new FormData();
        formData.append('despesa', new Blob([JSON.stringify(despesaData)], { type: 'application/json' }));
        
        const files = form.querySelector('#despesa-anexo').files;
        for (let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }

        try {
            const token = localStorage.getItem('userToken');
            const response = await fetch(url, {
                method: method,
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });
            if (!response.ok) throw new Error('Erro ao salvar despesa com ficheiros.');
            
            document.getElementById('despesa-modal').classList.remove('active');
            loadDespesas();
            alert('Despesa salva com sucesso!');
        } catch (error) {
            console.error('Erro:', error);
            alert('Não foi possível salvar a despesa.');
        }
        return;
    }

    const formData = getFormData(entitySingular, form);
    try {
        const response = await fetchWithAuth(url, {
            method: method,
            body: JSON.stringify(formData)
        });
        if (!response.ok) throw new Error(`Erro ao salvar ${entitySingular}.`);
        
        document.getElementById(`${entitySingular}-modal`).classList.remove('active');
        const loadFunctionName = `load${capitalizeFirstLetter(entityPlural.replace('-trabalho', ''))}`;
        if (typeof window[loadFunctionName] === 'function') {
            window[loadFunctionName]();
        }
        alert(`${capitalizeFirstLetter(entitySingular)} ${id ? 'atualizado(a)' : 'salvo(a)'} com sucesso!`);
    } catch (error) {
        console.error('Erro:', error);
        alert(`Não foi possível salvar. Verifique o console.`);
    }
}

function openModal(entityName, defaultValues = {}) {
    const form = document.getElementById(`${entityName}-form`);
    form.reset();
    document.getElementById(`${entityName}-id`).value = '';
    document.getElementById(`${entityName}-modal-title`).textContent = `Novo ${capitalizeFirstLetter(entityName)}`;
    for (const key in defaultValues) {
        document.getElementById(`${entityName}-${key}`).value = defaultValues[key];
    }
    if (entityName === 'agendamento') {
        document.getElementById('btn-deletar-agendamento').style.display = 'none';
        populatePacientesSelect();
    }
    document.getElementById(`${entityName}-modal`).classList.add('active');
}

function getFormData(entityName, form) {
    const rawData = {};
    const formElements = form.elements;
    for (const el of formElements) {
        if (el.id && el.type !== 'submit' && el.type !== 'button') {
            const key = el.id.replace(`${entityName}-`, '');
            if (key !== 'id') rawData[key] = el.value;
        }
    }

    if (entityName === 'paciente') {
        return {
            nomeCompleto: rawData.nome,
            dataNascimento: rawData['data-nascimento'],
            cpf: rawData.cpf,
            telefone: rawData.telefone,
            email: rawData['email-paciente'],
            endereco: rawData.endereco,
            nomeResponsavel: rawData['nome-responsavel'],
            cpfResponsavel: rawData['cpf-responsavel']
        };
    }
    if (entityName === 'horario') {
        return {
            diaSemana: rawData.dia,
            horaInicio: rawData.inicio,
            horaFim: rawData.fim
        };
    }
    if (entityName === 'despesa') {
        return {
            descricao: rawData.descricao,
            valor: rawData.valor,
            dataDespesa: rawData.data,
            categoria: rawData.categoria,
            status: rawData.status
        };
    }
    if (entityName === 'agendamento') {
        return {
            pacienteId: rawData.paciente,
            dataHora: `${rawData.data}T${rawData.hora}:00`,
            valor: rawData.valor,
            status: rawData.status,
            observacoes: rawData.obs
        };
    }
    return rawData;
}

function populateForm(entityName, data) {
    document.getElementById(`${entityName}-modal-title`).textContent = `Editar ${capitalizeFirstLetter(entityName)}`;
    document.getElementById(`${entityName}-id`).value = data.id;

    const form = document.getElementById(`${entityName}-form`);
    
    if (form.querySelector('input[type="file"]')) {
        form.querySelector('input[type="file"]').value = '';
    }
    if (document.getElementById(`${entityName}-anexos-list`)) {
        document.getElementById(`${entityName}-anexos-list`).innerHTML = '';
        document.getElementById(`${entityName}-anexos-list-container`).style.display = 'none';
    }

    if (entityName === 'paciente') {
        form.querySelector('#nome').value = data.nomeCompleto || '';
        form.querySelector('#data-nascimento').value = data.dataNascimento || '';
        form.querySelector('#cpf').value = data.cpf || '';
        form.querySelector('#telefone').value = data.telefone || '';
        form.querySelector('#email-paciente').value = data.email || '';
        form.querySelector('#endereco').value = data.endereco || '';
        form.querySelector('#nome-responsavel').value = data.nomeResponsavel || '';
        form.querySelector('#cpf-responsavel').value = data.cpfResponsavel || '';
    } else if (entityName === 'despesa') {
        form.querySelector('#despesa-descricao').value = data.descricao || '';
        form.querySelector('#despesa-valor').value = data.valor || '';
        form.querySelector('#despesa-data').value = data.dataDespesa || '';
        form.querySelector('#despesa-categoria').value = data.categoria || '';
        form.querySelector('#despesa-status').value = data.status || 'Aguardando';

        if (data.anexos && data.anexos.length > 0) {
            const listContainer = document.getElementById('despesa-anexos-list-container');
            const list = document.getElementById('despesa-anexos-list');
            data.anexos.forEach(anexo => {
                const li = document.createElement('li');
                const a = document.createElement('a');
                a.href = `${API_BASE_URL}/despesas/anexos/${anexo.filePath}`;
                a.textContent = anexo.fileName;
                a.target = '_blank';
                li.appendChild(a);
                list.appendChild(li);
            });
            listContainer.style.display = 'block';
        }
    } else if (entityName === 'agendamento') {
        form.querySelector('#agendamento-data').value = data.dataHora.slice(0, 10);
        form.querySelector('#agendamento-hora').value = data.dataHora.slice(11, 16);
        populatePacientesSelect(data.pacienteId);
        form.querySelector('#agendamento-valor').value = data.valor || '';
        form.querySelector('#agendamento-status').value = data.status || 'Em_Aberto';
        form.querySelector('#agendamento-obs').value = data.observacoes || '';
        document.getElementById('btn-deletar-agendamento').style.display = 'inline-block';
    } else if (entityName === 'horario') {
        form.querySelector('#horario-dia').value = data.diaSemana || '';
        form.querySelector('#horario-inicio').value = data.horaInicio || '';
        form.querySelector('#horario-fim').value = data.horaFim || '';
    }

    document.getElementById(`${entityName}-modal`).classList.add('active');
}

async function populatePacientesSelect(selectedId = null) {
    const select = document.getElementById('agendamento-paciente');
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/pacientes`);
        const pacientes = await response.json();
        select.innerHTML = '<option value="">Selecione um paciente</option>';
        pacientes.forEach(p => {
            const option = new Option(p.nomeCompleto, p.id);
            if (p.id === selectedId) option.selected = true;
            select.add(option);
        });
    } catch (error) {
        console.error("Erro ao carregar pacientes no select:", error);
    }
}

async function renderTable(url, tableBody, rowTemplate, colSpan, emptyMessage) {
    try {
        const response = await fetchWithAuth(url);
        if (!response.ok) throw new Error(`Erro ao buscar dados de ${url}`);
        const data = await response.json();
        tableBody.innerHTML = '';
        if (data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="${colSpan}">${emptyMessage}</td></tr>`;
            return;
        }
        data.forEach(item => {
            const tr = document.createElement('tr');
            tr.innerHTML = rowTemplate(item);
            tableBody.appendChild(tr);
        });
    } catch (error) {
        console.error(`Erro ao carregar dados de ${url}:`, error);
        tableBody.innerHTML = `<tr><td colspan="${colSpan}">Erro ao carregar dados.</td></tr>`;
    }
}

function processFinancialData(agendamentos, despesas) {
    const data = {};
    const addValue = (month, type, value) => {
        if (!data[month]) data[month] = { receitas: 0, despesas: 0 };
        data[month][type] += value;
    };
    agendamentos.filter(a => a.status === 'Pago').forEach(a => {
        const month = new Date(a.dataHora).toISOString().slice(0, 7);
        addValue(month, 'receitas', a.valor);
    });
    despesas.forEach(d => {
        const month = new Date(d.dataDespesa).toISOString().slice(0, 7);
        addValue(month, 'despesas', d.valor);
    });
    return Object.keys(data).sort().map(month => ({
        month, receitas: data[month].receitas, despesas: data[month].despesas
    }));
}

function renderDashboardChart(monthlyData) {
    const ctx = document.getElementById('dashboard-chart').getContext('2d');
    if (dashboardChart) dashboardChart.destroy();
    dashboardChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: monthlyData.map(d => d.month),
            datasets: [
                { label: 'Receitas (R$)', data: monthlyData.map(d => d.receitas), backgroundColor: 'rgba(95, 137, 121, 0.7)' },
                { label: 'Despesas (R$)', data: monthlyData.map(d => d.despesas), backgroundColor: 'rgba(196, 72, 101, 0.7)' }
            ]
        },
        options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true } } }
    });
}

function formatCurrency(value) {
    if(typeof value !== 'number') return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

function capitalizeFirstLetter(string) {
    if (!string) return '';
    return string.charAt(0).toUpperCase() + string.slice(1);
}