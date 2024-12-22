// Estado do jogo
let gameState = {
    selectedFighter: null,
    selectedPower: null,
    selectedTarget: null,
    isMyTurn: false,
    stompClient: null
};

// Conectar ao WebSocket
function connectWebSocket() {
    console.log('Iniciando conexão WebSocket...');
    const socket = new SockJS('/ws');
    gameState.stompClient = Stomp.over(socket);

    gameState.stompClient.connect({}, function(frame) {
        console.log('WebSocket Connected:', frame);
        const gameId = document.getElementById('game-status').dataset.gameId;
        const currentUsername = document.getElementById('current-player-username').textContent;

        gameState.stompClient.subscribe('/topic/game', function(response) {
            const gameUpdate = JSON.parse(response.body);
            // Só atualiza se for para este jogo
            if (gameUpdate.gameId.toString() === gameId) {
                handleGameUpdate(gameUpdate, currentUsername);
            }
        });
    });
}



// Seleção de Fighter
function selectFighter(element) {
    // Remove seleção anterior
    document.querySelectorAll('.fighter-card').forEach(card => {
        card.classList.remove('border-blue-500', 'border-2');
        card.querySelector('.fighter-select-status').classList.add('hidden');
    });

    // Adiciona seleção ao fighter atual
    element.classList.add('border-blue-500', 'border-2');
    element.querySelector('.fighter-select-status').classList.remove('hidden');

    gameState.selectedFighter = {
        id: element.dataset.fighterId,
        points: parseInt(element.dataset.points)
    };

    // Habilita powers válidos
    updateAvailablePowers();
}

// Seleção de Power
function selectPower(element) {
    if (!gameState.selectedFighter || element.classList.contains('disabled')) {
        return;
    }

    // Remove seleção anterior
    document.querySelectorAll('.power-card').forEach(card => {
        card.classList.remove('border-yellow-500', 'border-2');
    });

    // Adiciona seleção ao power atual
    element.classList.add('border-yellow-500', 'border-2');

    gameState.selectedPower = {
        id: element.dataset.powerId,
        value: parseInt(element.dataset.powerValue)
    };

    // Habilita seleção de alvo
    enableTargetSelection();
}

// Seleção de Alvo
function selectTarget(element) {
    if (!gameState.selectedFighter || !gameState.selectedPower || !element.dataset.active) {
        return;
    }

    // Remove seleção anterior
    document.querySelectorAll('.opponent-fighter').forEach(card => {
        card.classList.remove('border-red-500', 'border-2');
    });

    // Adiciona seleção ao alvo atual
    element.classList.add('border-red-500', 'border-2');

    gameState.selectedTarget = {
        id: element.dataset.fighterId
    };

    // Habilita botão de ataque
    enableAttackButton();
}

// Atualiza powers disponíveis baseado no fighter selecionado
function updateAvailablePowers() {
    const fighterPoints = gameState.selectedFighter?.points || 0;

    document.querySelectorAll('.power-card').forEach(power => {
        const powerValue = parseInt(power.dataset.powerValue);
        if (powerValue > fighterPoints) {
            power.classList.add('disabled', 'opacity-50', 'cursor-not-allowed');
        } else {
            power.classList.remove('disabled', 'opacity-50', 'cursor-not-allowed');
        }
    });
}

// Habilita seleção de alvo
function enableTargetSelection() {
    document.querySelectorAll('.opponent-fighter').forEach(fighter => {
        if (fighter.dataset.active === 'true') {
            fighter.classList.add('cursor-pointer', 'hover:bg-red-100');
        }
    });
}

// Habilita botão de ataque
function enableAttackButton() {
    const attackButton = document.getElementById('attack-button');
    if (attackButton) {
        attackButton.disabled = false;
        attackButton.classList.remove('opacity-50');
    }
}

