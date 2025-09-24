// Configurações
const CONFIG = {
    GEOCODER_ZOOM: 15
};

// Variáveis globais
let map;
let currentRoutes = L.layerGroup();
let allLines = [];

// Cores para direções
const DIRECTION_COLORS = {
    '0': '#e74c3c', // Ida - Vermelho
    '1': '#27ae60', // Volta - Verde
    'default': '#3498db' // Azul padrão
};

// Inicializa o mapa
function initializeMap() {
    map = L.map('map').setView([-22.908333, -43.196388], 11);

    // Mapa base
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
    }).addTo(map);

    currentRoutes.addTo(map);
    return map;
}

// Carrega a lista de linhas disponíveis
async function loadAvailableLines() {
    try {
        showLoading(true, 'Carregando lista de linhas...');
        const response = await fetch('/api/itineraries/metadata/lines');

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        allLines = await response.json();

        // Verificar se allLines é um array válido
        if (!Array.isArray(allLines)) {
            throw new Error('Resposta inválida do servidor');
        }

        populateLineSelector();
        // REMOVIDO: updateStats() não existe mais
        updateRouteInfo([], 0); // Inicializa as estatísticas com zero

    } catch (error) {
        console.error('Erro ao carregar linhas:', error);
        alert('Erro ao carregar lista de linhas: ' + error.message);
    } finally {
        showLoading(false);
    }
}

// Preenche o seletor de linhas
function populateLineSelector() {
    const selector = $('#lineSelector');

    // Limpar opções existentes
    selector.empty();

    // Adicionar opção padrão
    selector.append($('<option></option>').attr('value', '').text('Selecione as linhas'));

    // Adicionar linhas
    allLines.forEach(line => {
        if (line && line.trim() !== '') {
            selector.append($('<option></option>').attr('value', line).text(line));
        }
    });

    // Inicializar Select2
    selector.select2({
        placeholder: "Selecione uma ou mais linhas",
        allowClear: true,
        width: '100%',
        maximumSelectionLength: 10
    });

    // Habilitar o botão de carregar rotas
    $('#loadRoutes').prop('disabled', false);
}

