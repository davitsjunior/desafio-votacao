import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const JSON_HEADERS = { headers: { 'Content-Type': 'application/json' } };

export const options = {
    scenarios: {
        votos_concorrentes: {
            executor: 'constant-vus',
            vus: 50,
            duration: '30s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<500'],
    },
};

// O client fake de validação de CPF (Feature 37) recusa aleatoriamente parte dos votos
// (CPF "inválido" ou associado UNABLE_TO_VOTE), retornando 404. Isso é esperado e faz
// parte da regra de negócio — não é falha de carga.
const votosRegistrados = new Counter('votos_registrados');
const votosRecusadosPeloClientCpf = new Counter('votos_recusados_client_cpf');

export function setup() {
    const pautaRes = http.post(`${BASE_URL}/v1/pautas`, JSON.stringify({
        titulo: 'Pauta de teste de carga',
        descricao: 'Gerada pelo script k6 de votos concorrentes',
    }), JSON_HEADERS);

    check(pautaRes, { 'pauta cadastrada (201)': (r) => r.status === 201 });
    const pautaId = pautaRes.json('id');

    const sessaoRes = http.post(`${BASE_URL}/v1/pautas/${pautaId}/sessao`, JSON.stringify({
        duracaoMinutos: 5,
    }), JSON_HEADERS);

    check(sessaoRes, { 'sessão aberta (201)': (r) => r.status === 201 });

    return { pautaId };
}

export default function (data) {
    const cpf = `${__VU}${__ITER}`.padStart(11, '0').slice(-11);
    const opcao = Math.random() < 0.5 ? 'SIM' : 'NAO';

    const res = http.post(`${BASE_URL}/v1/pautas/${data.pautaId}/votos`, JSON.stringify({
        cpf,
        opcao,
    }), JSON_HEADERS);

    if (res.status === 201) {
        votosRegistrados.add(1);
    } else if (res.status === 404) {
        votosRecusadosPeloClientCpf.add(1);
    }

    check(res, {
        'resposta é 201 (voto registrado) ou 404 (recusa esperada do client fake de CPF)':
            (r) => r.status === 201 || r.status === 404,
    });
}

export function teardown(data) {
    const resultado = http.get(`${BASE_URL}/v1/pautas/${data.pautaId}/resultado`);
    console.log(`Resultado final da pauta ${data.pautaId}: ${resultado.body}`);
}