// Executa ataque
function executeAttack() {
    if (!gameState.selectedFighter || !gameState.selectedPower || !gameState.selectedTarget) {
        console.log("Faltam seleções para o ataque");
        return;
    }

    const gameId = document.getElementById('game-status').dataset.gameId;
    const moveRequest = {
        gameId: gameId,
        attackingFighterId: gameState.selectedFighter.id,
        attackPowerId: gameState.selectedPower.id,
        targetFighterId: gameState.selectedTarget.id
    };

    // Envia movimento via WebSocket
    gameState.stompClient.send("/app/game.move", {}, JSON.stringify(moveRequest));

    // Reseta seleções após o ataque
    resetSelections();

    // Desabilita o botão de ataque
    const attackButton = document.getElementById('attack-button');
    if (attackButton) {
        attackButton.disabled = true;
        attackButton.classList.add('opacity-50');
    }
}

// Reseta seleções
function resetSelections() {
    gameState.selectedFighter = null;
    gameState.selectedPower = null;
    gameState.selectedTarget = null;

    document.querySelectorAll('.fighter-card, .power-card, .opponent-fighter').forEach(element => {
        element.classList.remove('border-blue-500', 'border-2', 'border-yellow-500', 'border-red-500');
    });

    document.querySelectorAll('.fighter-select-status').forEach(element => {
        element.classList.add('hidden');
    });
}

// Atualiza o estado do jogo
function handleGameUpdate(gameUpdate) {
    console.log('Recebido update do jogo:', gameUpdate);

    const currentUsername = document.getElementById('current-player-username').textContent;
    const isMyTurn = gameUpdate.currentTurnUsername === currentUsername;

    // Atualiza área de status
    updateGameStatus(gameUpdate, isMyTurn);

    // Atualiza fighters e powers
    updateGameUI(gameUpdate, currentUsername, isMyTurn);

    // Trata área de defesa
    handleDefenseArea(gameUpdate, isMyTurn);
}

function updateGameStatus(gameUpdate, isMyTurn) {
    const gameStatusArea = document.getElementById('game-status');
    gameStatusArea.dataset.isMyTurn = isMyTurn.toString();

    const turnSpan = document.getElementById('current-turn');
    if (turnSpan) {
        turnSpan.textContent = gameUpdate.currentTurnUsername;
    }

    // Atualiza mensagem de turno
    const turnMessage = document.getElementById('turn-message');
    if (turnMessage) {
        turnMessage.style.display = isMyTurn ? 'block' : 'none';
    }

    // Atualiza status dos fighters
    document.querySelectorAll('.fighter-status').forEach(statusDiv => {
        const fighterId = statusDiv.closest('[data-fighter-id]').dataset.fighterId;
        const fighter = [...gameUpdate.player1Fighters, ...gameUpdate.player2Fighters]
            .find(f => f.id.toString() === fighterId);

        if (fighter) {
            const statusText = statusDiv.querySelector('.status-text');
            if (statusText) {
                statusText.textContent = fighter.active ? 'Active' : 'Defeated';
                statusText.className = `status-text ${fighter.active ? 'text-green-600' : 'text-red-600'}`;
            }
        }
    });
}

function updateGameUI(gameUpdate, currentUsername, isMyTurn) {
    // Determina se sou player1 ou player2
    const isPlayer1 = currentUsername === gameUpdate.player1Username;

    // Determina meus powers e os atualiza
    const myPowers = isPlayer1 ? gameUpdate.player1Powers : gameUpdate.player2Powers;
    const powerContainer = document.querySelector('.grid.grid-cols-8.gap-2:not(#defense-powers)');

    if (powerContainer && myPowers) {
        powerContainer.innerHTML = myPowers
            .filter(power => !power.used)
            .map(power => `
                <div class="p-3 border rounded-lg text-center bg-yellow-50 power-card ${isMyTurn ? '' : 'opacity-50'}"
                     data-power-id="${power.id}"
                     data-power-value="${power.value}"
                     ${isMyTurn ? 'onclick="selectPower(this)"' : ''}>
                    <span class="text-2xl font-bold text-yellow-800">${power.value}</span>
                </div>
            `).join('');
    }

    // Atualiza fighters
    updateFighters(gameUpdate.player1Fighters, gameUpdate.player2Fighters, isMyTurn);

        const hasNoPowers = !myPowers || myPowers.length === 0;

        // Show/hide pass turn button based on powers
        const passTurnButton = document.getElementById('pass-turn-button');
        if (passTurnButton) {
            passTurnButton.style.display = (isMyTurn && hasNoPowers) ? 'inline-block' : 'none';
        }
}