// Carrega as rotas baseado nas linhas selecionadas
async function loadRoutes() {
    const selectedLines = $('#lineSelector').val();

    if (!selectedLines || selectedLines.length === 0) {
        alert('Por favor, selecione pelo menos uma linha.');
        return;
    }

    try {
        showLoading(true, 'Carregando rotas...');

        // Construir URL com parâmetros
        const linesParam = selectedLines.join(',');
        const url = `/api/itineraries/by-lines?lines=${encodeURIComponent(linesParam)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const data = await response.json();

        // Verificar se a resposta é válida
        if (!data || !Array.isArray(data.features)) {
            throw new Error('Resposta inválida do servidor');
        }

        // Limpar rotas anteriores
        currentRoutes.clearLayers();

        // Adicionar novas rotas
        if (data.features.length > 0) {
            data.features.forEach(feature => {
                const routeLine = createRouteLine(feature);
                if (routeLine) {
                    currentRoutes.addLayer(routeLine);
                }
            });

            // Ajustar mapa para mostrar todas as rotas
            const layers = currentRoutes.getLayers();
            if (layers.length > 0) {
                const group = L.featureGroup(layers);
                map.fitBounds(group.getBounds());
            }

            updateRouteInfo(selectedLines, data.features.length);
        } else {
            updateRouteInfo(selectedLines, 0);
            alert('Nenhuma rota encontrada para as linhas selecionadas.');
        }

    } catch (error) {
        console.error('Erro ao carregar rotas:', error);
        alert('Erro ao carregar rotas: ' + error.message);
    } finally {
        showLoading(false);
    }
}

// Cria uma linha de rota no mapa
function createRouteLine(feature) {
    if (!feature.geometry || !feature.geometry.coordinates) {
        return null;
    }

    const coordinates = feature.geometry.coordinates.map(coord => [coord[1], coord[0]]);
    const properties = feature.properties;

    // Determinar cor baseada na direção
    const direction = properties.direcao?.toString() || 'default';
    const color = DIRECTION_COLORS[direction] || DIRECTION_COLORS.default;

    const polyline = L.polyline(coordinates, {
        color: color,
        weight: 4,
        opacity: 0.7,
        className: 'route-line'
    });

    // Popup informativo
    const popupContent = `
        <div class="route-popup">
            <h4>Linha ${properties.servico || 'N/A'}</h4>
            <div class="detail"><strong>Destino:</strong> ${properties.destino || 'N/A'}</div>
            <div class="detail"><strong>Consórcio:</strong> ${properties.consorcio || 'N/A'}</div>
            <div class="detail"><strong>Tipo:</strong> ${properties.tipo_rota || 'N/A'}</div>
            <div class="detail"><strong>Direção:</strong> ${direction === '0' ? 'Ida' : 'Volta'}</div>
            <div class="detail"><strong>Extensão:</strong> ${properties.extensao ? (properties.extensao / 1000).toFixed(2) + ' km' : 'N/A'}</div>
        </div>
    `;

    polyline.bindPopup(popupContent);

    // Evento de clique para mostrar informações detalhadas
    polyline.on('click', function() {
        showRouteDetails(properties);
    });

    return polyline;
}

// Mostra detalhes da rota no painel lateral
function showRouteDetails(properties) {
    const routeInfo = document.getElementById('routeInfo');
    routeInfo.innerHTML = `
        <div class="route-details">
            <h4>Linha ${properties.servico || 'N/A'}</h4>
            <p><strong>Destino:</strong> ${properties.destino || 'N/A'}</p>
            <p><strong>Consórcio:</strong> ${properties.consorcio || 'N/A'}</p>
            <p><strong>Tipo de Rota:</strong> ${properties.tipo_rota || 'N/A'}</p>
            <p><strong>Direção:</strong> ${properties.direcao === 0 ? 'Ida' : 'Volta'}</p>
            <p><strong>Extensão:</strong> ${properties.extensao ? (properties.extensao / 1000).toFixed(2) + ' km' : 'N/A'}</p>
            <p><strong>Shape ID:</strong> ${properties.shape_id || 'N/A'}</p>
            ${properties.descricao_desvio ? `<p><strong>Observação:</strong> ${properties.descricao_desvio}</p>` : ''}
        </div>
    `;
}

// Atualiza informações no painel de estatísticas
function updateRouteInfo(selectedLines, routesCount) {
    document.getElementById('linesCount').textContent = `Linhas selecionadas: ${selectedLines.length}`;
    document.getElementById('routesCount').textContent = `Rotas exibidas: ${routesCount}`;
}

// Limpa o mapa
function clearMap() {
    currentRoutes.clearLayers();
    document.getElementById('routeInfo').innerHTML = '<p>Selecione uma linha e clique em "Carregar Rotas" para visualizar</p>';
    updateRouteInfo([], 0);
    $('#lineSelector').val(null).trigger('change');
}

// Controle de loading
function showLoading(show, message = 'Carregando...') {
    const overlay = document.getElementById('loadingOverlay');
    const messageElement = overlay.querySelector('p');

    messageElement.textContent = message;
    overlay.style.display = show ? 'flex' : 'none';

    // Desabilitar/habilitar botões durante o loading
    $('#loadRoutes').prop('disabled', show);
    $('#clearRoutes').prop('disabled', show);
}

// Filtra as rotas baseado nos filtros secundários
function applyFilters() {
    const consortiumFilter = document.getElementById('consortiumFilter').value;
    const directionFilter = document.getElementById('directionFilter').value;

    // Se houver rotas carregadas, aplicar filtros
    if (currentRoutes.getLayers().length > 0) {
        // Por enquanto, vamos recarregar as rotas com os filtros
        // Em uma versão futura, podemos filtrar client-side
        loadRoutes();
    }
}

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    // Verificar se jQuery está carregado
    if (typeof jQuery === 'undefined') {
        console.error('jQuery não está carregado. Usando fallback.');
        // Usar fallback sem jQuery
        document.getElementById('loadRoutes').addEventListener('click', function() {
            alert('jQuery não carregado. Recarregue a página.');
        });
        return;
    }

    // Inicializar mapa
    initializeMap();

    // Carregar lista de linhas
    loadAvailableLines();

    // Event listeners
    document.getElementById('loadRoutes').addEventListener('click', loadRoutes);
    document.getElementById('clearRoutes').addEventListener('click', clearMap);

    // Filtros secundários
    document.getElementById('consortiumFilter').addEventListener('change', applyFilters);
    document.getElementById('directionFilter').addEventListener('change', applyFilters);

    // Auto-carregar quando selecionar linhas (opcional)
    $('#lineSelector').on('change', function() {
        const selectedLines = $(this).val();
        if (selectedLines && selectedLines.length > 0) {
            // Opcional: auto-carregar quando selecionar linhas
            // loadRoutes();
        }
    });

    // Desabilitar botões inicialmente
    $('#loadRoutes').prop('disabled', true);
});