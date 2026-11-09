let token = localStorage.getItem('agroToken');
const $ = id => document.getElementById(id);
function authHeaders() { return { 'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json' }; }
async function login() { $('loginError').textContent = ''; try {
    const r = await fetch('/api/auth/token', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username: $('username').value, password: $('password').value }) });
    if (!r.ok)
        throw new Error('Usuário ou senha inválidos');
    const d = await r.json();
    token = d.token;
    localStorage.setItem('agroToken', token);
    showApp();
    await refresh();
}
catch (e) {
    $('loginError').textContent = e.message;
} }
function showApp() { $('login').hidden = !!token; $('app').hidden = !token; $('connection').textContent = token ? 'Autenticado' : 'Desconectado'; }
async function get(path) { const r = await fetch(path, { headers: authHeaders() }); if (r.status === 401 || r.status === 403) {
    logout();
    throw new Error('Sessão expirada. Entre novamente.');
} if (!r.ok)
    throw new Error('Falha ao consultar ' + path); return r.json(); }
function date(v) { return v ? new Date(v).toLocaleString('pt-BR') : '—'; }
async function refresh() { try {
    const [d, s, a, readings] = await Promise.all([get('/api/dashboard'), get('/api/sensors'), get('/api/alerts?limit=10'), get('/api/readings?limit=15')]);
    $('totalReadings').textContent = d.totalReadings;
    $('activeSensors').textContent = d.activeSensors;
    $('criticalReadings').textContent = d.criticalReadings;
    $('sensorRows').innerHTML = s.map(x => `<tr><td>${x.sensorId}</td><td>${x.type}</td><td>${Number(x.value).toFixed(2)} ${x.unit || ''}</td><td><span class="status ${x.status}">${x.status || 'NORMAL'}</span></td><td>${date(x.timestamp)}</td></tr>`).join('');
    $('alerts').innerHTML = a.length ? a.map(x => `<div class="alert"><b>${x.sensorId}</b> · ${x.message} · valor ${x.value} · ${date(x.timestamp)}</div>`).join('') : '<p class="muted">Nenhum alerta registrado.</p>';
    $('readingRows').innerHTML = readings.map(x => `<tr><td>${x.sensorId}</td><td>${x.type}</td><td>${Number(x.value).toFixed(2)} ${x.unit || ''}</td><td>${date(x.timestamp)}</td></tr>`).join('');
    $('updated').textContent = 'Atualizado às ' + new Date().toLocaleTimeString('pt-BR');
    $('connection').textContent = 'Conectado';
}
catch (e) {
    $('updated').textContent = e.message;
} }
function logout() { token = null; localStorage.removeItem('agroToken'); showApp(); }
$('loginBtn').addEventListener('click', login);
$('refreshBtn').addEventListener('click', refresh);
$('logoutBtn').addEventListener('click', logout);
showApp();
if (token)
    refresh();
setInterval(() => { if (token && !$('app').hidden)
    refresh(); }, 5000);