window.passTurn = function() {
    if (!gameState.stompClient) {
        console.error("WebSocket connection not established");
        return;
    }

    const gameId = document.getElementById('game-status').dataset.gameId;

    const passTurnRequest = {
        gameId: parseInt(gameId)
    };

    // Send pass turn via WebSocket
    gameState.stompClient.send("/app/game.pass-turn", {}, JSON.stringify(passTurnRequest));

    // Disable the pass turn button after clicking
    const passTurnButton = document.getElementById('pass-turn-button');
    if (passTurnButton) {
        passTurnButton.disabled = true;
        passTurnButton.classList.add('opacity-50');
    }
}

function handleDefenseArea(gameUpdate, isMyTurn) {
    const existingDefenseArea = document.getElementById('defense-area');

    if (gameUpdate.pendingMove && !isMyTurn) {
        if (!existingDefenseArea) {
            showDefenseArea(gameUpdate.pendingMove);
        }
    } else if (existingDefenseArea) {
        existingDefenseArea.remove();
    }

    if (gameUpdate.message) {
        showGameMessage(gameUpdate.message);
    }
}

function updateFighters(player1Fighters, player2Fighters, isMyTurn) {
    const fighterElements = document.querySelectorAll('.fighter-card, .opponent-fighter');

    fighterElements.forEach(element => {
        const fighterId = element.dataset.fighterId;
        const fighter = [...player1Fighters, ...player2Fighters]
            .find(f => f.id.toString() === fighterId);

        if (fighter) {
            // Atualiza pontos
            const pointsElement = element.querySelector('.points');
            if (pointsElement) {
                pointsElement.textContent = fighter.points;
            }

            // Atualiza status ativo/inativo
            element.dataset.active = fighter.active.toString();
            if (!fighter.active) {
                element.classList.add('bg-gray-50');
                element.classList.remove('bg-blue-50', 'bg-red-50');
            }

            // Atualiza interatividade baseada no turno
            if (isMyTurn) {
                element.classList.remove('pointer-events-none', 'opacity-50');
            } else {
                element.classList.add('pointer-events-none', 'opacity-50');
            }
        }
    });
}

// Atualiza powers
function updatePowers(powers) {
    if (!powers) return;

    // Remove powers usados
    powers.forEach(power => {
        if (power.used) {
            const element = document.querySelector(`[data-power-id="${power.id}"]`);
            if (element) {
                element.remove();
            }
        }
    });
}

// Exibe área de defesa
function showDefenseArea(pendingMove) {
    // Verifica se o jogador atual é o defensor (não é seu turno)
    const gameStatus = document.getElementById('game-status');
    const isMyTurn = gameStatus.dataset.isMyTurn === 'true';

    // Se for meu turno, não sou o defensor
    if (isMyTurn) {
        return;
    }

    // Cria ou atualiza a área de defesa
    let defenseArea = document.getElementById('defense-area');
    if (!defenseArea) {
        defenseArea = document.createElement('div');
        defenseArea.id = 'defense-area';
        defenseArea.className = 'fixed top-0 left-0 w-full bg-red-100 p-4';
        document.body.prepend(defenseArea);
    }

    defenseArea.innerHTML = `
        <div class="container mx-auto">
            <h2 class="text-xl font-bold text-red-800 mb-4">Você está sendo atacado!</h2>
            <div id="pending-move" data-move-id="${pendingMove.id}" class="grid grid-cols-2 gap-4 mb-4">
                <div>
                    <p class="font-bold">Atacante:</p>
                    <div class="p-2 bg-white rounded">
                        <p>Fighter: ${pendingMove.attackingFighter.name}</p>
                        <p>Poder de Ataque: <span class="font-bold text-red-600">${pendingMove.attackPower.value}</span></p>
                    </div>
                </div>
                <div>
                    <p class="font-bold">Alvo:</p>
                    <div class="p-2 bg-white rounded">
                        <p>Fighter: ${pendingMove.targetFighter.name}</p>
                        <p>Pontos atuais: ${pendingMove.targetFighter.points}</p>
                    </div>
                </div>
            </div>
            <div class="mt-6">
                <h3 class="text-lg font-bold">Escolha sua defesa:</h3>
                <div class="grid grid-cols-8 gap-2 mt-2" id="defense-powers">
                    ${Array.from(document.querySelectorAll('.power-card')).map(power => {
                        const powerValue = parseInt(power.dataset.powerValue);
                        if (powerValue <= pendingMove.targetFighter.points) {
                            return `
                                <div class="p-3 border rounded-lg text-center bg-yellow-50 power-card cursor-pointer hover:bg-yellow-100"
                                     data-power-id="${power.dataset.powerId}"
                                     data-power-value="${powerValue}"
                                     onclick="selectDefensePower(this)">
                                    <span class="text-2xl font-bold text-yellow-800">${powerValue}</span>
                                </div>
                            `;
                        }
                        return '';
                    }).join('')}
                </div>
                <div class="mt-4 text-center">
                    <button onclick="executeDefense()" id="defend-button"
                            class="bg-green-600 text-white px-6 py-2 rounded-lg font-bold mr-4 opacity-50 disabled:cursor-not-allowed"
                            disabled>
                        Defender com Power
                    </button>
                    <button onclick="defendWithoutPower()"
                            class="bg-gray-600 text-white px-6 py-2 rounded-lg font-bold">
                        Não Defender
                    </button>
                </div>
            </div>
        </div>
    `;
}

// Para selecionar power de defesa
let selectedDefensePower = null;

function selectDefensePower(element) {
console.log("selectDefensePower")
    document.querySelectorAll('.power-card').forEach(card => {
        card.classList.remove('border-blue-500', 'border-2');
    });

    element.classList.add('border-blue-500', 'border-2');
    selectedDefensePower = {
        id: element.dataset.powerId,
        value: parseInt(element.dataset.powerValue)
    };

    // Tentar obter o botão de várias formas para garantir que o encontramos
    const defendButton = document.querySelector('#defend-button') ||
                        document.querySelector('button[onclick="executeDefense()"]');

    if (defendButton) {
        defendButton.disabled = false;
        defendButton.classList.remove('opacity-50');
        console.log('Botão de defesa habilitado:', defendButton);
    } else {
        console.log('Botão de defesa não encontrado');
    }
    console.log("FIm selectDefensePower")
}

function executeDefense() {
    console.log("Apertou executeDefense")
    if (!selectedDefensePower) {
        console.log("Nenhum power de defesa selecionado");
        return;
    }

    const gameId = document.getElementById('game-status').dataset.gameId;
    const moveId = document.getElementById('pending-move').dataset.moveId;

    const defenseRequest = {
        gameId: gameId,
        moveId: moveId,
        defensePowerId: selectedDefensePower?.id
    };

    // Envia defesa via WebSocket
    gameState.stompClient.send("/app/game.defend", {}, JSON.stringify(defenseRequest));

    // Remove a área de defesa
    const defenseArea = document.getElementById('defense-area');
    if (defenseArea) {
        defenseArea.remove();
    }

    selectedDefensePower = null;
}

function defendWithoutPower() {
console.log("defendWithoutPower")
    const gameId = document.getElementById('game-status').dataset.gameId;
    const moveId = document.getElementById('pending-move').dataset.moveId;

    const defenseRequest = {
        gameId: gameId,
        moveId: moveId,
        defensePowerId: null
    };

    // Envia defesa via WebSocket
    gameState.stompClient.send("/app/game.defend", {}, JSON.stringify(defenseRequest));

    // Remove a área de defesa
    const defenseArea = document.getElementById('defense-area');
    if (defenseArea) {
        defenseArea.remove();
    }
}

// Exibe mensagem do jogo
function showGameMessage(message) {
    const messageElement = document.getElementById('game-message');
    if (messageElement) {
        messageElement.textContent = message;
        messageElement.classList.remove('hidden');
        setTimeout(() => {
            messageElement.classList.add('hidden');
        }, 3000);
    }
}

// Inicializa o jogo quando a página carregar
document.addEventListener('DOMContentLoaded', function() {
    connectWebSocket();
});

// Função auxiliar para pegar o username do jogador atual
function getCurrentUsername() {
    return document.getElementById('current-player-username').textContent;
}